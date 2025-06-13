package unitTests;

import polimi.model.Bus;
import polimi.model.Line;
import polimi.model.Network;

import java.util.*;

public class TestUtils {

    public static Network buildMockNetwork(int totalBus, int totalLine, int faultLineCount, int faultBusCount ) {

        // Bus creation
        Map<Integer, Bus> busMap = new HashMap<>();
        for (int i = 1; i <= totalBus; i++) {
            busMap.put(i, new Bus(i));
        }

        // Random bus fault selection
        List<Integer> busIds = new ArrayList<>(busMap.keySet());
        Collections.shuffle(busIds);
        Set<Integer> faultBusIds = new HashSet<>(busIds.subList(0, Math.min(faultBusCount, totalBus)));

        for (Integer id : faultBusIds) {
            busMap.get(id).setFaultCandidate(true);
        }

        // Lines creation
        Map<Integer, Line> lineMap = new HashMap<>();
        int lineId = 1;
        for (int i = 1; i <= totalLine; i++) {
            int fromId = ((i - 1) % totalBus) + 1;
            int toId = (i % totalBus) + 1;

            Bus fromBus = busMap.get(fromId);
            Bus toBus = busMap.get(toId);

            Line line = new Line(lineId++, fromBus, toBus, 0, 0);

            fromBus.getOutLines().add(line);
            toBus.getInLines().add(line);

            lineMap.put(line.getId(), line);
        }

        // Random line faults selection
        List<Integer> lineIds = new ArrayList<>(lineMap.keySet());
        Collections.shuffle(lineIds);
        Set<Integer> faultLineIds = new HashSet<>(lineIds.subList(0, Math.min(faultLineCount, totalLine)));

        for (Integer id : faultLineIds) {
            lineMap.get(id).setFaultCandidate(true);
        }

        return new Network(busMap, lineMap, new HashMap<>(), new ArrayList<>());

    }
}
