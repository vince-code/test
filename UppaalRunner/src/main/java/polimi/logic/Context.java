package polimi.logic;

import com.uppaal.model.core2.Document;
import lombok.Getter;
import lombok.Setter;
import polimi.logic.engineManagement.UppaalEngineException;
import polimi.logic.engineManagement.UppaalEnginePool;
import polimi.model.Network;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

@Getter
public class Context {

    private static Context instance;
    private static final Object INIT_LOCK = new Object();

    @Setter
    private Network network;
    @Setter
    private Document networkDocument;
    @Setter
    private List<Document> splitNetworkDocuments;

    private ExecutorService executor;
    private final String uppaalHome;
    private static boolean shutdownHookAdded = false;

    private Context(String uppaalHome) {
        this.uppaalHome = uppaalHome;
    }

    public static Context getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Context is not initialized");
        }
        return instance;
    }

    public static void initializeUppaalEnginesPool(String uppaalHome, int maxParallelInstances) throws IOException {
        if (maxParallelInstances < 1) {
            throw new IllegalArgumentException("Invalid number of Uppaal engine instances (at least 1)");
        }
        try {
            if (instance == null) {
                synchronized (INIT_LOCK) {
                    if (instance == null) {
                        instance = new Context(uppaalHome);
                        addShutdownHook();
                    }
                }
            }
            UppaalEnginePool.initialize(uppaalHome, maxParallelInstances);
            instance.syncExecutorWithPoolSize(maxParallelInstances);
        } catch (UppaalEngineException e) {
            throw new IOException("Failed to initialize UPPAAL engines pool", e);
        }
    }

    public ExecutorService getExecutor() {
        if (executor == null || executor.isShutdown()) {
            throw new IllegalStateException("Executor is not available");
        }
        return executor;
    }

    private void syncExecutorWithPoolSize(int requiredSize) {
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newFixedThreadPool(requiredSize);
        } else {
            int currentSize = ((ThreadPoolExecutor) executor).getCorePoolSize();
            if (currentSize != requiredSize) {
                resizeExecutor(requiredSize);
            }
        }
    }

    private static void addShutdownHook() {
        if (!shutdownHookAdded) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Context currentInstance = instance;
                if (currentInstance != null) {
                    currentInstance.shutdown();
                }
            }));
            shutdownHookAdded = true;
        }
    }

    public void shutdown() {
        try {
            if (executor != null && !executor.isShutdown()) {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }

            UppaalEnginePool.getInstance().shutdown();

        } catch (Exception e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }

        resetInstance();
        System.out.println("Shutdown completed");
    }

    private void resetInstance() {
        synchronized (INIT_LOCK) {
            instance = null;
        }
    }

    private void resizeExecutor(int newSize) {

        ExecutorService oldExecutor = executor;
        executor = Executors.newFixedThreadPool(newSize);

        oldExecutor.shutdown();
        CompletableFuture.runAsync(() -> {
            try {
                if (!oldExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    oldExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                oldExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        });
    }
}
