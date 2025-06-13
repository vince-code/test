package polimi.logic.generator.builder.declarations.functions;

import polimi.model.Bus;
import polimi.model.Line;
import polimi.model.Network;
import polimi.model.Source;
import polimi.util.IndentedStringBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IscFunctionBuilder {

    private final Network network;

    public IscFunctionBuilder(Network network) {
        this.network = network;
    }

    public String build() {
        IndentedStringBuilder function = new IndentedStringBuilder();
        function.appendLine("void Isc(int L, int LB){");
        function.increaseIndentation();
        boolean firstBlock = true;

        for (Line line : network.getLineMap().values()) {
            if (!line.isFaultCandidate()) continue;

            function.appendIndented(firstBlock ? "if" : "else if")
                    .append(" (L==").append(line.getId()).appendRawLine("){");

            function.increaseIndentation();
            function.append(generateIscBlock(network, line.getId(), null, function.getCurrentIndentation()));
            function.decreaseIndentation();
            function.appendLine("}");
            firstBlock = false;
        }

        for (Bus bus : network.getBusMap().values()) {
            if (!bus.isFaultCandidate()) continue;

            function.appendIndented(firstBlock ? "if" : "else if")
                    .append(" (LB==").append(bus.getId()).appendRawLine("){");

            function.increaseIndentation();
            function.append(generateIscBlock(network, null, bus.getId(), function.getCurrentIndentation()));
            function.decreaseIndentation();
            function.appendLine("}");
            firstBlock = false;
        }

        function.decreaseIndentation();
        function.appendLine("}");
        return function.toString();
    }

    public static String generateIscBlock(Network network, Integer faultedLineId, Integer faultedBusId, int indentationLevel) {
        if ((faultedLineId == null && faultedBusId == null) || (faultedLineId != null && faultedBusId != null)) {
            throw new IllegalArgumentException("Specify either a faulted line OR a faulted bus.");
        }

        IndentedStringBuilder sb = new IndentedStringBuilder(null, indentationLevel);

        Source source = network.getSources().get(0);
        Bus sourceBus = source.getSourceBus();
        Bus targetBus;
        String iscVar;

        if (faultedLineId != null) {
            sb.appendAssign("F", faultedLineId);
            Line faultedLine = network.getLine(faultedLineId);
            targetBus = faultedLine.getToBus();
            iscVar = "Isc_" + sourceBus.getId() + "_" + faultedLine.getId();
        } else {
            sb.appendAssign("FB", faultedBusId);
            Bus faultedBus = network.getBus(faultedBusId);
            targetBus = faultedBus;
            iscVar = "Iscb_" + sourceBus.getId() + "_" + faultedBus.getId();
        }

        List<Line> path = network.getCachedPath(sourceBus, targetBus);
        if (path == null) throw new IllegalStateException("No path found from source bus " + sourceBus.getId() + " to fault bus " + targetBus.getId());

        String sourceLineIrc = "Irc_" + path.get(0).getId();
        sb.appendAssign(sourceLineIrc, iscVar);

        Set<Line> pathSet = new HashSet<>(path);

        for (Line line : network.getLineMap().values()) {
            if (!pathSet.contains(line)) {
                sb.appendAssign("Irc_" + line.getId(), 0);
            }
        }

        for (int i = 1; i < path.size(); i++) {
            Line current = path.get(i);
            String currentIrc = "Irc_" + current.getId();

            Line prev = path.get(i - 1);
            String prevIrc = "Irc_" + prev.getId();

            Bus currentFrom = current.getFromBus();

            List<String> inContrib = currentFrom.getInLines().stream()
                    .filter(l -> !pathSet.contains(l))
                    .map(l -> "Irc_" + l.getId())
                    .collect(Collectors.toList());

            List<String> outContrib = currentFrom.getOutLines().stream()
                    .filter(l -> !l.equals(current) && !pathSet.contains(l))
                    .map(l -> "Irc_" + l.getId())
                    .collect(Collectors.toList());

            IndentedStringBuilder expr = new IndentedStringBuilder(prevIrc);

            if (!inContrib.isEmpty()) {
                expr.append(" + ").append(String.join(" + ", inContrib));
            }
            if (!outContrib.isEmpty()) {
                expr.append(" - ").append(String.join(" - ", outContrib));
            }

            sb.appendAssign(currentIrc, expr.toString());

        }

        return sb.toString();
    }
}
