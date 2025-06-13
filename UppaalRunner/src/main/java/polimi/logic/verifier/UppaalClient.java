package polimi.logic.verifier;

import com.uppaal.engine.*;
import com.uppaal.engine.connection.BundledConnection;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.EngineSettings;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.QueryResult;
import com.uppaal.model.io2.Problem;
import com.uppaal.model.system.*;
import com.uppaal.model.system.symbolic.SymbolicState;
import com.uppaal.model.system.symbolic.SymbolicTrace;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import polimi.model.VerificationResult;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UppaalClient implements AutoCloseable{

    private Engine uppaalEngine;
    private final String uppaalHome;

    public UppaalClient(String uppaalHome) throws IOException {
        this.uppaalHome = uppaalHome;
        uppaalEngine = connectToEngine(uppaalHome);
    }

    public VerificationResult verify(Document document, Query query, boolean withTrace) throws Exception {
        if (uppaalEngine == null) {
            uppaalEngine = connectToEngine(uppaalHome);
        }

        VerificationResult richResult;
        EngineOptions engineOptions = uppaalEngine.getOptions().get();
        EngineSettings engineSettings = engineOptions.getDefaultSettings();
        engineSettings.setValue("--diagnostic", "2");
        uppaalEngine.setOptionSettings(engineSettings);

        UppaalSystem uppaalSystem = compile(uppaalEngine, document);
        DefaultQueryFeedback queryFeedback = new DefaultQueryFeedback();

        QueryResult queryResult = (QueryResult) uppaalEngine.query(uppaalSystem, query, queryFeedback).get();
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

    private Engine connectToEngine(String uppaalHome) throws IOException {

        if (uppaalHome == null) {
            throw new IOException("Uppaal Home not found");
        } else {
            Engine engine = new Engine();
            engine.addConnection(new BundledConnection(new BinaryResolution(new File(uppaalHome))));
            engine.connect();
            return engine;
        }
    }

    @Override
    public void close() {
        if (uppaalEngine == null) return;
        try {
            uppaalEngine.forceDisconnect(2000).get();
        } catch (Exception e) {
            System.err.println("Error disconnecting UPPAAL engine: " + e.getMessage());
        }
        shutdownEngineExecutor(uppaalEngine);
        tryClearInternalReferences();
        uppaalEngine = null;
    }

    private UppaalSystem compile(Engine engine, Document document) throws InterruptedException, ExecutionException {
        ArrayList<Problem> problems = new ArrayList<>();
        UppaalSystem uppaalSystem = (UppaalSystem)engine.getSystem(document, problems).get();
        if (!problems.isEmpty()) {
            boolean terminateProgram = false;
            System.out.println("There are problems with the document:");

            for(Problem problem : problems) {
                System.out.println(problem.toString());
                if (!"warning".equals(problem.getType())) {
                    terminateProgram = true;
                }
            }

            if (terminateProgram) {
                System.exit(1);
            }
        }

        return uppaalSystem;
    }

    private void shutdownEngineExecutor(Engine engine) {
        try {
            java.lang.reflect.Field executorField = Engine.class.getDeclaredField("executor");
            executorField.setAccessible(true);
            java.util.concurrent.ExecutorService executor = (java.util.concurrent.ExecutorService) executorField.get(engine);
            if (executor != null && !executor.isShutdown()) {
                executor.shutdownNow();
                executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void tryClearInternalReferences() {
        try {
            Field connField = Engine.class.getDeclaredField("connections");
            connField.setAccessible(true);
            Object connections = connField.get(uppaalEngine);
            if (connections instanceof List<?>) {
                ((List<?>) connections).clear();
            }
        } catch (Exception ignored) {
        }
    }

}
