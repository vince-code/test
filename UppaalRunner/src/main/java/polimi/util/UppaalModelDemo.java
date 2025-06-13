package polimi.util;

import com.uppaal.engine.*;
import com.uppaal.engine.connection.BundledConnection;
import com.uppaal.model.core2.*;
import com.uppaal.model.io2.Problem;
import com.uppaal.model.io2.XTAReaderParsing.ParseException;
import com.uppaal.model.system.SystemEdge;
import com.uppaal.model.system.SystemEdgeSelect;
import com.uppaal.model.system.SystemLocation;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.concrete.ConcreteState;
import com.uppaal.model.system.concrete.ConcreteTrace;
import com.uppaal.model.system.concrete.Limit;
import com.uppaal.model.system.symbolic.SymbolicState;
import com.uppaal.model.system.symbolic.SymbolicTrace;
import com.uppaal.model.system.symbolic.SymbolicTransition;

import javax.xml.stream.XMLStreamException;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UppaalModelDemo {
    static SymbolicTrace strace = null;
    static ConcreteTrace ctrace = null;
    public static QueryFeedback qf = new QueryFeedback() {
        public void setProgressAvail(boolean var1) {
        }

        public void setProgress(int var1, long var2, long var4, long var6, long var8, long var10, long var12, long var14, long var16, long var18) {
        }

        public void setSystemInfo(long var1, long var3, long var5) {
        }

        public void setLength(int var1) {
        }

        public void setCurrent(int var1) {
        }

        public void setTrace(char var1, String var2, SymbolicTrace var3, QueryResult var4) {
            UppaalModelDemo.strace = var3;
        }

        public void setTrace(char var1, String var2, ConcreteTrace var3, QueryResult var4) {
            UppaalModelDemo.ctrace = var3;
        }

        public void setFeedback(String var1) {
            if (var1 != null && var1.length() > 0) {
                System.out.println("Feedback: " + var1);
            }

        }

        public void appendText(String var1) {
            if (var1 != null && var1.length() > 0) {
                System.out.println("Append: " + var1);
            }

        }

        public void setResultText(String var1) {
            if (var1 != null && var1.length() > 0) {
                System.out.println("Result: " + var1);
            }

        }
    };

    public UppaalModelDemo() {
    }

    public static Property addLabel(Location var0, LKind var1, Object var2, int var3, int var4) {
        Property var5 = var0.setProperty(var1.name(), var2);
        return var5.setXY(var3, var4);
    }

    public static Location addLocation(Template template, String var1, String var2, int var3, int var4) {
        Location var5 = template.addLocation();
        var5.setXY(var3, var4);
        var3 += 8;
        if (var1 != null && !var1.isBlank()) {
            var4 -= 25;
            addLabel((Location)var5, (LKind) LKind.name, (Object)var1, var3, var4);
        }

        if (var2 != null && !var2.isBlank()) {
            var4 -= 18;
            addLabel((Location)var5, (LKind) LKind.exponentialrate, (Object)var2, var3, var4);
        }

        return var5;
    }

    public static Property addLabel(Edge var0, EKind var1, String var2, int var3, int var4) {
        Property var5 = var0.setProperty(var1.name(), var2);
        return var5.setXY(var3, var4);
    }

    public static Edge addEdge(Template template, Location location, Location location1, String var3, String var4, String var5) throws ModelException {
        Edge edge = template.addEdge(location, location1);
        int var7 = (location.getX() + location1.getX()) / 2 + 8;
        int var8 = (location.getY() + location1.getY()) / 2;
        if (var5 != null && !var5.isBlank()) {
            addLabel(edge, EKind.assignment, var5, var7, var8);
            var8 -= 18;
        }

        if (var4 != null && !var4.isBlank()) {
            addLabel(edge, EKind.synchronisation, var4, var7, var8);
            var8 -= 18;
        }

        if (var3 != null && !var3.isBlank()) {
            addLabel(edge, EKind.guard, var3, var7, var8);
        }

        return edge;
    }

    public static Document createSampleModel() throws ModelException {
        Document document = new Document(new DocumentPrototype());
        document.setProperty("declaration", "int v;\n\nclock x,y,z;");
        Template template = document.addTemplate();
        template.setProperty("name", "Experiment");

        Location locationL0 = addLocation(template, "L0", "1", 0, 0);
        locationL0.setProperty("init", true);

        Location locationL1 = addLocation(template, "L1", (String)null, 162, 0);
        addLabel((Location)locationL1, (LKind) LKind.invariant, (Object)"x<=10", locationL1.getX() + 8, locationL1.getY());
        Location locationL2 = addLocation(template, "L2", (String)null, 162, 162);
        addLabel((Location)locationL2, (LKind) LKind.invariant, (Object)"y<=20", locationL2.getX() + 8, locationL2.getY());
        Location locationL3 = addLocation(template, "L3", "1", 0, 162);

        Location locationError = addLocation(template, "Error", "1", 0, 324);
        Location locationGoal = addLocation(template, "Goal", (String)null, -162, 162);

        addEdge(template, locationL0, locationL1, "v<2", (String)null, "v=1,\nx=0").setProperty(EKind.comments.name(), "Enabled only when v=1").setY(locationL0.getY() - 64);
        addEdge(template, locationL0, locationError, (String)null, (String)null, (String)null).addNail(locationGoal.getX() - 36, locationL0.getY()).addNail(locationGoal.getX() - 36, locationError.getY()).setProperty(EKind.guard.name(), "v==6").setXY(locationL0.getX() - 90, locationL0.getY() - 18);
        addEdge(template, locationL1, locationL2, "x>=5", (String)null, "v=2,\ny=0");
        addEdge(template, locationL2, locationL3, "y>=10", (String)null, "v=3,\nz=0");
        addEdge(template, locationL3, locationL0, "x>=100", (String)null, "v=4");
        addEdge(template, locationL3, locationError, "v==7", (String)null, "v=6");
        addEdge(template, locationL3, locationGoal, "v==4", (String)null, "v=5");

        document.setProperty("system", "Exp1 = Experiment();\nExp2 = Experiment();\n\nsystem Exp1, Exp2;");

        QueryList queryList = document.getQueryList();
        queryList.add(new Query("E<> Exp1.Goal", "Can Exp1 reach Goal?"));
        queryList.add(new Query("E<> Exp1.Error", "Can Exp1 reach Error?"));
        queryList.add(new Query("Pr[<=1000](<> Exp1.Goal)", "What is the probability of Exp1 reaching Goal?"));
        queryList.add(new Query("simulate [<=1000] { v, x, y }", "get simulation trajectories"));
        return document;
    }

    public static Document loadModel(String var0) throws IOException {
        return Document.load(var0);
    }

    public static Engine connectToEngine(String uppaalHome) throws EngineException, IOException {

        if (uppaalHome == null) {
            File var3 = new File(".");

            while(var3.getParentFile() != null) {
                if ((new File(var3, "uppaal.jar")).exists()) {
                    uppaalHome = var3.getAbsolutePath();
                } else {
                    var3 = var3.getParentFile();
                }
            }

            throw new IOException("Set UPPAAL_HOME environment variable to Uppaal installation path");
        } else {
            Engine var1 = new Engine();
            var1.addConnection(new BundledConnection(new BinaryResolution(new File(uppaalHome))));
            var1.connect();
            return var1;
        }
    }

    public static Engine connectToEngine() throws EngineException, IOException {
        String uppaalHome = System.getenv("UPPAAL_HOME");
        if (uppaalHome == null) {
            File var3 = new File(".");

            while(var3.getParentFile() != null) {
                if ((new File(var3, "uppaal.jar")).exists()) {
                    uppaalHome = var3.getAbsolutePath();
                } else {
                    var3 = var3.getParentFile();
                }
            }

            throw new IOException("Set UPPAAL_HOME environment variable to Uppaal installation path");
        } else {
            Engine var1 = new Engine();
            var1.addConnection(new BundledConnection(new BinaryResolution(new File(uppaalHome))));
            var1.connect();
            return var1;
        }
    }

    public static UppaalSystem compile(Engine engine, Document document) throws EngineException, IOException, InterruptedException, ExecutionException {
        ArrayList<Problem> problems = new ArrayList<>();
        UppaalSystem var3 = (UppaalSystem)engine.getSystem(document, problems).get();
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

        return var3;
    }

    public static PrintStream print(PrintStream printStream, UppaalSystem uppaalSystem, SymbolicState symbolicState) {
        printStream.print("(");
        boolean var3 = true;

        for(SystemLocation systemLocation : symbolicState.getLocations()) {
            if (var3) {
                var3 = false;
            } else {
                printStream.print(", ");
            }

            printStream.print(systemLocation.getName());
        }

        int[] var8 = symbolicState.getVariableValues();

        for(int var9 = 0; var9 < uppaalSystem.getNoOfVariables(); ++var9) {
            printStream.print(", ");
            String var10001 = uppaalSystem.getVariableName(var9);
            printStream.print(var10001 + "=" + var8[var9]);
        }

        ArrayList<String> var10 = new ArrayList<>();
        symbolicState.getPolyhedron().getAllConstraints(var10);

        for(String var12 : var10) {
            printStream.print(", ");
            printStream.print(var12);
        }

        printStream.println(")");
        return printStream;
    }

    public static PrintStream print(PrintStream printStream, UppaalSystem uppaalSystem, ConcreteState concreteState) {
        printStream.print("(");
        boolean var3 = true;

        for(SystemLocation var7 : concreteState.getLocations()) {
            if (var3) {
                var3 = false;
            } else {
                printStream.print(", ");
            }

            printStream.print(var7.getName());
        }

        Limit var9 = concreteState.getInvariant();
        if (!var9.isUnbounded()) {
            if (var9.isStrict()) {
                printStream.print(", limit<");
            } else {
                printStream.print(", limit≤");
            }

            printStream.print(var9.getDoubleValue());
        }

        int var10 = 0;
        int[] var13 = concreteState.getVars();

        for(int var14 = 0; var14 < var13.length; ++var14) {
            if (var10 + var14 > 0) {
                printStream.print(", ");
            }

            String var10001 = uppaalSystem.getVariableName(var14);
            printStream.print(var10001 + " = " + var13[var14]);
        }

        var10 += var13.length;
        double[] var15 = concreteState.getFPVars();

        for(int var8 = 0; var8 < var15.length; ++var8) {
            if (var10 + var8 > 0) {
                printStream.print(", ");
            }

            String var18 = uppaalSystem.getVariableName(var10 + var8);
            printStream.print(var18 + " = " + var15[var8]);
        }

        var10 += var15.length;
        var15 = concreteState.getClocks();

        for(int var17 = 0; var17 < var15.length; ++var17) {
            if (var10 + var17 > 0) {
                printStream.print(", ");
            }

            String var19 = uppaalSystem.getClockName(var17);
            printStream.print(var19 + " = " + var15[var17]);
        }

        printStream.println(")");
        return printStream;
    }

    public static PrintStream printTrace(PrintStream printStream, UppaalSystem uppaalSystem, SymbolicTrace symbolicTrace) {
        if (symbolicTrace == null) {
            printStream.println("(null trace)");
            return printStream;
        } else {
            Iterator<SymbolicTransition> iterator = symbolicTrace.iterator();
            print(printStream, uppaalSystem, iterator.next().getTarget());

            while(iterator.hasNext()) {
                SymbolicTransition transition = iterator.next();

                if (transition.getSize() == 0) {
                    printStream.println(transition.getEdgeDescription());
                } else {
                    boolean first = true;
                    for(SystemEdgeSelect edgeSelect : transition.getEdges()) {
                        if (!first) {
                            printStream.print(", ");
                        }
                        first = false;

                        SystemEdge edge = (SystemEdge) edgeSelect;
                        String processName = edge.getProcessName();

                        Object sourceNameObj = edge.getEdge().getSource().getPropertyValue("name");
                        Object targetNameObj = edge.getEdge().getTarget().getPropertyValue("name");
                        String sourceName = sourceNameObj != null ? sourceNameObj.toString() : "null";
                        String targetName = targetNameObj != null ? targetNameObj.toString() : "null";

                        printStream.print(processName + ": " + sourceName + " → " + targetName);
                    }
                }

                printStream.println();
                print(printStream, uppaalSystem, transition.getTarget());
            }

            printStream.println();
            return printStream;
        }
    }

    public static PrintStream printTrace(PrintStream printStream, UppaalSystem uppaalSystem, ConcreteTrace concreteTrace) {
        if (concreteTrace == null) {
            printStream.println("(null trace)");
            return printStream;
        } else {
            Iterator<ConcreteTraceElement> iterator = concreteTrace.iterator();
            print(printStream, uppaalSystem, iterator.next().getTarget());

            while(iterator.hasNext()) {
                ConcreteTraceElement transition = iterator.next();

                if (transition.getSize() == 0) {
                    printStream.println(transition.getTransitionDescription());
                } else {
                    boolean first = true;
                    for(SystemEdgeSelect edgeSelect : transition.getEdges()) {
                        if (!first) {
                            printStream.print(", ");
                        }
                        first = false;

                        SystemEdge edge = (SystemEdge) edgeSelect;
                        String processName = edge.getProcessName();

                        Object sourceNameObj = edge.getEdge().getSource().getPropertyValue("name");
                        Object targetNameObj = edge.getEdge().getTarget().getPropertyValue("name");
                        String sourceName = sourceNameObj != null ? sourceNameObj.toString() : "null";
                        String targetName = targetNameObj != null ? targetNameObj.toString() : "null";

                        printStream.print(processName + ": " + sourceName + " → " + targetName);
                    }
                }

                printStream.println();
                print(printStream, uppaalSystem, transition.getTarget());
            }

            printStream.println();
            return printStream;
        }
    }

    public static PrintStream print(PrintStream printStream, QueryResult queryResult) {
        for(DataSet2D var3 : queryResult.getPlots()) {
            String var10001 = var3.getTitle();
            printStream.println("Plot \"" + var10001 + "\" showing \"" + var3.getYLabel() + "\" over \"" + var3.getXLabel() + "\"");

            for(Data2D var5 : var3) {
                printStream.print("Trajectory " + var5.getTitle() + ":");

                for(Point2D.Double var7 : var5) {
                    printStream.print(" (" + var7.x + "," + var7.y + ")");
                }

                printStream.println();
            }
        }

        return printStream;
    }

    public static SymbolicTrace symbolicSimulation(Engine engine, UppaalSystem uppaalSystem) throws EngineException, IOException, CannotEvaluateException, InterruptedException, ExecutionException {
        PrintStream printStream = System.out;
        SymbolicTrace trace = new SymbolicTrace();
        SymbolicState state = (SymbolicState)engine.getInitialState(uppaalSystem).get();
        trace.add(new SymbolicTransition((SymbolicState)null, (SystemEdgeSelect[])null, state));

        while(state != null) {
            print(printStream, uppaalSystem, state);
            List<SymbolicTransition> symbolicTransitions = engine.getTransitions(uppaalSystem, state).get();

            int index = (int) Math.floor(Math.random() * (double) symbolicTransitions.size());
            SymbolicTransition transition = (SymbolicTransition)symbolicTransitions.get(index);

            if (transition.getSize() == 0) {
                printStream.print(transition.getEdgeDescription());
            } else {
                boolean first = true;

                for(SystemEdgeSelect edgeSelect : transition.getEdges()) {
                    if (first) {
                        first = false;
                    } else {
                        printStream.print(", ");
                    }

                    SystemEdge edge = (SystemEdge) edgeSelect;
                    String processName = edge.getProcessName();

                    Object sourceNameObj = edge.getEdge().getSource().getPropertyValue("name");
                    Object targetNameObj = edge.getEdge().getTarget().getPropertyValue("name");
                    String sourceName = sourceNameObj != null ? sourceNameObj.toString() : "null";
                    String targetName = targetNameObj != null ? targetNameObj.toString() : "null";

                    printStream.print(processName + ": " + sourceName + " → " + targetName);
                }
            }

            printStream.println();
            state = transition.getTarget();
            if (state != null) {
                trace.add(transition);
            }
        }

        return trace;
    }

    public static OutputStreamWriter saveXTR(OutputStreamWriter outputStreamWriter, SymbolicTrace symbolicTrace) throws IOException {
        Iterator<SymbolicTransition> symbolicTraceIterator = symbolicTrace.iterator();
        ((SymbolicTransition)symbolicTraceIterator.next()).getTarget().writeXTRFormat(outputStreamWriter);

        while(symbolicTraceIterator.hasNext()) {
            ((SymbolicTransition)symbolicTraceIterator.next()).writeXTRFormat(outputStreamWriter);
        }

        outputStreamWriter.write(".\n");
        outputStreamWriter.flush();
        return outputStreamWriter;
    }

    public static void workflows(PrintStream printStream, Document document, String uppaalHome) throws EngineException, IOException, ExecutionException, InterruptedException, CannotEvaluateException {
        Engine engine = connectToEngine(uppaalHome);
        UppaalSystem uppaalSystem = compile(engine, document);

        printStream.println("===== Random symbolic simulation =====");
        strace = symbolicSimulation(engine, uppaalSystem);

        printStream.println("===== Trace saving and loading =====");
        saveXTR(new FileWriter("demo.polimi.util.ModelDemo.xtr"), strace).close();

        Parser parser = new Parser(new FileInputStream("demo.polimi.util.ModelDemo.xtr"));
        strace = parser.parseXTRTrace(uppaalSystem);
        printTrace(printStream, uppaalSystem, strace);

        EngineOptions engineOptions = (EngineOptions)engine.getOptions().get();
        EngineSettings engineSettings = engineOptions.getDefaultSettings();

        printStream.println("==== Engine Settings ===");
        engineSettings.setValue("--diagnostic", "0");
        engineSettings.getProperties().forEach((key, value) -> {
            Object valObj = value.getValue();
            String valStr;

            if (valObj instanceof char[]) {
                valStr = new String((char[]) valObj);
            } else {
                valStr = valObj.toString();
            }

            printStream.println("  " + key + ": " + valStr);
        });

        engine.setOptionSettings(engineSettings);

        Query query1 = new Query("E<> Exp1.Goal");
        printStream.println("=== Symbolic check: " + query1.getFormula() + " ===");
        strace = null;
        QueryResult queryResult = (QueryResult)engine.query(uppaalSystem, query1, qf).get();
        printStream.println("Result: " + String.valueOf(queryResult));
        printTrace(printStream, uppaalSystem, strace);

        Query query2 = new Query("E<> Exp1.Error");
        printStream.println("=== Symbolic check: " + query2.getFormula() + " ===");
        strace = null;
        queryResult = (QueryResult)engine.query(uppaalSystem, query2, qf).get();
        printStream.println("Result: " + String.valueOf(queryResult));
        printTrace(printStream, uppaalSystem, strace);

        Query query3 = new Query("Pr[<=1000](<> Exp1.Goal)");
        printStream.println("=== SMC check: " + query3.getFormula() + " ===");
        ctrace = null;
        queryResult = (QueryResult)engine.query(uppaalSystem, query3, qf).get();
        printStream.println("Result: " + String.valueOf(queryResult));
        printTrace(printStream, uppaalSystem, ctrace);
        print(printStream, queryResult);

        Query query4 = new Query("simulate [<=30] { v, x, y }");
        printStream.println("=== Simulation: " + query4.getFormula() + " ===");
        queryResult = (QueryResult)engine.query(uppaalSystem, query4, qf).get();
        printStream.println("Result: " + String.valueOf(queryResult));
        print(printStream, queryResult);

        int variableVIndex = -1;

        for(int i = 0; i < uppaalSystem.getNoOfVariables(); ++i) {
            if ("v".equals(uppaalSystem.getVariableName(i))) {
                variableVIndex = i;
                break;
            }
        }

        SymbolicState symbolicState = (SymbolicState)engine.getInitialState(uppaalSystem).get();
        int[] variableValues = symbolicState.getVariableValues();
        if (variableVIndex >= 0 && variableVIndex < variableValues.length) {
            symbolicState.getVariableValues()[variableVIndex] = 6;
            symbolicState.getPolyhedron().addNonStrictConstraint(1, 0, 5);
            symbolicState.getPolyhedron().addNonStrictConstraint(0, 1, -5);
            printStream.println("=== Custom state check: " + query1.getFormula() + " === (expect NOT_OK)");
            strace = null;
            queryResult = (QueryResult)engine.query(uppaalSystem, symbolicState, query1, qf).get();
            printStream.println("Result: " + String.valueOf(queryResult));
            printTrace(printStream, uppaalSystem, strace);
            printStream.println("=== Custom state check: " + query2.getFormula() + " === (expect OK)");
            strace = null;
            queryResult = (QueryResult)engine.query(uppaalSystem, symbolicState, query2, qf).get();
            printStream.println("Result: " + String.valueOf(queryResult));
            printTrace(printStream, uppaalSystem, strace);
            engine.forceDisconnect(10).get();
        } else {
            throw new IOException("Value for variable \"v\" was not found");
        }
    }

    public static void workflows(PrintStream printStream, Document document) throws EngineException, IOException, ExecutionException, InterruptedException, CannotEvaluateException {
        Engine engine = connectToEngine();
        UppaalSystem uppaalSystem = compile(engine, document);

        printStream.println("===== Random symbolic simulation =====");
        strace = symbolicSimulation(engine, uppaalSystem);

        printStream.println("===== Trace saving and loading =====");
        saveXTR(new FileWriter("demo.polimi.util.ModelDemo.xtr"), strace).close();

        Parser parser = new Parser(new FileInputStream("demo.polimi.util.ModelDemo.xtr"));
        strace = parser.parseXTRTrace(uppaalSystem);
        printTrace(printStream, uppaalSystem, strace);

        EngineOptions engineOptions = (EngineOptions)engine.getOptions().get();
        EngineSettings engineSettings = engineOptions.getDefaultSettings();

        printStream.println("==== Engine Settings ===");
        engineSettings.setValue("--diagnostic", "0");
        engineSettings.getProperties().forEach((key, value) -> {
            Object valObj = value.getValue();
            String valStr;

            if (valObj instanceof char[]) {
                valStr = new String((char[]) valObj);
            } else {
                valStr = valObj.toString();
            }

            printStream.println("  " + key + ": " + valStr);
        });

        engine.setOptionSettings(engineSettings);

        Query query1 = new Query("E<> Exp1.Goal");
        printStream.println("=== Symbolic check: " + query1.getFormula() + " ===");
        strace = null;
        QueryResult queryResult = (QueryResult)engine.query(uppaalSystem, query1, qf).get();
        printStream.println("Result: " + String.valueOf(queryResult));
        printTrace(printStream, uppaalSystem, strace);

        Query query2 = new Query("E<> Exp1.Error");
        printStream.println("=== Symbolic check: " + query2.getFormula() + " ===");
        strace = null;
        queryResult = (QueryResult)engine.query(uppaalSystem, query2, qf).get();
        printStream.println("Result: " + String.valueOf(queryResult));
        printTrace(printStream, uppaalSystem, strace);

        Query query3 = new Query("Pr[<=1000](<> Exp1.Goal)");
        printStream.println("=== SMC check: " + query3.getFormula() + " ===");
        ctrace = null;
        queryResult = (QueryResult)engine.query(uppaalSystem, query3, qf).get();
        printStream.println("Result: " + String.valueOf(queryResult));
        printTrace(printStream, uppaalSystem, ctrace);
        print(printStream, queryResult);

        Query query4 = new Query("simulate [<=30] { v, x, y }");
        printStream.println("=== Simulation: " + query4.getFormula() + " ===");
        queryResult = (QueryResult)engine.query(uppaalSystem, query4, qf).get();
        printStream.println("Result: " + String.valueOf(queryResult));
        print(printStream, queryResult);

        int variableVIndex = -1;

        for(int i = 0; i < uppaalSystem.getNoOfVariables(); ++i) {
            if ("v".equals(uppaalSystem.getVariableName(i))) {
                variableVIndex = i;
                break;
            }
        }

        SymbolicState symbolicState = (SymbolicState)engine.getInitialState(uppaalSystem).get();
        int[] variableValues = symbolicState.getVariableValues();
        if (variableVIndex >= 0 && variableVIndex < variableValues.length) {
            symbolicState.getVariableValues()[variableVIndex] = 6;
            symbolicState.getPolyhedron().addNonStrictConstraint(1, 0, 5);
            symbolicState.getPolyhedron().addNonStrictConstraint(0, 1, -5);
            printStream.println("=== Custom state check: " + query1.getFormula() + " === (expect NOT_OK)");
            strace = null;
            queryResult = (QueryResult)engine.query(uppaalSystem, symbolicState, query1, qf).get();
            printStream.println("Result: " + String.valueOf(queryResult));
            printTrace(printStream, uppaalSystem, strace);
            printStream.println("=== Custom state check: " + query2.getFormula() + " === (expect OK)");
            strace = null;
            queryResult = (QueryResult)engine.query(uppaalSystem, symbolicState, query2, qf).get();
            printStream.println("Result: " + String.valueOf(queryResult));
            printTrace(printStream, uppaalSystem, strace);
            engine.forceDisconnect(10).get();
        } else {
            throw new IOException("Value for variable \"v\" was not found");
        }
    }

    public static String toXMLString(Document document) throws IOException {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        document.saveXML(var1);
        var1.close();
        return var1.toString();
    }

    public static Document fromXML(String var0) throws IOException, XMLStreamException {
        ByteArrayInputStream var1 = new ByteArrayInputStream(var0.getBytes());
        return (new DocumentPrototype()).loadXML(var1, new ArrayList());
    }

    public static String toXTAString(Document document) throws IOException {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        document.saveXTA(var1, (OutputStream)null);
        var1.close();
        return var1.toString();
    }

    public static Document fromXTA(String var0) throws IOException, XMLStreamException, ParseException {
        ByteArrayInputStream var1 = new ByteArrayInputStream(var0.getBytes());
        return (new DocumentPrototype()).loadXTA(var1, (InputStream)null);
    }


    public static void main(String[] var0) {
        PrintStream var1 = System.out;
        try {
            Document var2 = createSampleModel();

            var2.save("ModelDemo.xml");
            var2 = loadModel("demo.polimi.util.ModelDemo.xml");
            //workflows(var1, var2);
            var1.println("Done!");
            System.exit(0);
        } catch (IOException | ModelException | CannotEvaluateException var3) {
            var1.flush();
            ((Exception)var3).printStackTrace(System.err);
            System.exit(1);
        }
    }

    public static enum LKind {
        name,
        init,
        urgent,
        committed,
        invariant,
        exponentialrate,
        comments;

        private LKind() {
        }
    }

    public static enum EKind {
        select,
        guard,
        synchronisation,
        assignment,
        comments;

        private EKind() {
        }
    }
}