package com.uppaal.engine.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.uppaal.engine.protocol.serializer.ConcreteTraceViewModelDeserializer;
import com.uppaal.engine.protocol.serializer.ConcreteTransitionViewModelDeserializer;
import com.uppaal.engine.protocol.serializer.Localizer;
import com.uppaal.engine.protocol.serializer.QueryResultViewModelDeserializer;
import com.uppaal.engine.protocol.serializer.ResponseDeserializer;
import com.uppaal.engine.protocol.viewmodel.ConcreteTraceViewModel;
import com.uppaal.engine.protocol.viewmodel.ConcreteTransitionViewModel;
import com.uppaal.engine.protocol.viewmodel.QueryResultViewModel;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class JsonParser {
   private final Gson reader;
   private final InputStreamReader in;

   public JsonParser(byte[] arr, Class<? extends RuntimeException> engineSpecificException) {
      this(new InputStreamReader(new ByteArrayInputStream(arr), StandardCharsets.UTF_8), engineSpecificException);
   }

   public JsonParser(InputStream in, Class<? extends RuntimeException> engineSpecificException) {
      this(new InputStreamReader(in, StandardCharsets.UTF_8), engineSpecificException);
   }

   public JsonParser(InputStreamReader in, Class<? extends RuntimeException> engineSpecificException) {
      this.in = in;
      this.reader = (new GsonBuilder()).serializeSpecialFloatingPointValues().registerTypeAdapter(String.class, new Localizer()).registerTypeAdapter(Response.class, new ResponseDeserializer(this, engineSpecificException)).registerTypeAdapter(ConcreteTransitionViewModel.class, new ConcreteTransitionViewModelDeserializer()).registerTypeAdapter(QueryResultViewModel.class, new QueryResultViewModelDeserializer()).registerTypeAdapter(ConcreteTraceViewModel.class, new ConcreteTraceViewModelDeserializer()).create();
   }

   public <T> T parse(Class<T> type) {
      JsonReader read = new JsonReader(this.in);
      return this.reader.fromJson(read, type);
   }

   public <T> T parse(JsonElement element, Class<T> type) {
      return this.reader.fromJson(element, type);
   }

   public <T> T parseGeneric(JsonElement element, Type type) {
      return this.reader.fromJson(element, type);
   }
}
