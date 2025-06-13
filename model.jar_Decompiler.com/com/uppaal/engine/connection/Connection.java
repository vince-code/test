package com.uppaal.engine.connection;

import com.uppaal.engine.EngineException;

public abstract class Connection implements Cloneable {
   private final String name;
   private MonitorWriter monitor;

   public abstract InitialConnection connect() throws EngineException;

   public InitialConnection connectWithMonitor() throws EngineException {
      InitialConnection connection = this.connect();
      this.monitor = new MonitorWriter(connection.out);
      return new InitialConnection(connection.in, this.monitor);
   }

   public abstract void disconnect();

   public abstract void kill();

   public abstract boolean isConnected();

   public String getErrorStream() {
      return "";
   }

   public Connection(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public boolean hasTrace() {
      return this.monitor != null;
   }

   public String dumpTrace() {
      return this.hasTrace() ? this.monitor.getSnapshot() : "";
   }

   public abstract Connection clone();
}
