package polimi.logic.modelGenerator.builder;

import polimi.model.CircuitBreaker;
import polimi.model.Network;

public class SystemDeclarationsBuilder {
    public static String build(Network network) {
        StringBuilder declarations = new StringBuilder("FG = Fault_Generator();\n\n");
        StringBuilder systemLine = new StringBuilder("system FG");

        for (CircuitBreaker cb : network.getCbMap().values()) {
            int cbId = cb.getId();
            declarations.append("CB").append(cbId).append(" = Circuit_Breaker_").append(cbId).append("();\n");
            systemLine.append(", CB").append(cbId);
        }

        systemLine.append(";\n");

        return declarations.append("\n").append(systemLine).toString();
    }
}
