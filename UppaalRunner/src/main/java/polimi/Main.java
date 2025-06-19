package polimi;

import polimi.logic.NetworkBuilder;
import polimi.logic.SelectivityChecker;
import polimi.model.Network;
import polimi.model.VerificationResult;
import polimi.util.NetworkGraphVisualizer;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        //String uppaalHome = resolveUppaalPath(args);
        String uppaalHome = "UppaalRunner/UPPAAL-5.1.0-beta5.app/Contents/Resources/uppaal";

        String jsonPath = "UppaalRunner/example/jsonNet/Example_20bus_Network_1.json";

        Network network = NetworkBuilder.build(new File(jsonPath));
        //visualizeNetwork(network);

        JavaAPI.startUppaalEngines(uppaalHome, 2);
        JavaAPI.loadNetwork(jsonPath);

        JavaAPI.generateUppaalModel("/Users/enzo/Desktop/test","test");
        JavaAPI.generateSplitUppaalModels("/Users/enzo/Desktop/test/split","test",8);

        System.out.println("Path 8 -> 9: " + Arrays.toString(JavaAPI.getNetworkPath(8, 9)));
        System.out.println("DefaultQuery: " + JavaAPI.generateDefaultQuery());
        System.out.println("Default Query Verification:\n" + JavaAPI.verifyDefaultQuery(true));

        String filteredQuery = JavaAPI.generateFilteredQuery(new int[]{3, 5, 8});

        System.out.println("Filtered Query (without cbs 3,5,8): " + filteredQuery);
        //VerificationResult result = JavaAPI.verifyQuery(filteredQuery, false);
        //System.out.println("Filtered Query Verification:\n" + Arrays.toString(result.getMisconfiguredCBsArray()));

        System.out.println("Initial Net misconfigured cbs: " + Arrays.toString(JavaAPI.extractMisconfiguredCBs()));
        System.out.println("Updating t2 of CB_23 from 6 to 1");
        JavaAPI.updateCBConfig(23, 1);
        System.out.println("Misconfigured cbs after update: " + Arrays.toString(JavaAPI.extractMisconfiguredCBs()));

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