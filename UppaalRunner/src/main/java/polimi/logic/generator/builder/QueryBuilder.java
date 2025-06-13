package polimi.logic.generator.builder;

import com.uppaal.model.core2.Query;
import polimi.model.Bus;
import polimi.model.Line;
import polimi.model.Network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QueryBuilder {

    public static Query generateQuery(Network network) {
        return buildQuery(network, null);
    }

    public static Query generateQuery(Network network, List<Integer> excludedCBs) {
        return buildQuery(network, excludedCBs);
    }

    private static Query buildQuery(Network network, List<Integer> excludedCBs) {
        String condition = buildConditions(network, excludedCBs);
        return new Query("E<>(" + condition + ")");
    }


    private static String buildConditions(Network network, List<Integer> excludedCBs) {
        StringBuilder queryConditions = new StringBuilder();

        Bus sourceBus = network.getSources().get(0).getSourceBus();

        List<String> conditions = new ArrayList<>();

        for (Line line : network.getLineMap().values()){
            if (!line.isFaultCandidate()) continue;

            List<Line> faultPath = new ArrayList<>(network.getCachedPath(sourceBus,line.getFromBus()));
            Collections.reverse(faultPath);
            conditions.addAll(faultPath.stream().map(line1 -> buildLineCondition(line, line1, excludedCBs))
                    .filter(cond -> !cond.isEmpty()).collect(Collectors.toList()));

        }

        for (Bus bus : network.getBusMap().values()){
            if (!bus.isFaultCandidate()) continue;

            List<Line> faultPath = new ArrayList<>(network.getCachedPath(sourceBus, bus.getInLines().get(0).getFromBus()));
            Collections.reverse(faultPath);
            conditions.addAll(faultPath.stream().map(line1 -> buildBusCondition(bus, line1, excludedCBs))
                    .filter(cond -> !cond.isEmpty()).collect(Collectors.toList()));

        }

        return String.join("||", conditions);
    }

    private static String buildLineCondition(Line faultLine, Line pathLine, List<Integer> excludedCBs) {
        int faultCB = faultLine.getCircuitBreaker().getId();
        int pathCB = pathLine.getCircuitBreaker().getId();

        if (excludedCBs != null && excludedCBs.contains(faultCB) && excludedCBs.contains(pathCB)) {
            return "";
        }

        return "(!CB" + faultCB + ".Open && CB" + pathCB + ".Open && F" + faultLine.getId() + ")";
    }

    private static String buildBusCondition(Bus faultBus, Line pathLine, List<Integer> excludedCBs) {
        int faultCB = faultBus.getInLines().get(0).getCircuitBreaker().getId();
        int pathCB = pathLine.getCircuitBreaker().getId();

        if (excludedCBs != null && excludedCBs.contains(faultCB) && excludedCBs.contains(pathCB)) {
            return "";
        }

        return "(!CB" + faultCB + ".Open && CB" + pathCB + ".Open && FB" + faultBus.getId() + ")";
    }


}
