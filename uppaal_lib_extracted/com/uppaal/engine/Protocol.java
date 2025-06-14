package com.uppaal.engine;

import com.uppaal.engine.protocol.ParseException;
import com.uppaal.engine.protocol.viewmodel.RandomTransitionQuery;
import com.uppaal.model.core2.DataSet2D;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.QueryResult;
import com.uppaal.model.io2.Problem;
import com.uppaal.model.lscsystem.LscProcess;
import com.uppaal.model.system.SystemEdgeSelect;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.concrete.ConcreteState;
import com.uppaal.model.system.concrete.ConcreteSuccessor;
import com.uppaal.model.system.concrete.RandomTransition;
import com.uppaal.model.system.symbolic.SymbolicState;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import java.io.IOException;
import java.util.ArrayList;

public interface Protocol {
   void close() throws IOException;

   String getVersion() throws IOException, EngineException;

   String getOptionsInfo() throws EngineException, IOException;

   void setOptions(String var1) throws EngineException, IOException;

   SymbolicState getSymbolicInitial(UppaalSystem var1) throws EngineException, IOException, CannotEvaluateException;

   ConcreteState getConcreteInitial(UppaalSystem var1) throws EngineException, IOException, CannotEvaluateException;

   ConcreteSuccessor getConcreteSuccessor(UppaalSystem var1, ConcreteState var2, SystemEdgeSelect[] var3, double var4, double var6) throws EngineException, IOException, CannotEvaluateException;

   RandomTransition getRandomTransition(UppaalSystem var1, ConcreteState var2, RandomTransitionQuery.RandomSemantics var3, double var4) throws Exception;

   DataSet2D getConcreteTrajectory(UppaalSystem var1, ConcreteState var2, double var3) throws ProtocolException, IOException, ParseException;

   ArrayList<SymbolicTransition> getTransitions(UppaalSystem var1, SymbolicState var2) throws EngineException, IOException, CannotEvaluateException;

   UppaalSystem upload(Document var1, ArrayList<Problem> var2) throws EngineException, IOException;

   LscProcess uploadLsc(Document var1, ArrayList<Problem> var2) throws EngineException, IOException;

   UppaalSystem upload(Document var1) throws EngineException, IOException;

   QueryResult query(UppaalSystem var1, Query var2, QueryFeedback var3) throws EngineException, IOException;

   QueryResult query(UppaalSystem var1, SymbolicState var2, Query var3, QueryFeedback var4) throws EngineException, IOException, CannotEvaluateException;

   void cancel();

   void setStrategy(String var1, boolean var2);

   ArrayList<String> getStrategies(boolean var1);

   String getLicensee() throws IOException, ProtocolException, ParseException;

   String getLeaseRequest(String var1, String var2) throws IOException, ProtocolException, ParseException;

   String installLease(String var1) throws IOException, ProtocolException, ParseException;

   String getStrategy(String var1) throws IOException, ProtocolException;
}
