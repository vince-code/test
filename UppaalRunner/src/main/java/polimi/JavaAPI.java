package polimi;

import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Query;
import polimi.logic.NetworkBuilder;
import polimi.logic.SelectivityChecker;
import polimi.logic.generator.UppaalModelGenerator;
import polimi.logic.Context;
import polimi.logic.generator.builder.QueryBuilder;
import polimi.logic.splitter.NetworkFaultSplitter;
import polimi.logic.verifier.UppaalClient;
import polimi.model.VerificationResult;
import polimi.model.Line;
import polimi.model.Network;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public interface JavaAPI {

    static void startUppaalEngines(String uppaalHome, int maxParallelInstances) throws IOException {
        Context.initializeUppaalEnginesPool(uppaalHome, maxParallelInstances);
    }

    static void loadNetwork(String jsonPath) throws IOException {
        File jsonFile = new File(jsonPath);
        Network network = NetworkBuilder.build(jsonFile);
        Context.getInstance().setNetwork(network);
    }

    static void generateUppaalModel(String outputPath, String outputName) throws IOException {
        Network network = Context.getInstance().getNetwork();
        UppaalModelGenerator modelGenerator = new UppaalModelGenerator(network);
        Document document = modelGenerator.generateDocument(true);
        document.save(outputPath + "/" + outputName.replace(".xml","").trim() + ".xml");
    }

    static void generateSplitUppaalModels(String outputPath, String outputName, int maxFaults) throws IOException {
        Network network = Context.getInstance().getNetwork();
        NetworkFaultSplitter faultSplitter = new NetworkFaultSplitter(network);
        List<Document> uppaalModels = faultSplitter.generateSplitDocuments(maxFaults, true);
        String splitName = outputName.replace(".xml", "").trim() + "_";
        int i = 1;
        for (Document splitModel : uppaalModels){
            splitModel.save(outputPath + "/" + splitName + i + ".xml");
            i++;
        }
    }

    static int[] getNetworkPath(int startBusId, int endBusId) {
        Network network = Context.getInstance().getNetwork();
        return network.getCachedPath(startBusId, endBusId).stream().mapToInt(Line::getId).toArray();
    }


    static String generateDefaultQuery() {
        Network network = Context.getInstance().getNetwork();
        return QueryBuilder.generateQuery(network).getFormula();
    }


    static String generateFilteredQuery(int[] excludedCBIds) {
        Network network = Context.getInstance().getNetwork();
        return QueryBuilder.generateQuery(network, Arrays.stream(excludedCBIds).boxed().collect(Collectors.toList())).getFormula();
    }


    static VerificationResult verifyDefaultQuery(boolean withTrace) throws Exception {
        Network network = Context.getInstance().getNetwork();
        Document document = new UppaalModelGenerator(network).generateDocument(true);

        UppaalClient client = new UppaalClient();
        return client.verify(document, document.getQueryList().get(0), withTrace);
    }


    static VerificationResult verifyQuery(String query, boolean withTrace) throws Exception {
        Network network = Context.getInstance().getNetwork();
        Document document = new UppaalModelGenerator(network).generateDocument(true);

        UppaalClient client = new UppaalClient();
        return client.verify(document, new Query(query), withTrace);
    }

    static int[] extractMisconfiguredCBs() throws ExecutionException, InterruptedException {
        Network network = Context.getInstance().getNetwork();
        return SelectivityChecker.extractMisconfiguredCBs(network).stream().mapToInt(Integer::intValue).toArray();
    }

    static int[] extractMisconfiguredCBs(int maxFaults) throws ExecutionException, InterruptedException {
        Network network = Context.getInstance().getNetwork();
        return SelectivityChecker.extractMisconfiguredCBs(network, maxFaults).stream().mapToInt(Integer::intValue).toArray();
    }

    static void updateCBConfig(int cbId, int t2) {
        Network network = Context.getInstance().getNetwork();
        network.getCB(cbId).setT2(t2);
    }


    static void shutdown() {
        Context.getInstance().shutdown();
    }
}
