package polimi.logic.engine;

import com.uppaal.engine.*;
import com.uppaal.engine.connection.BundledConnection;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.EngineSettings;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.QueryResult;
import com.uppaal.model.io2.Problem;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.symbolic.SymbolicState;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class UppaalEngine implements AutoCloseable{

    @Getter
    private final String engineId;

    @Getter
    private final String uppaalHome;
    private final Engine nativeEngine;
    private final AtomicBoolean healthy = new AtomicBoolean(true);
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public UppaalEngine(String uppaalHome) throws UppaalEngineException {
        if (uppaalHome == null || uppaalHome.trim().isEmpty()) {
            throw new UppaalEngineException("uppaalHome cannot be null or empty");
        }

        this.engineId = "engine-" + UUID.randomUUID().toString().substring(0, 8);
        this.uppaalHome = uppaalHome;

        try {
            this.nativeEngine = connectToEngine(uppaalHome);
            configureEngine();
        } catch (Exception e) {
            throw new UppaalEngineException("Failed to create UPPAAL engine: " + engineId, e);
        }
    }

    private Engine connectToEngine(String uppaalHome) throws IOException, EngineException {
        File uppaalDir = new File(uppaalHome);
        if (!uppaalDir.exists()) {
            throw new IOException("UPPAAL directory does not exist: " + uppaalHome);
        }

        Engine engine = new Engine();
        engine.addConnection(new BundledConnection(new BinaryResolution(new File(uppaalHome))));
        engine.connect();
        return engine;
    }

    private void configureEngine() throws UppaalEngineException {
        try {
            EngineOptions engineOptions = nativeEngine.getOptions().get();
            EngineSettings engineSettings = engineOptions.getDefaultSettings();
            engineSettings.setValue("--diagnostic", "2");
            nativeEngine.setOptionSettings(engineSettings);
        } catch (Exception e) {
            throw new UppaalEngineException("Failed to configure engine: " + engineId, e);
        }
    }

    public UppaalSystem compile(Document document) throws UppaalEngineException {
        checkHealthy();

        try {
            ArrayList<Problem> problems = new ArrayList<>();
            UppaalSystem system = nativeEngine.getSystem(document, problems).get();

            validateCompilationProblems(problems);
            return system;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            markUnhealthy();
            throw new UppaalEngineException("Compilation interrupted for engine: " + engineId, e);
        } catch (ExecutionException e) {
            markUnhealthy();
            throw new UppaalEngineException("Compilation failed for engine: " + engineId, e.getCause());
        } catch (Exception e) {
            markUnhealthy();
            throw new UppaalEngineException("Unexpected error during compilation for engine: " + engineId, e);
        }
    }

    public QueryResult query(UppaalSystem system, Query query, QueryFeedback feedback) throws UppaalEngineException {
        checkHealthy();

        if (system == null) {
            throw new UppaalEngineException("System cannot be null");
        }
        if (query == null) {
            throw new UppaalEngineException("Query cannot be null");
        }
        if (feedback == null) {
            throw new UppaalEngineException("QueryFeedback cannot be null");
        }

        try {
            return nativeEngine.query(system, query, feedback).get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            markUnhealthy();
            throw new UppaalEngineException("Query interrupted for engine: " + engineId, e);
        } catch (ExecutionException e) {
            markUnhealthy();
            throw new UppaalEngineException("Query execution failed for engine: " + engineId, e.getCause());
        } catch (Exception e) {
            markUnhealthy();
            throw new UppaalEngineException("Unexpected error during query for engine: " + engineId, e);
        }
    }

    public QueryResult query(UppaalSystem system, SymbolicState state, Query query, QueryFeedback feedback) throws UppaalEngineException {
        checkHealthy();

        if (system == null) {
            throw new UppaalEngineException("System cannot be null");
        }
        if (query == null) {
            throw new UppaalEngineException("Query cannot be null");
        }
        if (feedback == null) {
            throw new UppaalEngineException("QueryFeedback cannot be null");
        }

        try {
            return nativeEngine.query(system, state, query, feedback).get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            markUnhealthy();
            throw new UppaalEngineException("Query interrupted for engine: " + engineId, e);
        } catch (ExecutionException e) {
            markUnhealthy();
            throw new UppaalEngineException("Query execution failed for engine: " + engineId, e.getCause());
        } catch (Exception e) {
            markUnhealthy();
            throw new UppaalEngineException("Unexpected error during query for engine: " + engineId, e);
        }
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            healthy.set(false);

            if (nativeEngine != null) {
                try {
                    nativeEngine.cancel();
                    forceDestroyProcesses();
                    Thread.sleep(1000);
                    nativeEngine.forceDisconnect(2000).get();
                    try {
                        shutdownEngineExecutor();
                    } catch (Exception e) {
                        System.err.println("Warning: Error during engine cleanup " + engineId + ": " + e.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println("Warning: Error disconnecting UPPAAL engine " + engineId + ": " + e.getMessage());
                }
            }
        }
    }

    private void forceDestroyProcesses() {
        try {

            java.lang.reflect.Field stubField = nativeEngine.getClass().getDeclaredField("stub");
            stubField.setAccessible(true);
            Object engineStub = stubField.get(nativeEngine);

            java.lang.reflect.Field connectionsField = engineStub.getClass().getDeclaredField("connections");
            connectionsField.setAccessible(true);
            java.util.Map<String, Object> connections = (java.util.Map<String, Object>) connectionsField.get(engineStub);

            for (Object connection : connections.values()) {
                forceDestroyConnection(connection);
            }

        } catch (Exception e) {
            System.err.println("Could not force destroy processes for engine " + engineId + ": " + e.getMessage());
        }
    }

    private void forceDestroyConnection(Object connection) {
        try {
            String connectionClass = connection.getClass().getSimpleName();

            java.lang.reflect.Field internalField;

            switch (connectionClass) {
                case "BundledConnection": {
                    internalField = connection.getClass().getSuperclass().getDeclaredField("internal");
                    internalField.setAccessible(true);
                    Object commandConnection = internalField.get(connection);
                    forceDestroyCommandConnection(commandConnection);
                    break;
                }
                case "LocalConnection": {
                    internalField = connection.getClass().getDeclaredField("internal");
                    internalField.setAccessible(true);
                    Object commandConnection = internalField.get(connection);
                    forceDestroyCommandConnection(commandConnection);
                    break;
                }
                case "CommandConnection":
                    forceDestroyCommandConnection(connection);
                    break;
            }

        } catch (Exception e) {
            System.err.println("Could not force destroy connection: " + e.getMessage());
        }
    }

    private void forceDestroyCommandConnection(Object commandConnection) {
        try {
            java.lang.reflect.Field processField = commandConnection.getClass()
                    .getDeclaredField("process");
            processField.setAccessible(true);
            Process process = (Process) processField.get(commandConnection);

            if (process != null && process.isAlive()) {

                process.destroy();

                boolean terminated = process.waitFor(2, TimeUnit.SECONDS);
                if (!terminated) {
                    process.destroyForcibly();

                    terminated = process.waitFor(3, TimeUnit.SECONDS);
                    if (!terminated) {
                        System.err.println("⚠️ Process STILL alive after destroyForcibly!");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error in forceDestroyCommandConnection: " + e.getMessage());
        }
    }

    private void shutdownEngineExecutor() {
        try {
            java.lang.reflect.Field executorField = Engine.class.getDeclaredField("executor");
            executorField.setAccessible(true);
            java.util.concurrent.ExecutorService executor = (java.util.concurrent.ExecutorService) executorField.get(nativeEngine);

            if (executor != null && !executor.isShutdown()) {
                executor.shutdownNow();

                if (!executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    System.err.println("UPPAAL executor did not terminate within timeout for engine: " + engineId);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not shutdown UPPAAL internal executor for engine " + engineId + ": " + e.getMessage());
        }
    }

    public boolean isHealthy() {
        return healthy.get() && !closed.get() && nativeEngine != null;
    }

    private void checkHealthy() throws UppaalEngineException {
        if (!isHealthy()) {
            throw new UppaalEngineException("Engine is not healthy: " + engineId);
        }
    }

    private void markUnhealthy() {
        healthy.set(false);
    }

    private void validateCompilationProblems(ArrayList<Problem> problems) throws UppaalEngineException {
        if (problems.isEmpty()) {
            return;
        }

        boolean hasErrors = false;
        StringBuilder errorMessage = new StringBuilder("Compilation problems for engine ").append(engineId).append(":\n");

        for (Problem problem : problems) {
            String type = problem.getType();
            if (!"warning".equals(type)) {
                hasErrors = true;
            }
            errorMessage.append("  ").append(type.toUpperCase()).append(": ").append(problem.toString()).append("\n");
        }

        if (hasErrors) {
            markUnhealthy();
            throw new UppaalEngineException(errorMessage.toString());
        }

        System.out.println("UPPAAL compilation warnings for engine " + engineId + ":\n" + errorMessage);
    }

    @Override
    public String toString() {
        return String.format("UppaalEngine{id='%s', healthy=%s, closed=%s, uppaalHome='%s'}",
                engineId, healthy.get(), closed.get(), uppaalHome);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UppaalEngine that = (UppaalEngine) obj;
        return engineId.equals(that.engineId);
    }

    @Override
    public int hashCode() {
        return engineId.hashCode();
    }

    private void forceProcessCleanup() {
        System.out.println("Attempting OS-level force cleanup of UPPAAL processes for engine: " + engineId);

        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;

            if (os.contains("win")) {
                pb = new ProcessBuilder("taskkill", "/f", "/im", "server.exe");
            } else if (os.contains("mac") || os.contains("nix") || os.contains("nux")) {
                pb = new ProcessBuilder("pkill", "-9", "server");
            } else {
                System.err.println("Unknown OS, cannot force cleanup: " + os);
                return;
            }

            Process killProcess = pb.start();
            boolean finished = killProcess.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);

            if (finished) {
                int exitCode = killProcess.exitValue();
                if (exitCode == 0) {
                    System.out.println("OS-level force cleanup completed for engine: " + engineId);
                } else if (exitCode == 1) {
                    System.out.println("OS-level force cleanup: no processes to kill for engine: " + engineId);
                } else {
                    System.err.println("OS-level force cleanup returned exit code: " + exitCode + " for engine: " + engineId);
                }
            } else {
                System.err.println("OS-level force cleanup timed out for engine: " + engineId);
                killProcess.destroyForcibly();
            }

        } catch (Exception e) {
            System.err.println("Error during OS-level force cleanup for engine " + engineId + ": " + e.getMessage());
        }
    }

    public SymbolicState getInitialState(UppaalSystem system) throws ExecutionException, InterruptedException {
        return nativeEngine.getInitialState(system).get();
    }
}



