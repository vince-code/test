package com.uppaal.engine;

import com.uppaal.engine.connection.Connection;
import com.uppaal.engine.protocol.LicenseMissingException;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.swing.event.EventListenerList;

public class Engine implements EngineInterface {
   protected EngineStub stub = new EngineStub();
   private String docVersion;
   private UppaalSystem system = null;
   private EngineOptions options = new EngineOptions();
   private boolean isLicensed;
   private ExecutorService executor = Executors.newSingleThreadExecutor();
   private final EventListenerList eventListener = new EventListenerList();

   public void resetConnections(List<Connection> connections) {
      this.stub.resetConnections(connections);
   }

   public void addConnection(Connection conn) {
      this.stub.addConnection(conn);
   }

   public Connection getConnection() {
      return this.stub.getConnection();
   }

   public void setConnectionMode(String mode) {
      this.disconnect();
      this.stub.setConnectionMode(mode);
   }

   public HashMap<String, Connection> getConnections() {
      return this.stub.getConnections();
   }

   private void disconnect() {
      if (this.stub.isConnected()) {
         this.stub.disconnect();
         this.fireDisconnected();
      }

   }

   public SwingFuture<Void> forceDisconnect(int ms_timeout) {
      SwingFuture<Void> future = new SwingFuture();
      ExecutorService oldExecutor = this.executor;
      oldExecutor.shutdownNow();
      this.executor = Executors.newSingleThreadExecutor();
      this.executor.submit(() -> {
         try {
            if (oldExecutor.awaitTermination((long)ms_timeout, TimeUnit.MILLISECONDS)) {
               this.disconnect();
            } else {
               this.kill();
            }
         } catch (InterruptedException var8) {
         } finally {
            future.complete((Object)null);
         }

      });
      return future;
   }

   public SwingFuture<Void> forceDisconnect() {
      return this.forceDisconnect(10);
   }

   public void cancel() {
      if (this.stub.isConnected()) {
         this.stub.cancel();
      }

   }

   private void kill() {
      if (this.stub.isConnected()) {
         this.stub.kill();
         this.fireDisconnected();
      }

   }

   public SwingFuture<String> getVersion() {
      return this.submitLicenseLess(new Job<String>() {
         public String run(Engine engine) throws Exception {
            return Engine.this.stub.getVersion();
         }

         public void error(Throwable e) {
            Engine.this.kill();
         }
      });
   }

   public SwingFuture<EngineOptions> getOptions() {
      return this.submitLicenseLess((engine) -> {
         this.options.reset();
         this.options.parse(this.stub.getOptionsInfo());
         return this.options;
      });
   }

   public SwingFuture<List<SymbolicTransition>> getTransitions(final UppaalSystem system, final SymbolicState state) {
      return this.submit(new Job<List<SymbolicTransition>>() {
         public List<SymbolicTransition> run(Engine engine) throws Exception {
            Engine.this.activate(system);
            return Engine.this.stub.getTransitions(system, state);
         }

         public void error(Throwable e) {
            Engine.this.kill();
         }
      });
   }

   public SwingFuture<ConcreteSuccessor> getConcreteSuccessor(final UppaalSystem system, final ConcreteState state, final SystemEdgeSelect[] edges, final double delay, final double interval) {
      return this.submit(new Job<ConcreteSuccessor>() {
         public ConcreteSuccessor run(Engine engine) throws Exception {
            Engine.this.activate(system);
            return Engine.this.stub.getConcreteSuccessor(system, state, edges, delay, interval);
         }

         public void error(Throwable e) {
            if (!(e instanceof CannotEvaluateException)) {
               Engine.this.kill();
            }

         }
      });
   }

   public SwingFuture<ConcreteSuccessor> getConcreteSuccessor(UppaalSystem system, ConcreteState state, double interval) {
      return this.getConcreteSuccessor(system, state, new SystemEdgeSelect[0], 0.0D, interval);
   }

   public SwingFuture<RandomTransition> getRandomTransition(UppaalSystem system, ConcreteState state, RandomTransitionQuery.RandomSemantics semantics, double horizon) {
      return this.submit((engine) -> {
         this.activate(system);
         return this.stub.getRandomTransition(system, state, semantics, horizon);
      });
   }

   public SwingFuture<DataSet2D> getConcreteTrajectory(final UppaalSystem system, final ConcreteState state, final double horizon) {
      return this.submit(new Job<DataSet2D>() {
         public DataSet2D run(Engine engine) throws Exception {
            Engine.this.activate(system);
            return Engine.this.stub.getConcreteTrajectory(system, state, horizon);
         }

         public void error(Throwable e) {
            Engine.this.kill();
         }
      });
   }

   public SwingFuture<UppaalSystem> getSystem(final Document document, final ArrayList<Problem> problems) {
      return this.submitLicenseLess(new Job<UppaalSystem>() {
         public UppaalSystem run(Engine engine) throws Exception {
            UppaalSystem s = Engine.this.stub.upload(document, problems);
            if (s != null) {
               Engine.this.docVersion = document.getVersion();
               Engine.this.system = s;
            }

            return s;
         }

         public void error(Throwable e) {
            Engine.this.kill();
         }
      });
   }

   public SwingFuture<Void> setOptionSettings(final EngineSettings settings) {
      return this.submitLicenseLess(new Job<Void>() {
         public Void run(Engine engine) throws Exception {
            Engine.this.stub.setOptions(Engine.this.options.getText(settings));
            return null;
         }

         public void error(Throwable e) {
            Engine.this.cancel();
         }
      });
   }

   public synchronized UppaalSystem getSystem() {
      return this.system;
   }

   public SwingFuture<SymbolicState> getInitialState(final UppaalSystem system) {
      return this.submit(new Job<SymbolicState>() {
         public SymbolicState run(Engine engine) throws Exception {
            Engine.this.activate(system);
            return Engine.this.stub.getSymbolicInitial(system);
         }

         public void error(Throwable e) {
            Engine.this.kill();
         }
      });
   }

   public SwingFuture<ConcreteState> getConcreteInitialState(UppaalSystem system) {
      return this.submit((engine) -> {
         this.activate(system);
         return this.stub.getConcreteInitial(system);
      });
   }

   public SwingFuture<LscProcess> getLscProcess(Document document, ArrayList<Problem> problems) {
      return this.submit((engine) -> {
         LscProcess p = this.stub.uploadLsc(document, problems);
         if (p != null) {
            this.docVersion = document.getVersion();
         }

         return p;
      });
   }

   public SwingFuture<QueryResult> query(final UppaalSystem system, final Query query, final QueryFeedback f) {
      return this.submit(new Job<QueryResult>() {
         public QueryResult run(Engine engine) throws Exception {
            Engine.this.activate(system);
            return Engine.this.stub.query(system, query, f);
         }

         public void error(Throwable e) {
            Engine.this.cancel();
         }
      });
   }

   public SwingFuture<QueryResult> query(final UppaalSystem system, final SymbolicState state, final Query query, final QueryFeedback f) throws EngineException, IOException {
      new SwingFuture();
      return this.submit(new Job() {
         public QueryResult run(Engine engine) throws Exception {
            Engine.this.activate(system);
            return Engine.this.stub.query(system, state, query, f);
         }

         public void error(Throwable e) {
            Engine.this.cancel();
         }
      });
   }

   public SwingFuture<String> getStrategy(final String strategy) {
      return this.submit(new Job<String>() {
         public String run(Engine engine) throws Exception {
            return Engine.this.stub.getStrategy(strategy);
         }

         public void error(Throwable e) {
            Engine.this.cancel();
         }
      });
   }

   private void activate(UppaalSystem value) throws EngineException, IOException {
      if (this.system != value || !value.getDocument().getVersion().equals(this.docVersion)) {
         if (this.stub.upload(value.getDocument()) == null) {
            throw new EngineException("Could not transfer model to engine due to syntax errors.");
         }

         this.system = value;
         this.docVersion = value.getDocument().getVersion();
      }

   }

   private void ensureLicense() throws EngineException, IOException {
      if (!this.checkLicenseSync()) {
         throw new LicenseMissingException();
      }
   }

   private void connectSync() throws EngineException, IOException {
      if (!this.stub.isConnected()) {
         this.fireBeforeConnected();

         try {
            this.stub.connect();
            this.options = new EngineOptions();
            this.system = null;
            this.isLicensed = false;
         } catch (IOException | EngineException var2) {
            this.fireDisconnected();
            throw var2;
         }

         this.fireAfterConnected();
      }

   }

   private boolean checkLicenseSync() throws EngineException, IOException {
      if (!this.isLicensed) {
         String license = this.stub.getLicensee();
         this.isLicensed = license != null && !license.isBlank();
         if (this.isLicensed) {
            this.fireLicenseConfirmed();
         }
      }

      return this.isLicensed;
   }

   public SwingFuture<Void> connect() {
      return this.submitLicenseLess((engine) -> {
         return null;
      });
   }

   public SwingFuture<Void> connectLicense() {
      return this.submit((engine) -> {
         return null;
      });
   }

   public void tryConnect() {
      this.submit((engine) -> {
         return null;
      });
   }

   public void addConnectionListener(ConnectionListener listener) {
      this.eventListener.add(ConnectionListener.class, listener);
   }

   public void removeConnectionListener(ConnectionListener listener) {
      this.eventListener.remove(ConnectionListener.class, listener);
   }

   public void fireConnectionEvent(Consumer<ConnectionListener> event) {
      Object[] listeners = this.eventListener.getListenerList();

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ConnectionListener.class) {
            event.accept((ConnectionListener)listeners[i + 1]);
         }
      }

   }

   private void fireBeforeConnected() {
      this.fireConnectionEvent(ConnectionListener::beforeConnected);
   }

   private void fireLicenseConfirmed() {
      this.fireConnectionEvent(ConnectionListener::licenseConfirmed);
   }

   private void fireAfterConnected() {
      this.fireConnectionEvent(ConnectionListener::afterConnected);
   }

   private void fireDisconnected() {
      this.fireConnectionEvent(ConnectionListener::disconnected);
   }

   private void fireNoLicenseException() {
      this.fireConnectionEvent(ConnectionListener::noLicenseException);
   }

   private <T> SwingFuture<T> submit(Job<T> job) {
      return this.submit(job, true);
   }

   private <T> SwingFuture<T> submitLicenseLess(Job<T> job) {
      return this.submit(job, false);
   }

   private <T> SwingFuture<T> submit(Job<T> job, boolean checkLicense) {
      SwingFuture<T> future = new SwingFuture();
      Exception baseEx = new EngineInvocationException("Concurrent execution encountered an exception");
      this.executor.execute(() -> {
         try {
            this.connectSync();
            if (checkLicense) {
               this.ensureLicense();
            }

            T value = job.run(this);
            if (!Thread.interrupted()) {
               future.complete(value);
            }
         } catch (LicenseMissingException var6) {
            setCauseRecursive(var6, baseEx);
            this.fireNoLicenseException();
            job.error(var6);
            future.completeExceptionally(var6);
         } catch (InterruptedException var7) {
         } catch (Exception var8) {
            if (Thread.interrupted()) {
               return;
            }

            setCauseRecursive(var8, baseEx);
            job.error(var8);
            future.completeExceptionally(var8);
         }

      });
      return future;
   }

   private static void setCauseRecursive(Throwable base, Throwable cause) {
      if (base.getCause() == null) {
         base.initCause(cause);
      } else {
         setCauseRecursive(base.getCause(), cause);
      }

   }

   public SwingFuture<String> setStrategy(final String name, final boolean zone_stable) {
      return this.submitLicenseLess(new Job<String>() {
         public String run(Engine engine) throws Exception {
            Engine.this.activate(Engine.this.system);
            Engine.this.stub.setStrategy(name, zone_stable);
            return "ok";
         }

         public void error(Throwable e) {
            Engine.this.kill();
         }
      });
   }

   public SwingFuture<List<String>> getStrategies(final boolean zone_stable) {
      return this.submit(new Job<List<String>>() {
         public List<String> run(Engine engine) throws Exception {
            Engine.this.activate(Engine.this.system);
            return Engine.this.stub.getStrategies(zone_stable);
         }

         public void error(Throwable e) {
            Engine.this.kill();
         }
      });
   }

   public SwingFuture<String> getLicensee() {
      return this.submitLicenseLess((engine) -> {
         try {
            return this.stub.getLicensee();
         } catch (Exception var3) {
            return "";
         }
      });
   }

   public SwingFuture<String> getLeaseRequest(String key, String duration) {
      return this.submitLicenseLess((engine) -> {
         String lease = this.stub.getLeaseRequest(key, duration);
         this.isLicensed = true;
         this.fireLicenseConfirmed();
         return lease;
      });
   }

   public SwingFuture<Void> leaseInstall(String lease) {
      return this.submitLicenseLess((engine) -> {
         this.stub.leaseInstall(lease);
         return null;
      });
   }

   public boolean getIsLicensed() {
      return this.stub.isConnected() && this.isLicensed;
   }
}
