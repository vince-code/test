package polimi.logic;

import lombok.Getter;
import lombok.Setter;
import polimi.logic.verifier.UppaalClient;
import polimi.model.Network;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class Context {

    private static Context instance;

    @Setter
    private Network network;
    private final String uppaalHome;
    private static boolean shutdownHookAdded = false;

    private BlockingQueue<UppaalClient> clientPool;
    private ExecutorService executor;

    private Context(String uppaalHome) throws IOException {
        this.uppaalHome = uppaalHome;
    }

    public static void initializeUppaalEnginesPool(String uppaalHome, int maxParallelInstances) throws IOException {
        if (maxParallelInstances < 1) {
            throw new IllegalStateException("Invalid number of Uppaal engine instances (at least 1)");
        }
        if (instance == null) {
            instance = new Context(uppaalHome);
            instance.initClients(maxParallelInstances);

            if (!shutdownHookAdded) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    System.out.println("Terminating Uppaal engines...");
                    Context inst = instance;
                    if (inst != null) {
                        inst.shutdown();
                    }
                }));
                shutdownHookAdded = true;
            }
        }
    }

    public static Context getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Context is not initialized");
        }
        return instance;
    }

    private void initClients(int maxParallel) throws IOException {
        this.executor = Executors.newFixedThreadPool(maxParallel);
        this.clientPool = new ArrayBlockingQueue<>(maxParallel);
        for (int i = 0; i < maxParallel; i++) {
            this.clientPool.add(new UppaalClient(this.uppaalHome));
        }
    }

    public void shutdown() {
        if (this.clientPool != null) {
            for (UppaalClient client : clientPool) {
                try {
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.clientPool.clear();
        }

        if (this.executor != null) {
            this.executor.shutdownNow();
            try {
                if (!this.executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate in time");
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        this.executor = null;
        this.clientPool = null;

        resetInstance();
        System.out.println("\nJavaAPI Context Shutdown completed");
    }

    public void resetInstance() {
        if (instance != null) {
            instance = null;
        }
    }

}
