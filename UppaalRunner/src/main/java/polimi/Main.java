package polimi;

import polimi.logic.networkParsing.NetworkBuilder;
import polimi.logic.SelectivityChecker;
import polimi.model.Network;
import polimi.util.NetworkGraphVisualizer;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        //String uppaalHome = resolveUppaalPath(args);
        String uppaalHome = "UppaalRunner/UPPAAL-5.1.0-beta5.app/Contents/Resources/uppaal";

        String jsonPath = "UppaalRunner/example/jsonNet/Example_100bus.json";

        Network network = NetworkBuilder.build(new File(jsonPath));
        //visualizeNetwork(network);

        JavaAPI.startUppaalEngines(uppaalHome, 6);

        long startLoad = System.currentTimeMillis();
        JavaAPI.loadNetwork(jsonPath);
        long endLoad = System.currentTimeMillis();
        int[] misconfiguredCBs = JavaAPI.extractMisconfiguredCBs(7);
        long endExtraction = System.currentTimeMillis();

        System.out.println("Initial Net misconfigured cbs: " + Arrays.toString(misconfiguredCBs));

        System.out.println("Updating t2 of CB_23 from 6 to 1");

        long startUpdate = System.currentTimeMillis();
        JavaAPI.updateCBConfig(23, 1);
        long endUpdate = System.currentTimeMillis();
        int[] misconfigured2 = JavaAPI.extractMisconfiguredCBs(7);
        long endExtraction2 = System.currentTimeMillis();

        System.out.println("Misconfigured cbs after update: " + Arrays.toString(misconfigured2));
        long secondExtraction1 = endExtraction2 - endUpdate;
        System.out.println(secondExtraction1);


        long startLoad2 = System.currentTimeMillis();
        JavaAPI.loadNetwork(jsonPath, 7);
        long endLoad2 = System.currentTimeMillis();
        int[] misconfiguredCBs2 = JavaAPI.extractMisconfiguredCBs(7);
        long endExtraction22 = System.currentTimeMillis();

        System.out.println("Initial Net misconfigured cbs: " + Arrays.toString(misconfiguredCBs2));

        System.out.println("Updating t2 of CB_23 from 6 to 1");

        long startUpdate2 = System.currentTimeMillis();
        JavaAPI.updateDocumentCBConfig(23, 1);
        long endUpdate2 = System.currentTimeMillis();
        int[] misconfigured22 = JavaAPI.extractMisconfiguredCBs(7);
        long endExtraction3 = System.currentTimeMillis();

        System.out.println("Misconfigured cbs after update: " + Arrays.toString(misconfigured22));
        long secondExtraction = endExtraction3 - endUpdate2;
        System.out.println(secondExtraction);
        JavaAPI.shutdown();

    }

    public static void extractMisconfiguredCBs(Network network) throws Exception {
        long start = System.nanoTime();

        System.out.println(SelectivityChecker.extractMisconfiguredCBs(network));

        long end = System.nanoTime();
        System.out.println(((end-start) / 1_000_000.0) + " milliseconds");
    }

    public static void visualizeNetwork(Network network) throws InterruptedException {
        NetworkGraphVisualizer.visualize(network);
    }

    public static String resolveUppaalPath(String[] args) throws IOException {
        if (args.length > 0) {
            return args[0];
        }

        String env = System.getenv("UPPAAL_HOME");
        if (env != null && !env.isBlank()) {
            return env;
        }

        File dir = new File(".");
        while (dir != null) {
            File uppaalJar = new File(dir, "uppaal.jar");
            if (uppaalJar.exists()) {
                return dir.getAbsolutePath();
            }
            dir = dir.getParentFile();
        }

        throw new IOException("Could not find UPPAAL installation. Set UPPAAL_HOME or pass the path as argument.");
    }
}