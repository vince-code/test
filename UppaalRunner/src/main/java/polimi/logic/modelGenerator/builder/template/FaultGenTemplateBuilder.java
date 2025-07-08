package polimi.logic.modelGenerator.builder.template;

import com.uppaal.model.core2.*;
import polimi.logic.modelGenerator.builder.template.model.EdgeType;
import polimi.logic.modelGenerator.builder.template.model.FaultGeneratorLocation;
import polimi.model.Bus;
import polimi.model.Line;
import polimi.model.Network;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FaultGenTemplateBuilder extends TemplateBuilder {

    public Template build(Network network, Document document) throws ModelException {
        Template template = document.createTemplate();
        template.setProperty("name", "Fault_Generator");

        Map<FaultGeneratorLocation, Location> locationsMap = new HashMap<>();

        for (FaultGeneratorLocation faultLocation : FaultGeneratorLocation.values()){
            locationsMap.put(faultLocation, createLocation(template, faultLocation));
        }

        for (Line line: network.getLineMap().values()){
            if (!line.isFaultCandidate()) continue;
            int lineId = line.getId();
            Edge edge = template.addEdge(locationsMap.get(FaultGeneratorLocation.NO_FAULT), locationsMap.get(FaultGeneratorLocation.FAULT_SIGNAL));
            edge.setProperty(EdgeType.guard.name(), "Irc_" + lineId + "!=0");
            edge.setProperty(EdgeType.synchronisation.name(), "L" + lineId + "_fault!");
            edge.setProperty(EdgeType.assignment.name(), "Isc(" + lineId + ",0), F" + lineId + "=true");
        }

        for (Bus bus: network.getBusMap().values()){
            if (!bus.isFaultCandidate()) continue;
            int busId = bus.getId();
            Edge edge = template.addEdge(locationsMap.get(FaultGeneratorLocation.NO_FAULT), locationsMap.get(FaultGeneratorLocation.FAULT_SIGNAL));
            edge.setProperty(EdgeType.synchronisation.name(), "LB" + busId + "_fault!");
            edge.setProperty(EdgeType.assignment.name(), "Isc(0," + busId + "), FB" + busId + "=true");
        }

        template.addEdge(locationsMap.get(FaultGeneratorLocation.FAULT_SIGNAL),locationsMap.get(FaultGeneratorLocation.FAULT))
                .setProperty(EdgeType.synchronisation.name(), "Faults!");

        template.addEdge(locationsMap.get(FaultGeneratorLocation.FAULT),locationsMap.get(FaultGeneratorLocation.CHECK_FAULT))
                .setProperty(EdgeType.synchronisation.name(), "CBopen?");

        Edge closeEdge = template.addEdge(locationsMap.get(FaultGeneratorLocation.CHECK_FAULT),locationsMap.get(FaultGeneratorLocation.FAULT));
        String closeGuard = network.getLineMap().values().stream()
                .map(line -> "Irc_" + line.getId() + ">=Ith_" + line.getId())
                .collect(Collectors.joining(" or "));
        closeEdge.setProperty(EdgeType.guard.name(), closeGuard);
        closeEdge.setProperty(EdgeType.synchronisation.name(), "Close!");

        Edge faultClearEdge = template.addEdge(locationsMap.get(FaultGeneratorLocation.CHECK_FAULT),locationsMap.get(FaultGeneratorLocation.RESET_READY));
        String faultClearGuard = network.getLineMap().values().stream()
                .map(line -> "Irc_" + line.getId() + "<Ith_" + line.getId())
                .collect(Collectors.joining(" && "));
        faultClearEdge.setProperty(EdgeType.guard.name(), faultClearGuard);
        faultClearEdge.setProperty(EdgeType.synchronisation.name(), "Fault_cleared!");

        Edge resetEdge = template.addEdge(locationsMap.get(FaultGeneratorLocation.RESET_READY),locationsMap.get(FaultGeneratorLocation.NO_FAULT));

        List<String> assignments = new ArrayList<>();
        assignments.add("F=0");
        assignments.add("FB=0");

        network.getLineMap().values().stream()
                .filter(Line::isFaultCandidate)
                .map(line -> "F" + line.getId() + "=false")
                .forEach(assignments::add);

        network.getBusMap().values().stream()
                .filter(Bus::isFaultCandidate)
                .map(bus -> "FB" + bus.getId() + "=false")
                .forEach(assignments::add);

        String reset = String.join(", ", assignments);

        resetEdge.setProperty(EdgeType.assignment.name(), reset);
        resetEdge.setProperty(EdgeType.synchronisation.name(), "Reset!");

        return template;
    }

}
