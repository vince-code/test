package polimi.logic.modelGenerator.builder.declarations;

import polimi.model.*;
import polimi.logic.modelGenerator.IndentedStringBuilder;
import java.util.Comparator;

public class GlobalVariablesBuilder {

    private final Network network;

    public GlobalVariablesBuilder(Network network) {
        this.network = network;
    }

    public String build() {
        int numLines = network.getLineMap().size();
        int numBuses = network.getBusMap().size();
        int numCBs = network.getCbMap().size();

        IndentedStringBuilder sb = new IndentedStringBuilder(
                1024 + (numLines + numBuses + numCBs) * 80
        );

        IndentedStringBuilder lineFaultChannels = new IndentedStringBuilder(numLines * 20);
        IndentedStringBuilder busFaultChannels = new IndentedStringBuilder(numBuses * 20);
        IndentedStringBuilder iioc = new IndentedStringBuilder(numLines * 40);
        IndentedStringBuilder ith = new IndentedStringBuilder(numLines * 40);
        IndentedStringBuilder irc = new IndentedStringBuilder(numLines * 40);
        IndentedStringBuilder lineFaultFlags = new IndentedStringBuilder(numLines * 30);
        IndentedStringBuilder isFWDFlags = new IndentedStringBuilder(numLines * 35);
        IndentedStringBuilder busFaultFlags = new IndentedStringBuilder(numBuses * 30);
        IndentedStringBuilder cbStatus = new IndentedStringBuilder(numCBs * 25);
        IndentedStringBuilder blockFlags = new IndentedStringBuilder(numCBs * 35);

        for (Line line : network.getLineMap().values()) {
            int id = line.getId();
            lineFaultChannels.append("L").append(id).append("_fault, ");
            iioc.appendAssign("int Iioc_" + id, line.getIioc());
            ith.appendAssign("int Ith_" + id, line.getIth());
            irc.appendAssign("int Irc_" + id, line.getIioc());
            lineFaultFlags.appendAssign("bool F" + id, "false");
            isFWDFlags.appendAssign("bool IsFWD_" + id, "true");
        }

        for (Bus bus : network.getBusMap().values()) {
            int id = bus.getId();
            busFaultChannels.append("LB").append(id).append("_fault, ");
            busFaultFlags.appendAssign("bool FB" + id, "false");
        }

        for (CircuitBreaker cb : network.getCbMap().values()) {
            int id = cb.getId();
            cbStatus.appendAssign("int C" + id, "1");
            blockFlags.appendAssign("bool Block_" + id, "false");
        }

        sb.append("broadcast chan ")
                .append(lineFaultChannels, 0, lineFaultChannels.length() - 2)
                .appendRawLine(";");

        sb.append("broadcast chan ")
                .append(busFaultChannels, 0, busFaultChannels.length() - 2)
                .appendRawLine(";");

        sb.appendLine("broadcast chan CBopen, Faults, Reset, Fault_cleared, Close;").newLine();

        sb.append(iioc).newLine();
        sb.append(ith).newLine();

        for (Source source : network.getSources()) {
            int srcId = source.getSourceBus().getId();

            source.getIscLines().keySet().stream()
                    .sorted(Comparator.comparingInt(Integer::intValue))
                    .forEach(lineId -> {
                        int value = source.getIscLines().get(lineId);
                        sb.appendAssign("int Isc_" + srcId + "_" + lineId, value);
                    });
            sb.newLine();
            source.getIscBuses().keySet().stream()
                    .sorted(Comparator.comparingInt(Integer::intValue))
                    .forEach(busId -> {
                        int value = source.getIscBuses().get(busId);
                        sb.appendAssign("int Iscb_" + srcId + "_" + busId, value);
                    });
        }
        sb.newLine();

        sb.append(irc).newLine();
        sb.append(lineFaultFlags).newLine();
        sb.append(busFaultFlags).newLine();

        sb.appendLine("int F;");
        sb.appendLine("int FB;").newLine();

        sb.append(cbStatus).newLine();
        sb.append(isFWDFlags).newLine();
        sb.append(blockFlags);

        return sb.toString();
    }
}
