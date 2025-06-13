package polimi.logic.generator.builder.declarations.functions;

import polimi.model.CircuitBreaker;
import polimi.model.Line;
import polimi.model.Network;
import polimi.util.IndentedStringBuilder;

public class ClearFunctionBuilder {

    private final Network network;

    public ClearFunctionBuilder(Network network) {
        this.network = network;
    }

    public String build() {
        int numLines = network.getLineMap().size();
        int numCBs = network.getCbMap().size();

        IndentedStringBuilder irc = new IndentedStringBuilder(32 + numLines * 30);
        IndentedStringBuilder fwd = new IndentedStringBuilder(numLines * 30 + numCBs * 30);

        irc.appendLine("void clear() {");
        irc.increaseIndentation();
        fwd.increaseIndentation();
        for (Line line : network.getLineMap().values()) {
            int id = line.getId();
            irc.appendAssign("Irc_" + id, "Iioc_" + id);
            fwd.appendAssign("IsFWD_" + id, "true");
        }

        for (CircuitBreaker cb : network.getCbMap().values()) {
            int id = cb.getId();
            fwd.appendAssign("Block_" + id, "false");
        }
        fwd.decreaseIndentation();
        fwd.appendLine("}");
        return irc.append(fwd).toString();
    }
}
