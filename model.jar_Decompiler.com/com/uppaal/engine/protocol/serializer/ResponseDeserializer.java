package com.uppaal.engine.protocol.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.uppaal.engine.protocol.JsonParser;
import com.uppaal.engine.protocol.Response;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public class ResponseDeserializer implements JsonDeserializer<Response> {
   private final JsonParser parser;
   private final Constructor<? extends RuntimeException> exceptionConstructor;

   public ResponseDeserializer(JsonParser parser, Class<? extends RuntimeException> exceptionType) {
      this.parser = parser;

      try {
         this.exceptionConstructor = exceptionType.getConstructor(String.class);
      } catch (NoSuchMethodException var4) {
         throw new RuntimeException(var4);
      }
   }

   public Response deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      JsonObject object = json.getAsJsonObject();
      String response = (String)context.deserialize(object.get("res"), String.class);
      if (response == null) {
         String errorMessage = (String)context.deserialize(object.get("#error"), String.class);

         try {
            if (errorMessage != null) {
               throw (RuntimeException)this.exceptionConstructor.newInstance(errorMessage);
            } else {
               throw (RuntimeException)this.exceptionConstructor.newInstance("Json parser didn't receive response");
            }
         } catch (InvocationTargetException | IllegalAccessException | InstantiationException var8) {
            throw new RuntimeException(var8);
         }
      } else {
         return new Response(response, object.get("info"), this.parser);
      }
   }
}
