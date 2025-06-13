package com.uppaal.language;

import com.google.gson.reflect.TypeToken;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.ProtocolException;
import com.uppaal.engine.connection.InitialConnection;
import com.uppaal.engine.protocol.JsonMessageParser;
import com.uppaal.engine.protocol.JsonMessageWriter;
import com.uppaal.engine.protocol.Response;
import com.uppaal.engine.protocol.viewmodel.BasicCommand;
import com.uppaal.engine.protocol.viewmodel.GenericCommand;
import com.uppaal.language.viewmodels.FindDeclarationRequest;
import com.uppaal.model.core2.Document;
import com.uppaal.model.io2.XMLWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Protocol {
   private final JsonMessageWriter writer;
   private final JsonMessageParser parser;
   private final Map<String, Consumer<Response>> notificationHandlers = new HashMap();
   public static final String closeCommand = "exit";
   private Response cachedMessage;

   private Protocol(InputStreamReader in, BufferedWriter out) {
      this.writer = new JsonMessageWriter(out, false);
      this.parser = new JsonMessageParser(in, LanguageServerCrashException.class);
   }

   public static Protocol handshake(InitialConnection connection) throws IOException, EngineException {
      InputStreamReader input = new InputStreamReader(connection.in, StandardCharsets.UTF_8);
      BufferedReader reader = new BufferedReader(input);
      String line = reader.readLine();
      if (line == null) {
         throw new EngineException("LanguageServer closed connection.");
      } else if ("json".equals(line)) {
         return new Protocol(input, connection.out);
      } else {
         throw new ProtocolException("Unknown protocol version: " + line);
      }
   }

   public void upload(Document document) throws IOException {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();

      try {
         document.accept(new XMLWriter(stream));
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }

      this.writeBasicCommand("upload", stream.toString());
      this.awaitResponse("upload");
   }

   public void setCurrentNode(String xpath) throws IOException {
      this.writeBasicCommand("change_node", xpath);
      this.awaitResponse("change_node");
   }

   private void writeBasicCommand(String command, String argument) throws IOException {
      BasicCommand msg = new BasicCommand(command, argument);
      this.writer.write(msg);
      this.writer.flush();
   }

   private <T> void writeCommand(String command, T argument) throws IOException {
      GenericCommand<T> msg = new GenericCommand(command, argument);
      this.writer.write(msg);
      this.writer.flush();
   }

   private Response awaitResponse(String responseType) {
      while(true) {
         Response message = this.readNext();
         if (message.type.startsWith("response/")) {
            if (message.type.substring(9).equals(responseType)) {
               return message;
            }

            throw new RuntimeException("Expected response of type '" + responseType + "' but got '" + message.type + "'");
         }

         if (!message.type.startsWith("notif/")) {
            if ("err".equals(message.type)) {
               throw new UlsException(message.content.toString());
            }

            throw new RuntimeException("Received unknown message type '" + message.type + "'");
         }

         this.handleNotification(message);
      }
   }

   private Response readNext() {
      Response response;
      if (this.cachedMessage == null) {
         response = this.parser.parseRawResponse();
         if ("err".equals(response.type)) {
            throw new UlsException((String)response.as(String.class).orElse("Failed with no error message"));
         } else {
            return response;
         }
      } else {
         response = this.cachedMessage;
         this.cachedMessage = null;
         return response;
      }
   }

   public void setHandler(String notification, Consumer<Response> handler) {
      this.notificationHandlers.put(notification, handler);
   }

   private void handleNotification(Response response) {
      Consumer<Response> handler = (Consumer)this.notificationHandlers.get(response.type.substring(6));
      handler.accept(response);
   }

   public ArrayList<Suggestion> autocomplete(String xpath, String identifier, int offset) throws IOException {
      FindDeclarationRequest request = new FindDeclarationRequest(xpath, identifier, offset);
      this.writeCommand("autocomplete", request);

      try {
         return (ArrayList)this.awaitResponse("autocomplete").expectGeneric((new TypeToken<ArrayList<Suggestion>>() {
         }).getType());
      } catch (ProtocolException var6) {
         return new ArrayList();
      }
   }

   public void close() throws IOException {
      this.writeBasicCommand("exit", "");
   }
}
