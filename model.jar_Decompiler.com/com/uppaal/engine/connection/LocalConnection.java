package com.uppaal.engine.connection;

import com.uppaal.engine.EngineException;
import java.io.File;
import java.util.ResourceBundle;

public class LocalConnection extends Connection {
   private final File serverBinary;
   private CommandConnection internal;

   public LocalConnection(String name, File serverBinary) {
      super(name);
      this.serverBinary = serverBinary;
      String[] command = new String[]{serverBinary.getAbsolutePath()};
      this.internal = new CommandConnection("internal", command);
   }

   public boolean isConnected() {
      return this.internal.isConnected();
   }

   public Connection clone() {
      return new LocalConnection(this.getName(), this.serverBinary);
   }

   public File getServerBinary() {
      return this.serverBinary;
   }

   public InitialConnection connect() throws EngineException {
      return this.internal.connect();
   }

   public void disconnect() {
      this.internal.disconnect();
   }

   public void kill() {
      this.internal.kill();
   }

   public String toString() {
      ResourceBundle LOCALE = ResourceBundle.getBundle("locale.SystemInspector");
      return LOCALE.getString("si_connectLocal");
   }
}
