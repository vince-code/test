package polimi.logic.generator.builder.template;

import com.uppaal.model.core2.*;
import polimi.logic.generator.builder.template.model.CircuitBreakerLocation;
import polimi.logic.generator.builder.template.model.EdgeType;
import polimi.model.CircuitBreaker;
import polimi.model.Network;
import polimi.util.IndentedStringBuilder;

import java.util.HashMap;
import java.util.Map;

public class CBTemplateBuilder extends TemplateBuilder {

    private final CircuitBreaker cb;

    public CBTemplateBuilder(CircuitBreaker circuitBreaker) {
        this.cb = circuitBreaker;
    }

    @Override
    public Template build(Network network, Document document) throws ModelException {
        int cbId = cb.getId();

        Template template = document.createTemplate();
        template.setProperty("name", "Circuit_Breaker_" + cbId);
        template.setProperty("declaration", generateCBDeclarations(cb));

        Map<CircuitBreakerLocation, Location> locationsMap = new HashMap<>();

        for (CircuitBreakerLocation cbLocation : CircuitBreakerLocation.values()){
            locationsMap.put(cbLocation, createLocation(template, cbLocation));
        }

        Edge tripEdge = template.addEdge(locationsMap.get(CircuitBreakerLocation.CLOSED), locationsMap.get(CircuitBreakerLocation.STANDBY));
        tripEdge.setProperty(EdgeType.guard.name(), "Irc_" + cbId + ">Ith_" + cbId);
        tripEdge.setProperty(EdgeType.synchronisation.name(), "Faults?");
        tripEdge.setProperty(EdgeType.assignment.name(), "x=0, triptime(Irc_" + cbId + ")");

        Edge closeEdge = template.addEdge(locationsMap.get(CircuitBreakerLocation.STANDBY), locationsMap.get(CircuitBreakerLocation.CLOSED));
        closeEdge.setProperty(EdgeType.guard.name(), "Irc_" + cbId + "<Ith_" + cbId);
        closeEdge.setProperty(EdgeType.synchronisation.name(), "Close?");

        Edge faultClearedEdge = template.addEdge(locationsMap.get(CircuitBreakerLocation.STANDBY), locationsMap.get(CircuitBreakerLocation.CLOSED));
        faultClearedEdge.setProperty(EdgeType.guard.name(), "Irc_" + cbId + "<Ith_" + cbId);
        faultClearedEdge.setProperty(EdgeType.synchronisation.name(), "Fault_cleared?");

        Edge openEdge = template.addEdge(locationsMap.get(CircuitBreakerLocation.STANDBY), locationsMap.get(CircuitBreakerLocation.OPEN));
        openEdge.setProperty(EdgeType.guard.name(), "x==t && Irc_" + cbId + ">=Ith_" + cbId);
        openEdge.setProperty(EdgeType.synchronisation.name(), "CBopen!");
        openEdge.setProperty(EdgeType.assignment.name(), "update(" + cbId + ")");

        Edge resetEdge = template.addEdge(locationsMap.get(CircuitBreakerLocation.OPEN), locationsMap.get(CircuitBreakerLocation.CLOSED));
        resetEdge.setProperty(EdgeType.synchronisation.name(), "Reset?");
        resetEdge.setProperty(EdgeType.assignment.name(), "clear(), C" + cbId + "=1");

        return template;
    }

    private static String generateCBDeclarations(CircuitBreaker cb) {
        IndentedStringBuilder declarations = new IndentedStringBuilder();

        declarations.appendLine("clock x;");
        declarations.newLine();
        declarations.appendLine("int t;");
        declarations.appendAssign("int m", cb.getM());
        declarations.appendAssign("int i1", cb.getI1());
        declarations.appendAssign("int t1", cb.getT1());
        declarations.appendAssign("int i2", cb.getI2());
        declarations.appendAssign("int t2", cb.getT2());

        declarations.appendLine("void triptime(int I) {").increaseIndentation();
        declarations.appendLine("if (I < i1) {").increaseIndentation();
        declarations.appendLine("t = 9999;");
        declarations.decreaseIndentation().appendLine("} else if (I >= i1 && I < i2) {").increaseIndentation();
        declarations.appendLine("t = (m * i1 * m * i1 * t1) / (I * I);");
        declarations.decreaseIndentation().appendLine("} else if (I >= i2) {").increaseIndentation();
        declarations.appendLine("t = t2;");        declarations.decreaseIndentation().appendLine("}");
        declarations.decreaseIndentation().appendLine("}");

        return declarations.toString();
    }
}
