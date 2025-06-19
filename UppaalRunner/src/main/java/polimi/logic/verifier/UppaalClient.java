package polimi.logic.verifier;

import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.QueryResult;
import com.uppaal.model.system.*;
import com.uppaal.model.system.symbolic.SymbolicState;
import com.uppaal.model.system.symbolic.SymbolicTrace;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import polimi.logic.engine.UppaalEngine;
import polimi.logic.engine.UppaalEngineException;
import polimi.logic.engine.UppaalEnginePool;
import polimi.model.VerificationResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UppaalClient {

    public VerificationResult verify(Document document, Query query, boolean withTrace) throws Exception {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }

        UppaalEngine engine = null;
        try {
            engine = UppaalEnginePool.getInstance().borrowEngine();

            return performVerification(engine, document, query, withTrace);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Exception("Verification interrupted", e);
        } catch (UppaalEngineException e) {
            throw new Exception("Engine error during verification", e);
        } finally {
            if (engine != null) {
                UppaalEnginePool.getInstance().returnEngine(engine);
            }
        }
    }

    private VerificationResult performVerification(UppaalEngine engine, Document document,
                                                   Query query, boolean withTrace) throws Exception {
        try {
            UppaalSystem uppaalSystem = engine.compile(document);

            DefaultQueryFeedback queryFeedback = new DefaultQueryFeedback();
            QueryResult queryResult = engine.query(uppaalSystem, query, queryFeedback);

            return analyzeQueryResult(queryResult, queryFeedback, uppaalSystem, withTrace);

        } catch (UppaalEngineException e) {
            throw new Exception("UPPAAL engine error during verification", e);
        } catch (Exception e) {
            throw new Exception("Unexpected error during verification", e);
        }
    }

    private VerificationResult analyzeQueryResult( QueryResult queryResult, DefaultQueryFeedback queryFeedback, UppaalSystem uppaalSystem, boolean withTrace) {
        VerificationResult richResult;
        if (queryResult.toString().equals("OK")){
            SymbolicTrace symbolicTrace = queryFeedback.getSymbolicTrace();

            List<Integer> standByCBs = new ArrayList<>();
            String fault = null;
            for (SymbolicTransition transition : symbolicTrace) {
                for (SystemEdgeSelect sel : transition.getEdges()) {
                    String sourceLocation = ((SystemEdge) sel).getEdge().getSource().getPropertyValue("name");
                    String targetLocation = ((SystemEdge) sel).getEdge().getTarget().getPropertyValue("name");

                    String process = ((SystemEdge) sel).getProcessName();
                    if (process.equals("FG") && sel.getName().contains("_fault!")){
                        fault = "F" + sel.getName().split("L")[1].split("_")[0];
                    }
                    if ("Closed".equals(sourceLocation) && "Standby".equals(targetLocation) && process.startsWith("CB")) {
                        int cbId = Integer.parseInt(process.replace("CB", ""));
                        if (!standByCBs.contains(cbId)) {
                            standByCBs.add(cbId);
                        }
                    }
                }
            }
            if (withTrace){
                String trace = generateTrace(uppaalSystem, symbolicTrace);
                richResult = new VerificationResult(true, fault, standByCBs, trace);
            } else {
                richResult = new VerificationResult(true, fault, standByCBs, null);
            }
        } else {
            richResult = new VerificationResult(false,null,null,null);
        }

        return richResult;
    }

    public String generateTrace(UppaalSystem uppaalSystem, SymbolicTrace symbolicTrace) {
        StringBuilder trace = new StringBuilder();
        if (symbolicTrace == null) {
            trace.append("(null trace)").append("\n");
            return trace.toString();
        } else {
            Iterator<SymbolicTransition> iterator = symbolicTrace.iterator();
            print(trace, uppaalSystem, iterator.next().getTarget());

            while(iterator.hasNext()) {
                SymbolicTransition transition = iterator.next();

                if (transition.getSize() == 0) {
                    trace.append(transition.getEdgeDescription());
                } else {
                    boolean first = true;
                    for(SystemEdgeSelect edgeSelect : transition.getEdges()) {
                        if (!first) {
                            trace.append(", ");
                        }
                        first = false;

                        SystemEdge edge = (SystemEdge) edgeSelect;
                        String processName = edge.getProcessName();

                        Object sourceNameObj = edge.getEdge().getSource().getPropertyValue("name");
                        Object targetNameObj = edge.getEdge().getTarget().getPropertyValue("name");
                        String sourceName = sourceNameObj != null ? sourceNameObj.toString() : "null";
                        String targetName = targetNameObj != null ? targetNameObj.toString() : "null";

                        trace.append(processName).append(": ").append(sourceName).append(" → ").append(targetName);
                    }
                }

                trace.append("\n");
                print(trace, uppaalSystem, transition.getTarget());
            }

            trace.append("\n");
            return trace.toString();
        }
    }

    public StringBuilder print(StringBuilder traceBuilder, UppaalSystem uppaalSystem, SymbolicState symbolicState) {
        traceBuilder.append("(");
        boolean var3 = true;

        for(SystemLocation systemLocation : symbolicState.getLocations()) {
            if (var3) {
                var3 = false;
            } else {
                traceBuilder.append(", ");
            }

            traceBuilder.append(systemLocation.getName());
        }

        int[] variableValues = symbolicState.getVariableValues();

        for(int i = 0; i < uppaalSystem.getNoOfVariables(); ++i) {
            traceBuilder.append(", ");
            String variableName = uppaalSystem.getVariableName(i);
            traceBuilder.append(variableName).append("=").append(variableValues[i]);
        }

        ArrayList<String> var10 = new ArrayList<>();
        symbolicState.getPolyhedron().getAllConstraints(var10);

        for(String var12 : var10) {
            traceBuilder.append(", ");
            traceBuilder.append(var12);
        }

        traceBuilder.append(")").append("\n");
        return traceBuilder;
    }

}
