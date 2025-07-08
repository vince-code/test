package polimi.logic.modelGenerator.builder.declarations.functions;

import polimi.model.*;
import polimi.logic.modelGenerator.IndentedStringBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpdateFunctionBuilder {

    private final Network network;

    public UpdateFunctionBuilder(Network network) {
        this.network = network;
    }

    public String build() {
        IndentedStringBuilder sb = new IndentedStringBuilder();
        sb.appendLine("void update(int CB_ID){");
        sb.increaseIndentation();
        boolean firstBlock = true;

        for (Line faultedLine : network.getLineMap().values()) {
            if (!faultedLine.isFaultCandidate()) continue;

            sb.appendIndented(firstBlock ? "if" : "else if")
                    .append(" (F==").append(faultedLine.getId()).appendRawLine("){");
            sb.increaseIndentation();
            boolean firstIf = true;
            for (CircuitBreaker cb : network.getCbMap().values()) {
                int cbLineId = cb.getLine().getId();
                String block = generateUpdateBlock(network, cbLineId, faultedLine.getId(), null, firstIf, sb.getCurrentIndentation());
                sb.append(block);
                firstIf = false;
            }
            sb.decreaseIndentation();
            sb.appendLine("}");
            firstBlock = false;
        }

        for (Bus faultedBus : network.getBusMap().values()) {
            if (!faultedBus.isFaultCandidate()) continue;

            sb.appendIndented(firstBlock ? "if" : "else if")
                    .append(" (FB==").append(faultedBus.getId()).appendRawLine("){");
            sb.increaseIndentation();
            boolean firstIf = true;
            for (CircuitBreaker cb : network.getCbMap().values()) {
                int cbLineId = cb.getLine().getId();
                String block = generateUpdateBlock(network, cbLineId, null, faultedBus.getId(), firstIf, sb.getCurrentIndentation());
                sb.append(block);
                firstIf = false;
            }
            sb.decreaseIndentation();
            sb.appendLine("}");
            firstBlock = false;
        }
        sb.decreaseIndentation();
        sb.appendLine("}");
        return sb.toString();
    }

    public String generateUpdateBlock(Network network, int cbLineId, Integer faultedLineId, Integer faultedBusId, boolean isFirst, int indentationLevel){
        if ((faultedLineId == null && faultedBusId == null) || (faultedLineId != null && faultedBusId != null)) {
            throw new IllegalArgumentException("Specify either a faulted line OR a faulted bus.");
        }

        Source source = network.getSources().get(0);
        Bus sourceBus = source.getSourceBus();
        Bus targetBus;

        if (faultedLineId != null) {
            Line faultedLine = network.getLine(faultedLineId);
            targetBus = faultedLine.getToBus();
        } else {
            targetBus = network.getBus(faultedBusId);
        }

        List<Line> path = network.getCachedPath(sourceBus, targetBus);
        if (path == null) throw new IllegalStateException("No path from source to fault.");
        Set<Line> pathSet = new HashSet<>(path);

        Line cbLine = network.getLine(cbLineId);
        IndentedStringBuilder sb = new IndentedStringBuilder(null, indentationLevel);

        sb.appendIndented(isFirst ? "if " : "else if ")
                .append("(CB_ID==").append(cbLineId).appendRawLine("){");
        sb.increaseIndentation();
        sb.appendAssign("C" + cbLineId, 0);

        if (pathSet.contains(cbLine)) {
            for (Line line : network.getLineMap().values()) {
                sb.appendAssign("Irc_" + line.getId(), 0);
            }
        } else {
            sb.appendAssign("Irc_" + cbLineId, 0);
        }
        sb.decreaseIndentation();
        sb.appendLine("}");
        return sb.toString();
    }

}
