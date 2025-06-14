package com.uppaal.engine.connection;

import com.uppaal.engine.EngineException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class RemoteConnection extends Connection {
   private final int serverPort;
   private final String serverHost;
   private Socket socket;

   public RemoteConnection(String name, String serverHost, int serverPort) {
      super(name);
      this.serverHost = serverHost;
      this.serverPort = serverPort;
   }

   public int getServerPort() {
      return this.serverPort;
   }

   public String getServerHost() {
      return this.serverHost;
   }

   public InitialConnection connect() throws EngineException {
      try {
         this.socket = new Socket(this.serverHost, this.serverPort);
         OutputStream out = this.socket.getOutputStream();
         InputStream in = this.socket.getInputStream();
         BufferedWriter bufferedOut = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
         return new InitialConnection(in, bufferedOut);
      } catch (ConnectException var4) {
         String var10002 = this.serverHost;
         throw new ServerUnavailableException("Could not connect to " + var10002 + ":" + this.serverPort + ". " + var4.getMessage());
      } catch (IOException var5) {
         throw new ServerUnavailableException(var5.getMessage());
      }
   }

   public void disconnect() {
      if (this.isConnected()) {
         try {
            this.socket.close();
         } catch (IOException var2) {
         }

         this.socket = null;
      }
   }

   public void kill() {
      this.disconnect();
   }

   public boolean isConnected() {
      return this.socket != null;
   }

   public Connection clone() {
      return new RemoteConnection(this.getName(), this.serverHost, this.serverPort);
   }

   public String toString() {
      ResourceBundle LOCALE = ResourceBundle.getBundle("locale.SystemInspector");
      return MessageFormat.format(LOCALE.getString("si_connectRemote"), this.serverHost, this.serverPort);
   }
}
