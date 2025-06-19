package polimi.logic;

import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Query;
import polimi.logic.splitter.NetworkFaultSplitter;
import polimi.logic.verifier.UppaalClient;
import polimi.model.VerificationResult;
import polimi.model.Network;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SelectivityChecker {

    private final static int DEFAULT_MAX_FAULTS = 8;

    public static List<Integer> extractMisconfiguredCBs(Network network) throws ExecutionException, InterruptedException {
        return extractMisconfiguredCBs(network, DEFAULT_MAX_FAULTS);
    }

    public static List<Integer> extractMisconfiguredCBs(Network network, int maxFaults) throws ExecutionException, InterruptedException {

        NetworkFaultSplitter splitter = new NetworkFaultSplitter(network);
        List<Document> splitModels = splitter.generateSplitDocuments(maxFaults, true);

        ExecutorService executor = Context.getInstance().getExecutor();
        List<Future<List<Integer>>> futures = new ArrayList<>();

        for (Document document : splitModels) {
            futures.add(executor.submit(() -> {

                UppaalClient client = new UppaalClient();
                Set<Integer> localMisconfigured = new HashSet<>();
                Query query = document.getQueryList().get(0);
                VerificationResult result;

                do {
                    result = client.verify(document, query, false);

                    if (result.getMisconfiguredCBs() != null) {
                        localMisconfigured.addAll(result.getMisconfiguredCBs());
                    }

                    if (result.isSatisfied()) {
                        query = filterExistingQuery(query, localMisconfigured);
                    }

                } while (result.isSatisfied());

                return new ArrayList<>(localMisconfigured);
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

    public static Query filterExistingQuery(Query currentQuery, Set<Integer> newExcludedCBs) {
        String formula = currentQuery.getFormula();

        String innerFormula = extractInnerFormula(formula);
        String[] conditions = innerFormula.split("\\|\\|");

        List<String> validConditions = Arrays.stream(conditions)
                .map(String::trim)
                .filter(condition -> !shouldRemoveCondition(condition, newExcludedCBs))
                .collect(Collectors.toList());

        String joinedConditions = String.join("||", validConditions);
        return new Query("E<>(" + joinedConditions + ")");
    }

    private static boolean shouldRemoveCondition(String condition, Set<Integer> excludedCBs) {
        List<Integer> cbIds = extractCBIdsFromCondition(condition);
        return cbIds.size() >= 2 && excludedCBs.containsAll(cbIds);
    }

    private static List<Integer> extractCBIdsFromCondition(String condition) {

        Pattern pattern = Pattern.compile("CB(\\d+)\\.Open");
        Matcher matcher = pattern.matcher(condition);

        List<Integer> cbIds = new ArrayList<>();
        while (matcher.find()) {
            cbIds.add(Integer.parseInt(matcher.group(1)));
        }
        return cbIds;
    }

    private static String extractInnerFormula(String formula) {
        int start = formula.indexOf('(') + 1;
        int end = formula.lastIndexOf(')');
        return formula.substring(start, end);
    }
}
