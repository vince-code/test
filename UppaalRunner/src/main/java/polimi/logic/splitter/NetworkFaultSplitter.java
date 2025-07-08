package polimi.logic.splitter;

import com.uppaal.model.core2.Document;
import polimi.logic.modelGenerator.UppaalModelGenerator;
import polimi.model.Bus;
import polimi.model.Line;
import polimi.model.Network;
import java.util.*;
import java.util.stream.Collectors;

public class NetworkFaultSplitter {

    private final Network network;

    public NetworkFaultSplitter(Network network) {
        this.network = network;
    }

    public List<Document> generateSplitDocuments(int maxAcceptedFaults, boolean withDefaultQuery) {
        List<Document> documents = new ArrayList<>();
        List<FaultScenario> scenarios = splitFaults(maxAcceptedFaults);

        List<Integer> initialFaultBuses = network.getBusMap().values().stream()
                .filter(Bus::isFaultCandidate)
                .map(Bus::getId)
                .collect(Collectors.toList());

        List<Integer> initialFaultLines = network.getLineMap().values().stream()
                .filter(Line::isFaultCandidate)
                .map(Line::getId)
                .collect(Collectors.toList());

        for (FaultScenario scenario : scenarios) {
            scenario.applyTo(network);
            Document doc = new UppaalModelGenerator(network).generateDocument(withDefaultQuery);
            if (doc != null) {
                documents.add(doc);
            }
        }

        new FaultScenario(new HashSet<>(initialFaultBuses), new HashSet<>(initialFaultLines)).applyTo(network);

        return documents;
    }

    public List<FaultScenario> splitFaults(int maxAcceptedFaults){
        List<Integer> busFaults = network.getBusMap().values().stream()
                .filter(Bus::isFaultCandidate)
                .map(Bus::getId)
                .collect(Collectors.toList());

        List<Integer> lineFaults = network.getLineMap().values().stream()
                .filter(Line::isFaultCandidate)
                .map(Line::getId)
                .collect(Collectors.toList());

        int totalElements = busFaults.size() + lineFaults.size();
        int groupCount = (int) Math.ceil((double) totalElements / maxAcceptedFaults);

        int[] groupFaultLinesCount = new int[groupCount];
        int[] groupFaultBusesCount = new int[groupCount];

        int roundRobinIndex = 0;
        for (int lineId : lineFaults){
            groupFaultLinesCount[roundRobinIndex]++;
            roundRobinIndex = ( roundRobinIndex + 1 ) % groupCount;
        }

        for (int busId : busFaults){
            groupFaultBusesCount[roundRobinIndex]++;
            roundRobinIndex = ( roundRobinIndex + 1 ) % groupCount;
        }

        List<FaultScenario> scenarios = new ArrayList<>();

        int lineOffset = 0;
        int busOffset = 0;
        for (int groupIndex = 0; groupIndex < groupCount; groupIndex++){

            List<Integer> groupLines = lineFaults.subList(lineOffset, lineOffset + groupFaultLinesCount[groupIndex]);
            List<Integer> groupBuses = busFaults.subList(busOffset, busOffset + groupFaultBusesCount[groupIndex]);
            lineOffset += groupFaultLinesCount[groupIndex];
            busOffset += groupFaultBusesCount[groupIndex];

            scenarios.add(new FaultScenario(new LinkedHashSet<>(groupBuses), new LinkedHashSet<>(groupLines)));
        }
        return scenarios;
    }


}
