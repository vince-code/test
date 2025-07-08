package polimi.logic.engineManagement;

import lombok.Getter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;

public class UppaalEnginePool {

    private static volatile UppaalEnginePool instance;
    private static final Object INIT_LOCK = new Object();

    @Getter
    private final String uppaalHome;
    private volatile int maxInstances;

    private final BlockingQueue<UppaalEngine> availableEngines;
    private final List<UppaalEngine> allEngines;
    private final AtomicInteger currentSize = new AtomicInteger(0);
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);

    public static UppaalEnginePool getInstance() {
        if (instance == null || instance.isShutdown.get()) {
            throw new IllegalStateException("UppaalEnginePool is not initialized or has been shutdown");
        }
        return instance;
    }

    public static UppaalEnginePool initialize(String uppaalHome, int maxInstances) throws UppaalEngineException {
        if (instance == null) {
            synchronized (INIT_LOCK) {
                if (instance == null) {
                    instance = new UppaalEnginePool(uppaalHome, maxInstances);
                }
            }
        } else {
            instance.resize(maxInstances);
        }
        return instance;
    }

    private UppaalEnginePool(String uppaalHome, int maxInstances) throws UppaalEngineException {
        if (uppaalHome == null || uppaalHome.trim().isEmpty()) {
            throw new UppaalEngineException("uppaalHome cannot be null or empty");
        }
        if (maxInstances < 1) {
            throw new UppaalEngineException("maxInstances must be at least 1");
        }

        this.uppaalHome = uppaalHome;
        this.maxInstances = maxInstances;
        this.availableEngines = new ArrayBlockingQueue<>(maxInstances * 2);
        this.allEngines = new ArrayList<>(maxInstances);

        initializeEngines(maxInstances);
    }

    private void initializeEngines(int count) throws UppaalEngineException {
        System.out.println("Starting up " + count + " UPPAAL engines (\"server\" process)...");

        for (int i = 0; i < count; i++) {
            try {
                UppaalEngine engine = new UppaalEngine(uppaalHome);
                allEngines.add(engine);
                availableEngines.offer(engine);
                currentSize.incrementAndGet();

            } catch (Exception e) {
                cleanup();
                throw new UppaalEngineException("Failed to initialize engine pool at engine " + (i + 1), e);
            }
        }

        System.out.println("Startup Completed");
    }

    public UppaalEngine borrowEngine() throws UppaalEngineException, InterruptedException {
        checkNotShutdown();

        try {
            UppaalEngine engine = availableEngines.poll();
            if (engine != null) {
                return validateAndReturnEngine(engine);
            }

            engine = availableEngines.poll(10, TimeUnit.SECONDS);
            if (engine != null) {
                return validateAndReturnEngine(engine);
            }

            throw new UppaalEngineException("Timeout waiting for available engine 10s)");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    private UppaalEngine validateAndReturnEngine(UppaalEngine engine) throws UppaalEngineException, InterruptedException {
        if (!engine.isHealthy()) {
            replaceUnhealthyEngine(engine);
            return borrowEngineWithRetry(2);
        }
        return engine;
    }

    private UppaalEngine borrowEngineWithRetry(int retriesLeft) throws UppaalEngineException, InterruptedException {
        if (retriesLeft <= 0) {
            throw new UppaalEngineException("Failed to acquire healthy engine after retries");
        }

        UppaalEngine engine = availableEngines.poll(10, TimeUnit.SECONDS);
        if (engine == null) {
            throw new UppaalEngineException("No engines available after retry");
        }

        if (!engine.isHealthy()) {
            replaceUnhealthyEngine(engine);
            return borrowEngineWithRetry(retriesLeft - 1);
        }

        return engine;
    }

    public void returnEngine(UppaalEngine engine) {
        if (engine == null) {
            System.err.println("Attempted to return null engine to pool");
            return;
        }

        if (isShutdown.get()) {
            engine.close();
            return;
        }

        if (!engine.isHealthy()) {
            replaceUnhealthyEngine(engine);
            return;
        }

        if (!availableEngines.offer(engine)) {
            System.err.println("Failed to return engine " + engine.getEngineId());
            engine.close();
        }
    }

    public void resize(int newMaxInstances) throws UppaalEngineException {
        if (newMaxInstances < 1) {
            throw new UppaalEngineException("maxInstances must be at least 1");
        }

        checkNotShutdown();

        synchronized (this) {
            int currentMax = this.maxInstances;

            if (newMaxInstances == currentMax) {
                return;
            }

            if (newMaxInstances > currentMax) {
                expandPool(newMaxInstances - currentMax);
            } else {
                shrinkPool(currentMax - newMaxInstances);
            }

            this.maxInstances = newMaxInstances;
        }
    }

    private void expandPool(int additionalEngines) throws UppaalEngineException {
        System.out.println("Expanding pool by " + additionalEngines + " engines...");

        for (int i = 0; i < additionalEngines; i++) {
            try {
                UppaalEngine newEngine = new UppaalEngine(uppaalHome);
                synchronized (allEngines) {
                    allEngines.add(newEngine);
                }
                availableEngines.offer(newEngine);
                currentSize.incrementAndGet();

            } catch (Exception e) {
                System.err.println("Failed to create additional engine " + (i + 1) + ": " + e.getMessage());
                throw new UppaalEngineException("Failed to expand pool", e);
            }
        }
    }

    private void shrinkPool(int enginestoRemove) {

        List<UppaalEngine> toRemove = new ArrayList<>();

        for (int i = 0; i < enginestoRemove; i++) {
            UppaalEngine engine = availableEngines.poll();
            if (engine != null) {
                toRemove.add(engine);
            } else {
                break;
            }
        }
        for (UppaalEngine engine : toRemove) {
            synchronized (allEngines) {
                allEngines.remove(engine);
            }
            engine.close();
            currentSize.decrementAndGet();
        }

        System.out.println("Pool shrunk to " + currentSize.get() + " engines");
    }

    private void replaceUnhealthyEngine(UppaalEngine unhealthyEngine) {
        try {
            synchronized (allEngines) {
                allEngines.remove(unhealthyEngine);
            }
            unhealthyEngine.close();
            currentSize.decrementAndGet();

            UppaalEngine newEngine = new UppaalEngine(uppaalHome);
            synchronized (allEngines) {
                allEngines.add(newEngine);
            }
            availableEngines.offer(newEngine);
            currentSize.incrementAndGet();

        } catch (Exception e) {
            System.err.println("Failed to replace unhealthy engine " +
                    unhealthyEngine.getEngineId() + ": " + e.getMessage());
        }
    }

    public void shutdown() {
        if (!isShutdown.compareAndSet(false, true)) {
            return;
        }
        cleanup();
        synchronized (INIT_LOCK) {
            instance = null;
        }
    }

    private void cleanup() {
        List<UppaalEngine> allEnginesCopy;
        synchronized (allEngines) {
            allEnginesCopy = new ArrayList<>(allEngines);
            allEngines.clear();
        }
        availableEngines.clear();

        if (allEnginesCopy.isEmpty()) {
            System.out.println("No engines to cleanup");
            return;
        }

        System.out.println("Shutting down " + allEnginesCopy.size() + " engines...");

        ExecutorService cleanupExecutor = Executors.newFixedThreadPool(
                Math.min(allEnginesCopy.size(), 4)
        );

        try {
            List<Future<Void>> cleanupTasks = new ArrayList<>();

            for (int i = 0; i < allEnginesCopy.size(); i++) {
                final UppaalEngine engine = allEnginesCopy.get(i);
                final int engineIndex = i;

                Future<Void> task = cleanupExecutor.submit(() -> {
                    try {
                        engine.close();
                    } catch (Exception e) {
                        System.err.println("Engine " + (engineIndex + 1) + " cleanup failed: " + e.getMessage());
                    }
                    return null;
                });

                cleanupTasks.add(task);
            }

            for (int i = 0; i < cleanupTasks.size(); i++) {
                try {
                    cleanupTasks.get(i).get(10, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    System.err.println("⚠️ Engine " + (i + 1) + " cleanup timed out");
                    cleanupTasks.get(i).cancel(true);
                } catch (Exception e) {
                    System.err.println("⚠️ Engine " + (i + 1) + " cleanup error: " + e.getMessage());
                }
            }

        } finally {
            cleanupExecutor.shutdownNow();
            try {
                if (!cleanupExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    System.err.println("Cleanup executor did not terminate in time");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        currentSize.set(0);
    }

    private void checkNotShutdown() throws UppaalEngineException {
        if (isShutdown.get()) {
            throw new UppaalEngineException("UppaalEnginePool has been shutdown");
        }
    }

}