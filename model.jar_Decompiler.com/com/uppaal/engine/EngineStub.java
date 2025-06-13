package com.uppaal.engine;

import com.uppaal.engine.connection.Connection;
import com.uppaal.engine.connection.RemoteConnection;
import com.uppaal.engine.connection.ServerUnavailableException;
import com.uppaal.engine.protocol.JsonProtocol;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class EngineStub {
   private Protocol protocol;
   private final LinkedHashMap<String, Connection> connections = new LinkedHashMap();
   Connection connection;
   private String licensee;

   public String getErrorStream() {
      return this.connection.getErrorStream();
   }

   public int getServerPort() {
      return this.connection instanceof RemoteConnection ? ((RemoteConnection)this.connection).getServerPort() : -1;
   }

   public synchronized void setConnectionMode(String mode) {
      this.connection = (Connection)this.connections.get(mode);
   }

   public Connection getConnection() {
      return this.connection;
   }

   public HashMap<String, Connection> getConnections() {
      return this.connections;
   }

   public void resetConnections(List<Connection> connections) {
      this.connections.clear();
      String oldConnectionName = this.connection != null ? this.connection.getName() : "";
      this.connection = null;
      Iterator var3 = connections.iterator();

      while(var3.hasNext()) {
         Connection conn = (Connection)var3.next();
         this.addConnection(conn);
         if (conn.getName().equals(oldConnectionName)) {
            this.connection = conn;
         }
      }

      if (this.connection == null) {
         this.connection = (Connection)connections.get(0);
      }

   }

   public void addConnection(Connection connection) {
      this.connections.put(connection.getName(), connection);
   }

   public boolean isConnected() {
      return this.connection != null && this.connection.isConnected();
   }

   public void saveTrace() {
      if (this.connection.hasTrace()) {
         CrashSaver.SaveCrash("enginecrash", this.connection.dumpTrace());
      }

   }

   public synchronized void connect() throws EngineException, IOException {
      if (this.connection == null && !this.connections.isEmpty()) {
         this.connection = (Connection)this.connections.get("Bundled");
         if (this.connection == null) {
            this.connection = (Connection)((Entry)this.connections.entrySet().iterator().next()).getValue();
         }
      }

      if (this.connection == null) {
         throw new ServerUnavailableException("No connection selected");
      } else {
         this.protocol = JsonProtocol.handshake(this.connection.connectWithMonitor());
      }
   }

   public void disconnect() {
      if (this.isConnected()) {
         final Object lock = new Object();
         synchronized(lock) {
            Thread t = new Thread("ProtocolWaiter") {
               public void run() {
                  try {
                     if (EngineStub.this.protocol != null) {
                        EngineStub.this.protocol.close();
                     }
                  } catch (IOException var4) {
                  }

                  synchronized(lock) {
                     lock.notify();
                  }
               }
            };
            t.start();

            try {
               lock.wait(2000L);
               this.kill();
            } catch (InterruptedException var6) {
               var6.printStackTrace();
               if (this.connection != null) {
                  this.connection.disconnect();
               }
            }
         }

         this.kill();
      }
   }

   public void kill() {
      this.licensee = null;
      Iterator var1 = this.connections.values().iterator();

      while(var1.hasNext()) {
         Connection conn = (Connection)var1.next();
         conn.kill();
      }

   }

   public synchronized String getVersion() throws IOException, EngineException {
      try {
         return this.protocol.getVersion();
      } catch (IOException var2) {
         this.checkForEngineException(var2);
         throw var2;
      }
   }

   private void checkForEngineException(IOException e) throws EngineException {
      String err = this.getErrorStream();
      if (err != null && err.length() > 0) {
         this.saveTrace();
         throw new EngineException(err, e);
      }
   }

   public synchronized String getOptionsInfo() throws EngineException, IOException {
      try {
         return this.protocol.getOptionsInfo();
      } catch (IOException var2) {
         this.checkForEngineException(var2);
         throw var2;
      }
   }

   public synchronized void setOptions(String options) throws EngineException, IOException {
      try {
         this.protocol.setOptions(options);
      } catch (IOException var3) {
         this.checkForEngineException(var3);
         throw var3;
      }
   }

   public synchronized SymbolicState getSymbolicInitial(UppaalSystem system) throws EngineException, IOException, CannotEvaluateException {
      try {
         return this.protocol.getSymbolicInitial(system);
      } catch (IOException var3) {
         this.checkForEngineException(var3);
         throw var3;
      }
   }

   public synchronized ConcreteState getConcreteInitial(UppaalSystem system) throws EngineException, IOException, CannotEvaluateException {
      try {
         return this.protocol.getConcreteInitial(system);
      } catch (IOException var3) {
         this.checkForEngineException(var3);
         throw var3;
      }
   }

   public synchronized ConcreteSuccessor getConcreteSuccessor(UppaalSystem system, ConcreteState state, SystemEdgeSelect[] edges, double delay, double interval) throws EngineException, IOException, CannotEvaluateException {
      try {
         return this.protocol.getConcreteSuccessor(system, state, edges, delay, interval);
      } catch (IOException var9) {
         this.checkForEngineException(var9);
         throw var9;
      }
   }

   public synchronized RandomTransition getRandomTransition(UppaalSystem system, ConcreteState state, RandomTransitionQuery.RandomSemantics randomSemantics, double horizon) throws Exception {
      return this.protocol.getRandomTransition(system, state, randomSemantics, horizon);
   }

   public synchronized DataSet2D getConcreteTrajectory(UppaalSystem system, ConcreteState state, double horizon) throws EngineException, IOException {
      return this.protocol.getConcreteTrajectory(system, state, horizon);
   }

   public synchronized ArrayList<SymbolicTransition> getTransitions(UppaalSystem system, SymbolicState state) throws EngineException, IOException, CannotEvaluateException {
      try {
         return this.protocol.getTransitions(system, state);
      } catch (IOException var4) {
         this.checkForEngineException(var4);
         throw var4;
      }
   }

   public synchronized UppaalSystem upload(Document document, ArrayList<Problem> problems) throws EngineException, IOException {
      if (this.protocol == null) {
         this.saveTrace();
         throw new IOException("Could not connect to server");
      } else {
         try {
            return this.protocol.upload(document, problems);
         } catch (IOException var4) {
            this.checkForEngineException(var4);
            throw var4;
         }
      }
   }

   public synchronized LscProcess uploadLsc(Document document, ArrayList<Problem> problems) throws EngineException, IOException {
      try {
         return this.protocol.uploadLsc(document, problems);
      } catch (IOException var4) {
         this.checkForEngineException(var4);
         throw var4;
      }
   }

   public synchronized UppaalSystem upload(Document document) throws EngineException, IOException {
      try {
         return this.protocol.upload(document);
      } catch (IOException var3) {
         this.checkForEngineException(var3);
         throw var3;
      }
   }

   public synchronized QueryResult query(UppaalSystem system, Query query, QueryFeedback f) throws EngineException, IOException {
      try {
         return this.protocol.query(system, query, f);
      } catch (EngineException var6) {
         this.saveTrace();
         throw var6;
      } catch (IOException var7) {
         String err = this.getErrorStream();
         if (err != null && err.length() > 0) {
            this.saveTrace();
            throw new EngineException(err, var7);
         } else {
            this.kill();
            throw var7;
         }
      }
   }

   public synchronized QueryResult query(UppaalSystem system, SymbolicState state, Query query, QueryFeedback f) throws EngineException, IOException, CannotEvaluateException {
      return this.protocol.query(system, state, query, f);
   }

   public synchronized String getLicensee() throws IOException, EngineException {
      try {
         if (this.licensee == null || this.licensee.isEmpty()) {
            this.licensee = this.protocol.getLicensee();
         }

         return this.licensee;
      } catch (IOException var2) {
         this.checkForEngineException(var2);
         throw var2;
      }
   }

   public synchronized String getLeaseRequest(String license_key, String duration) throws IOException, EngineException {
      try {
         this.licensee = null;
         return this.protocol.getLeaseRequest(license_key, duration);
      } catch (IOException var4) {
         this.checkForEngineException(var4);
         throw var4;
      }
   }

   public synchronized void leaseInstall(String lease) throws IOException, EngineException {
      try {
         this.licensee = null;
         this.protocol.installLease(lease);
      } catch (IOException var3) {
         this.checkForEngineException(var3);
         throw var3;
      }
   }

   void cancel() {
      this.protocol.cancel();
   }

   ArrayList<String> getStrategies(boolean zone_stable) {
      return this.protocol.getStrategies(zone_stable);
   }

   void setStrategy(String name, boolean zone_stable) {
      this.protocol.setStrategy(name, zone_stable);
   }

   public String getStrategy(String strategy) throws ProtocolException, IOException {
      return this.protocol.getStrategy(strategy);
   }
}
