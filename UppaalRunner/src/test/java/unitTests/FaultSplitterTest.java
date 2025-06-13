package unitTests;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import polimi.logic.splitter.FaultScenario;
import polimi.logic.splitter.NetworkFaultSplitter;
import polimi.model.Bus;
import polimi.model.Line;
import polimi.model.Network;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FaultSplitterTest {

    static Stream<org.junit.jupiter.params.provider.Arguments> scenarioProvider() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(20, 20, 6, 5, 8),     // split required
                org.junit.jupiter.params.provider.Arguments.of(20, 20, 0, 0, 8),     // no faults
                org.junit.jupiter.params.provider.Arguments.of(20, 20, 9, 0, 8),     // only line faults
                org.junit.jupiter.params.provider.Arguments.of(20, 20, 0, 9, 8),     // only bus faults
                org.junit.jupiter.params.provider.Arguments.of(20, 20, 4, 4, 8)      // exact fit
        );
    }

    @ParameterizedTest
    @MethodSource("scenarioProvider")
    public void splitConstraintsTest(int totalBus, int totalLine, int faultLineCount, int faultBusCount, int maxAcceptedFaults) {

        Network network = TestUtils.buildMockNetwork(totalBus, totalLine, faultLineCount, faultBusCount);
        NetworkFaultSplitter faultSplitter = new NetworkFaultSplitter(network);

        List<FaultScenario> scenarios = faultSplitter.splitFaults(maxAcceptedFaults);

        Set<Integer> allBusIds = new HashSet<>();
        Set<Integer> allLineIds = new HashSet<>();

        for (FaultScenario scenario : scenarios) {
            System.out.println(scenario);
            int totalSize = scenario.getBusFaults().size() + scenario.getLineFaults().size();

            // Check no scenario exceeds maxAcceptedFaults
            assertTrue(totalSize <= maxAcceptedFaults, "Scenario exceeds maxAcceptedFaults");

            allBusIds.addAll(scenario.getBusFaults());
            allLineIds.addAll(scenario.getLineFaults());

            // Check order is non-decreasing
            assertTrue(isSorted(scenario.getBusFaults()), "Bus IDs not sorted");
            assertTrue(isSorted(scenario.getLineFaults()), "Line IDs not sorted");

        }

        List<Integer> busFaults = network.getBusMap().values().stream()
                .filter(Bus::isFaultCandidate)
                .map(Bus::getId)
                .collect(Collectors.toList());

        List<Integer> lineFaults = network.getLineMap().values().stream()
                .filter(Line::isFaultCandidate)
                .map(Line::getId)
                .collect(Collectors.toList());

        // Check all fault candidates are included
        Set<Integer> expectedBusIds = new HashSet<>(busFaults);
        Set<Integer> expectedLineIds = new HashSet<>(lineFaults);

        assertEquals(expectedBusIds, allBusIds, "Not all Bus IDs are present");
        assertEquals(expectedLineIds, allLineIds, "Not all Line IDs are present");


        int[] groupSizes = scenarios.stream().mapToInt(s -> s.getBusFaults().size() + s.getLineFaults().size()).toArray();
        int minSize = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < groupSizes.length; i++) {
            if (groupSizes[i] < minSize) {
                minSize = groupSizes[i];
                minIndex = i;
            }
        }

        int totalCapacityLeft = 0;
        for (int i = 0; i < groupSizes.length; i++) {
            if (i == minIndex) continue;  // skip the smallest group
            totalCapacityLeft += (maxAcceptedFaults - groupSizes[i]);
        }

        // Check if the smallest group could be absorbed by the others
        assertTrue(minSize > totalCapacityLeft, "Number of groups could be reduced");
    }

    @RepeatedTest(30)
    void randomizedSplitTest() throws IOException {
        Random rand = new Random();

        int totalBus = rand.nextInt(50) + 1;   // 1–50
        int totalLine = rand.nextInt(50) + 1;
        int faultBus = rand.nextInt(totalBus + 1);  // 0–totalBus
        int faultLine = rand.nextInt(totalLine + 1);
        int maxAccepted = 8;

        System.out.println("Total Buses: " + totalBus);
        System.out.println("Total Lines: " + totalLine);
        System.out.println("Fault Buses count: " + faultBus);
        System.out.println("Fault Lines count: " + faultLine);
        System.out.println("Max accepted faults: " + maxAccepted);

        splitConstraintsTest(totalBus,totalLine,faultLine,faultBus,maxAccepted);
    }

    private boolean isSorted(Set<Integer> ids) {
        List<Integer> list = new ArrayList<>(ids);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i - 1) > list.get(i)) {
                return false;
            }
        }
        return true;
    }

}



