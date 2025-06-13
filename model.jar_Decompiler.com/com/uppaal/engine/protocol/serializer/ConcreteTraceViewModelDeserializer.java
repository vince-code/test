package com.uppaal.engine.protocol.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.uppaal.engine.protocol.viewmodel.ConcreteTraceViewModel;
import java.lang.reflect.Type;

public class ConcreteTraceViewModelDeserializer extends AbstractTypeAdapter implements JsonDeserializer<ConcreteTraceViewModel> {
   public ConcreteTraceViewModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = json.getAsJsonObject();
      String fileTraceVersion = (String)context.deserialize(object.get("uppaaltraceversion"), String.class);

      try {
         ConcreteTraceViewModel model = (ConcreteTraceViewModel)this.parse(ConcreteTraceViewModel.class, json, context);
         return model;
      } catch (JsonParseException var7) {
         if (ConcreteTraceViewModel.getUppaalTraceVersion().equals(fileTraceVersion)) {
            throw var7;
         } else {
            throw new JsonParseException("Got trace format version " + fileTraceVersion + ". Expected " + ConcreteTraceViewModel.getUppaalTraceVersion() + "\nFailed parsing with: " + var7);
         }
      }
   }
}
