package com.uppaal.engine;

import com.uppaal.engine.protocol.viewmodel.RandomTransitionQuery;
import com.uppaal.model.core2.DataSet2D;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.EngineSettings;
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
import java.util.ArrayList;
import java.util.List;

public interface EngineInterface {
   SwingFuture<String> getVersion();

   SwingFuture<EngineOptions> getOptions();

   SwingFuture<List<SymbolicTransition>> getTransitions(UppaalSystem var1, SymbolicState var2);

   SwingFuture<ConcreteSuccessor> getConcreteSuccessor(UppaalSystem var1, ConcreteState var2, SystemEdgeSelect[] var3, double var4, double var6);

   SwingFuture<ConcreteSuccessor> getConcreteSuccessor(UppaalSystem var1, ConcreteState var2, double var3);

   SwingFuture<RandomTransition> getRandomTransition(UppaalSystem var1, ConcreteState var2, RandomTransitionQuery.RandomSemantics var3, double var4);

   SwingFuture<DataSet2D> getConcreteTrajectory(UppaalSystem var1, ConcreteState var2, double var3);

   SwingFuture<UppaalSystem> getSystem(Document var1, ArrayList<Problem> var2);

   SwingFuture<Void> setOptionSettings(EngineSettings var1);

   SwingFuture<SymbolicState> getInitialState(UppaalSystem var1);

   SwingFuture<ConcreteState> getConcreteInitialState(UppaalSystem var1);

   SwingFuture<LscProcess> getLscProcess(Document var1, ArrayList<Problem> var2);

   SwingFuture<QueryResult> query(UppaalSystem var1, Query var2, QueryFeedback var3);

   SwingFuture<String> setStrategy(String var1, boolean var2);

   SwingFuture<List<String>> getStrategies(boolean var1);

   SwingFuture<String> getLicensee();

   SwingFuture<String> getLeaseRequest(String var1, String var2);

   SwingFuture<Void> leaseInstall(String var1);

   boolean getIsLicensed();

   void addConnectionListener(ConnectionListener var1);

   void removeConnectionListener(ConnectionListener var1);
}
