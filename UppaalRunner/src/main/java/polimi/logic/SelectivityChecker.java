package polimi.logic;

import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Query;
import polimi.logic.generator.builder.QueryBuilder;
import polimi.logic.splitter.NetworkFaultSplitter;
import polimi.logic.verifier.UppaalClient;
import polimi.model.VerificationResult;
import polimi.model.Network;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SelectivityChecker {

    private final static int DEFAULT_MAX_FAULTS = 8;

    public static List<Integer> extractMisconfiguredCBs(Network network) throws ExecutionException, InterruptedException {
        return extractMisconfiguredCBs(network, DEFAULT_MAX_FAULTS);
    }

    public static List<Integer> extractMisconfiguredCBs(Network network, int maxFaults) throws ExecutionException, InterruptedException {

        NetworkFaultSplitter splitter = new NetworkFaultSplitter(network);
        List<Document> splitModels = splitter.generateSplitDocuments(maxFaults, true);

        BlockingQueue<UppaalClient> clientPool = Context.getInstance().getClientPool();
        ExecutorService executor = Context.getInstance().getExecutor();

        List<Future<List<Integer>>> futures = new ArrayList<>();
        for (Document document : splitModels) {
            futures.add(executor.submit(() -> {

                UppaalClient client = clientPool.take();
                try {
                    List<Integer> localMisconfigured = new ArrayList<>();
                    Query query = document.getQueryList().get(0);
                    VerificationResult result;

                    do {
                        result = client.verify(document, query, false);

                        if (result.getMisconfiguredCBs() != null) {
                            localMisconfigured.addAll(result.getMisconfiguredCBs());
                        }

                        if (result.isSatisfied()) {
                            query = QueryBuilder.generateQuery(network, new ArrayList<>(localMisconfigured));
                        }

                    } while (result.isSatisfied());
                    return localMisconfigured;

                } finally {
                    clientPool.put(client);
                }
            }));
        }

        Set<Integer> misconfiguredCbs = new HashSet<>();
        for (Future<List<Integer>> future : futures) {
            misconfiguredCbs.addAll(future.get());
        }

        List<Integer> sortedResult = new ArrayList<>(misconfiguredCbs);
        Collections.sort(sortedResult);
        return sortedResult;
    }

}
