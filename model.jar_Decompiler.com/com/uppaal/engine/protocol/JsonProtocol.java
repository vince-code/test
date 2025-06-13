package com.uppaal.engine.protocol;

import com.google.gson.reflect.TypeToken;
import com.uppaal.engine.CannotEvaluateException;
import com.uppaal.engine.EngineCrashException;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.ModelProblemException;
import com.uppaal.engine.Protocol;
import com.uppaal.engine.ProtocolException;
import com.uppaal.engine.QueryFeedback;
import com.uppaal.engine.connection.InitialConnection;
import com.uppaal.engine.protocol.viewmodel.ConcreteHorizonQuery;
import com.uppaal.engine.protocol.viewmodel.ConcreteStateViewModel;
import com.uppaal.engine.protocol.viewmodel.ConcreteSuccessorQuery;
import com.uppaal.engine.protocol.viewmodel.ConcreteSuccessorViewModel;
import com.uppaal.engine.protocol.viewmodel.ConcreteTraceViewModel;
import com.uppaal.engine.protocol.viewmodel.CustomStateMCCommand;
import com.uppaal.engine.protocol.viewmodel.DataSetViewModel;
import com.uppaal.engine.protocol.viewmodel.ErrorMessage;
import com.uppaal.engine.protocol.viewmodel.LeaseRequestCommand;
import com.uppaal.engine.protocol.viewmodel.ModelCheckStatus;
import com.uppaal.engine.protocol.viewmodel.PlotViewModel;
import com.uppaal.engine.protocol.viewmodel.PointFieldViewModel;
import com.uppaal.engine.protocol.viewmodel.ProcessViewModel;
import com.uppaal.engine.protocol.viewmodel.ProgressInfoViewModel;
import com.uppaal.engine.protocol.viewmodel.QueryResultViewModel;
import com.uppaal.engine.protocol.viewmodel.RandomTransitionQuery;
import com.uppaal.engine.protocol.viewmodel.RandomTransitionViewModel;
import com.uppaal.engine.protocol.viewmodel.SuccessorListViewModel;
import com.uppaal.engine.protocol.viewmodel.SuccessorViewModel;
import com.uppaal.engine.protocol.viewmodel.SymbolicStateViewModel;
import com.uppaal.engine.protocol.viewmodel.SystemInfoViewModel;
import com.uppaal.engine.protocol.viewmodel.SystemUploadHandshake;
import com.uppaal.model.core2.DataSet2D;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.QueryResult;
import com.uppaal.model.core2.QueryValue;
import com.uppaal.model.io2.Problem;
import com.uppaal.model.io2.XMLWriter;
import com.uppaal.model.lscsystem.LscProcess;
import com.uppaal.model.system.GanttChart;
import com.uppaal.model.system.GanttRow;
import com.uppaal.model.system.IdentifierTranslator;
import com.uppaal.model.system.SystemEdgeSelect;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.concrete.ConcreteState;
import com.uppaal.model.system.concrete.ConcreteSuccessor;
import com.uppaal.model.system.concrete.ConcreteTrace;
import com.uppaal.model.system.concrete.RandomTransition;
import com.uppaal.model.system.symbolic.SymbolicState;
import com.uppaal.model.system.symbolic.SymbolicTrace;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonProtocol implements Protocol {
   private final JsonMessageWriter writer;
   private final JsonMessageParser parser;
   private boolean canceling;
   public static final String closeCommand = "close";
   private final Pattern intervalPattern;

   private JsonProtocol(InputStreamReader in, BufferedWriter out) {
      this(new JsonMessageParser(in, EngineCrashException.class), new JsonMessageWriter(out));
   }

   public JsonProtocol(JsonMessageParser in, JsonMessageWriter out) {
      this.canceling = false;
      this.intervalPattern = Pattern.compile("\\[\\d+\\.?\\d*,\\d+\\.?\\d*\\]");
      this.parser = in;
      this.writer = out;
   }

   public static JsonProtocol handshake(InitialConnection connection) throws IOException, EngineException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.in, StandardCharsets.UTF_8));
      String line = reader.readLine();
      if (line == null) {
         throw new EngineException("Server closed connection.");
      } else if ("json".equals(line)) {
         return new JsonProtocol(new InputStreamReader(connection.in, StandardCharsets.UTF_8), connection.out);
      } else {
         throw new ProtocolException("Unknown protocol version: " + line);
      }
   }

   public void close() throws IOException {
      this.writer.writeEmptyCommand("close");
   }

   public String getVersion() throws IOException, EngineException {
      this.writer.writeEmptyCommand("getVersion");
      return (String)this.parser.parseResponse(String.class);
   }

   public String getOptionsInfo() throws EngineException, IOException {
      this.writer.writeEmptyCommand("getOptionsInfo");
      return (String)this.parser.parseResponse(String.class);
   }

   public void setOptions(String options) throws EngineException, IOException {
      this.writer.writeCommand("setOptions", options);
      this.parser.parseValidatedResponse();
   }

   public SymbolicState getSymbolicInitial(UppaalSystem system) throws EngineException, IOException, CannotEvaluateException {
      this.writer.writeEmptyCommand("getInitialStateSymbolic");
      SymbolicStateViewModel state = (SymbolicStateViewModel)this.parser.parseResponse(SymbolicStateViewModel.class);
      return (new TAModelInterpreter(system)).interpretSymbolicState(state);
   }

   public ConcreteState getConcreteInitial(UppaalSystem system) throws EngineException, IOException, CannotEvaluateException {
      this.writer.writeEmptyCommand("concreteGetInitial");
      ConcreteStateViewModel state = (ConcreteStateViewModel)this.parser.parseResponse(ConcreteStateViewModel.class);
      return (new TAModelInterpreter(system)).interpretConcreteState(state);
   }

   public ArrayList<SymbolicTransition> getTransitions(UppaalSystem system, SymbolicState state) throws EngineException, IOException, CannotEvaluateException {
      this.writer.writeCommand("getSuccessors", (Object)(new SymbolicStateViewModel(state)));
      SuccessorListViewModel successorListViewModel = (SuccessorListViewModel)this.parser.parseResponse(SuccessorListViewModel.class);
      List<SuccessorViewModel> successors = successorListViewModel.getSuccessors();
      TAModelInterpreter interpreter = new TAModelInterpreter(system);
      return (ArrayList)successors.stream().map((succ) -> {
         return new SymbolicTransition(interpreter.interpretSymbolicState(succ.getSource()), interpreter.interpretEdgeSelectList(succ.getTransition()), interpreter.interpretSymbolicState(succ.getDest()));
      }).collect(Collectors.toCollection(ArrayList::new));
   }

   private void uploadXMLSystem(Document document) throws EngineException, IOException {
      ByteArrayOutputStream sstream = new ByteArrayOutputStream();

      try {
         document.accept(new XMLWriter(sstream));
      } catch (Exception var4) {
         var4.printStackTrace();
         throw new EngineException(var4.toString());
      }

      this.writer.writeCommand("newXMLSystem3", sstream.toString());
   }

   public UppaalSystem upload(Document document, ArrayList<Problem> problems) throws EngineException, IOException {
      this.uploadXMLSystem(document);

      try {
         SystemUploadHandshake handshake = (SystemUploadHandshake)this.parser.parseWithManyErrors(SystemUploadHandshake.class);
         handshake.getWarnings().forEach((warning) -> {
            problems.add(new Problem("warning", warning));
         });
         return this.produceSystem(handshake, document);
      } catch (ModelProblemException var4) {
         var4.getErrors().forEach((error) -> {
            problems.add(new Problem("error", error));
         });
         var4.getWarnings().forEach((warning) -> {
            problems.add(new Problem("warning", warning));
         });
         return null;
      }
   }

   private UppaalSystem produceSystem(SystemUploadHandshake response, Document document) {
      UppaalSystem system = new UppaalSystem(document);
      system.setVariables((ArrayList)response.getVariableNames());
      system.setSupportedMethods(response.getSupportedMethods());
      system.setClocks((ArrayList)response.getClockNames());
      Iterator var4 = response.getProcesses().iterator();

      while(var4.hasNext()) {
         ProcessViewModel process = (ProcessViewModel)var4.next();
         IdentifierTranslator translator = new IdentifierTranslator(process.getParameterReferences());
         system.addProcess(process.getProcName(), process.getTemplateName(), translator);
      }

      GanttChart gc = system.getGanttChart();
      if (gc == null) {
         gc = new GanttChart();
      } else {
         gc.clearChart();
      }

      for(int i = 0; i < response.getGanttRows().size(); ++i) {
         gc.addRow((GanttRow)response.getGanttRows().get(i));
      }

      system.setGanttChart(gc);
      return system;
   }

   public UppaalSystem upload(Document document) throws EngineException, IOException {
      this.uploadXMLSystem(document);
      SystemUploadHandshake handshake = (SystemUploadHandshake)this.parser.parseResponse(SystemUploadHandshake.class);
      return this.produceSystem(handshake, document);
   }

   public QueryResult query(UppaalSystem system, Query query, QueryFeedback feedback) throws EngineException, IOException {
      SystemInfoViewModel info = this.getSystemInfo();
      if (info != null) {
         feedback.setProgressAvail(true);
         feedback.setSystemInfo(info.virtTotal, info.physTotal, info.swapTotal);
      }

      this.writer.writeCommand("modelCheck", query.getShortFormula());
      this.writer.flush();
      this.canceling = false;
      return this.processQueryResult(system, feedback);
   }

   private QueryResult processQueryResult(UppaalSystem system, QueryFeedback feedback) throws IOException, ProtocolException {
      this.canceling = false;

      while(true) {
         Response response = this.parser.parseRawResponse();
         String var4 = response.type;
         byte var5 = -1;
         switch(var4.hashCode()) {
         case 3548:
            if (var4.equals("ok")) {
               var5 = 1;
            }
            break;
         case 3449690:
            if (var4.equals("prog")) {
               var5 = 0;
            }
            break;
         case 96784904:
            if (var4.equals("error")) {
               var5 = 2;
            }
         }

         switch(var5) {
         case 0:
            feedback.setProgress((ProgressInfoViewModel)response.expect(ProgressInfoViewModel.class));
            this.writer.writeEmptyCommand(this.canceling ? "interrupt" : "continue");
            break;
         case 1:
            return this.processSymQueryResult((QueryResultViewModel)response.expect(QueryResultViewModel.class), system, feedback);
         case 2:
            return new QueryResult((ErrorMessage)response.expect(ErrorMessage.class));
         default:
            this.writer.flush();
            throw new RuntimeException();
         }
      }
   }

   public SystemInfoViewModel getSystemInfo() throws IOException, ProtocolException {
      this.writer.writeEmptyCommand("getSystemInfo");
      this.writer.flush();
      return (SystemInfoViewModel)this.parser.parseResponse(SystemInfoViewModel.class);
   }

   public QueryResult query(UppaalSystem system, SymbolicState state, Query query, QueryFeedback feedback) throws EngineException, IOException {
      SystemInfoViewModel info = this.getSystemInfo();
      if (info != null) {
         feedback.setProgressAvail(true);
         feedback.setSystemInfo(info.virtTotal, info.physTotal, info.swapTotal);
      }

      this.writer.writeCommand("modelCheckState", (Object)(new CustomStateMCCommand(state, query.getShortFormula())));
      this.writer.flush();
      this.canceling = false;
      return this.processQueryResult(system, feedback);
   }

   private QueryResult processSymQueryResult(QueryResultViewModel model, UppaalSystem system, QueryFeedback feedback) {
      TAModelInterpreter interpreter = new TAModelInterpreter(system);
      QueryResult result = new QueryResult(model);
      if (model.getStatus() == ModelCheckStatus.ERROR) {
         result.set(this.exceptionFromErrorMessage(model.getError()));
      }

      if (model.hasSymbolicTrace()) {
         SymbolicTrace trace = interpreter.interpretSymbolicTrace((List)model.getSymbolicTrace().get());
         trace.cycle = model.getCycleLength();
         feedback.setLength(trace.size());
         feedback.setTrace(model.getStatusRepresentation(), model.getMessage(), trace, result);
      } else if (model.hasConcreteTrace()) {
         ConcreteTrace trace = interpreter.interpretConcreteTrace((ConcreteTraceViewModel)model.getConcreteTrace().get());
         feedback.setLength(trace.size());
         feedback.setTrace(model.getStatusRepresentation(), model.getMessage(), trace, result);
      } else if (model.getError() == null) {
         feedback.setFeedback(model.getMessage());
      }

      this.interpretValue(model.getResult(), result);
      result.setMessage(model.getResult());
      this.interpretPlots(model.getPlots(), result);
      return result;
   }

   private void interpretValue(String resultStr, QueryResult result) {
      if (resultStr.isEmpty()) {
         result.getValue().setKind(QueryValue.Kind.Quality);
      } else if (!resultStr.startsWith("≤") && !resultStr.startsWith("≥") && !this.intervalPattern.matcher(resultStr).find()) {
         result.getValue().setKind(QueryValue.Kind.Quantity);
         result.getValue().setValue(resultStr);
      } else {
         result.getValue().setKind(QueryValue.Kind.Interval);
         result.getValue().setValue(resultStr);
      }

   }

   private CannotEvaluateException exceptionFromErrorMessage(ErrorMessage message) {
      return new CannotEvaluateException(message.begln, message.endln, message.ctx, message.msg, message.path, message.begcol, message.endcol);
   }

   private void interpretPlots(List<PlotViewModel> plots, QueryResult result) {
      Iterator var3 = plots.iterator();

      while(var3.hasNext()) {
         PlotViewModel plot = (PlotViewModel)var3.next();
         DataSet2D dataset = new DataSet2D(plot.getTitle(), plot.getXlabel(), plot.getYlabel());
         result.addPlot(dataset);
         Iterator var6 = plot.getDataSeries().iterator();

         while(var6.hasNext()) {
            DataSetViewModel protocolData = (DataSetViewModel)var6.next();
            dataset.addData(protocolData.getTitle(), protocolData.getType(), Color.decode(protocolData.getColor()));
            Iterator var8 = protocolData.getPoints().iterator();

            while(var8.hasNext()) {
               PointFieldViewModel point = (PointFieldViewModel)var8.next();
               dataset.addSample(point.getX(), point.getY());
            }
         }

         String[] var10 = plot.getComments().split("\\n");
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            String comment = var10[var12];
            dataset.addComment(comment);
         }
      }

   }

   public ConcreteSuccessor getConcreteSuccessor(UppaalSystem system, ConcreteState state, SystemEdgeSelect[] edges, double delay, double interval) throws EngineException, IOException, CannotEvaluateException {
      this.writer.writeCommand("concreteGetSuccessor", (Object)(new ConcreteSuccessorQuery(new ConcreteStateViewModel(state), delay, edges, interval)));
      ConcreteSuccessorViewModel successor = (ConcreteSuccessorViewModel)this.parser.parseResponse(ConcreteSuccessorViewModel.class);

      try {
         return (new TAModelInterpreter(system)).interpretConcreteSuccessor(successor);
      } catch (CloneNotSupportedException var10) {
         throw new RuntimeException("GanttChart not clonable");
      }
   }

   public RandomTransition getRandomTransition(UppaalSystem system, ConcreteState state, RandomTransitionQuery.RandomSemantics randomSemantics, double horizon) throws IOException, CannotEvaluateException, ProtocolException, ParseException {
      this.writer.writeCommand("concreteGetRandom", (Object)(new RandomTransitionQuery(new ConcreteStateViewModel(state), randomSemantics, horizon)));
      RandomTransitionViewModel transition = (RandomTransitionViewModel)this.parser.parseResponse(RandomTransitionViewModel.class);
      return (new TAModelInterpreter(system)).interpretRandomTransition(transition);
   }

   public DataSet2D getConcreteTrajectory(UppaalSystem system, ConcreteState state, double horizon) throws ProtocolException, IOException, ParseException {
      this.writer.writeCommand("concreteGetHorizon", (Object)(new ConcreteHorizonQuery(new ConcreteStateViewModel(state), horizon)));
      ArrayList<DataSetViewModel> datasets = (ArrayList)this.parser.parseGeneric((new TypeToken<ArrayList<DataSetViewModel>>() {
      }).getType());
      DataSet2D test = new DataSet2D("Horizons", "#time", "Value");
      Iterator var7 = datasets.iterator();

      while(var7.hasNext()) {
         DataSetViewModel dataset = (DataSetViewModel)var7.next();
         test.addData(dataset.getTitle(), dataset.getType());
         Iterator var9 = dataset.getPoints().iterator();

         while(var9.hasNext()) {
            PointFieldViewModel point = (PointFieldViewModel)var9.next();
            test.addSample(point.getX(), point.getY());
         }
      }

      return test;
   }

   public ArrayList<String> getStrategies(boolean zone_stable) {
      try {
         if (zone_stable) {
            this.writer.writeEmptyCommand("getZoneStableStrategies");
         } else {
            this.writer.writeEmptyCommand("getStrategies");
         }

         return (ArrayList)this.parser.parseGeneric((new TypeToken<ArrayList<String>>() {
         }).getType());
      } catch (ProtocolException | IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void setStrategy(String strategy, boolean zone_stable) {
      try {
         if (zone_stable) {
            this.writer.writeCommand("setZoneStrategy", strategy);
         } else {
            this.writer.writeCommand("setStrategy", strategy);
         }

         try {
            this.parser.parseValidatedResponse();
         } catch (ProtocolException var4) {
            throw new RuntimeException("No such strategy", var4);
         }
      } catch (IOException var5) {
         throw new RuntimeException("IOException in setStrategy", var5);
      }
   }

   public String getLicensee() throws IOException {
      this.writer.writeEmptyCommand("getLicensee");

      try {
         return (String)this.parser.parseResponse(String.class);
      } catch (Exception var2) {
         return "";
      }
   }

   public String getLeaseRequest(String license_key, String duration) throws IOException, ProtocolException {
      this.writer.writeCommand("leaseRequest", (Object)(new LeaseRequestCommand(license_key, duration)));
      return (String)this.parser.parseResponse(String.class);
   }

   public String installLease(String lease) throws IOException, ProtocolException, ParseException {
      this.writer.writeCommand("leaseInstall", lease);
      return (String)this.parser.parseResponse(String.class);
   }

   public String getStrategy(String strategy) throws IOException, ProtocolException {
      this.writer.writeCommand("getStrategyJson", strategy);
      return (String)this.parser.parseResponse(String.class);
   }

   public void cancel() {
      this.canceling = true;
   }

   public LscProcess uploadLsc(Document document, ArrayList<Problem> problems) {
      return null;
   }
}
