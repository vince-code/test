package com.uppaal.engine.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uppaal.engine.protocol.serializer.EnumSerializer;
import com.uppaal.engine.protocol.viewmodel.BasicCommand;
import com.uppaal.engine.protocol.viewmodel.GenericCommand;
import com.uppaal.engine.protocol.viewmodel.ModelCheckStatus;
import com.uppaal.engine.protocol.viewmodel.RandomTransitionQuery;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class JsonMessageWriter {
   private final BufferedWriter out;
   private final Gson writer;
   private final boolean useUppaalJson;

   public JsonMessageWriter(BufferedWriter out) {
      this(out, true);
   }

   public JsonMessageWriter(BufferedWriter out, boolean useUppaalJson) {
      this.writer = (new GsonBuilder()).serializeSpecialFloatingPointValues().disableHtmlEscaping().registerTypeAdapter(RandomTransitionQuery.RandomSemantics.class, new EnumSerializer(RandomTransitionQuery.RandomSemantics.class)).registerTypeAdapter(ModelCheckStatus.class, new EnumSerializer(ModelCheckStatus.class)).create();
      this.out = out;
      this.useUppaalJson = useUppaalJson;
   }

   public JsonMessageWriter(OutputStream out) {
      this(new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8)));
   }

   public <T> void write(T value) throws IOException {
      String json = this.writer.toJson(value);
      if (this.useUppaalJson) {
         json = json.replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r");
      }

      this.out.write(json);
   }

   public void writeEmptyCommand(String message) throws IOException {
      this.writeCommand(message, "");
   }

   public <T> void writeCommand(String messageType, T content) throws IOException {
      this.write(new GenericCommand(messageType, content));
      this.flush();
   }

   public void writeCommand(String cmd, String args) throws IOException {
      BasicCommand msg = new BasicCommand(cmd, args);
      this.write(msg);
      this.flush();
   }

   public void flush() throws IOException {
      this.out.flush();
   }
}
