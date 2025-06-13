package com.uppaal.engine.protocol;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.uppaal.engine.ProtocolException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Optional;

public class Response {
   public String type;
   public JsonElement content;
   private final JsonParser parser;

   public Response(String type, JsonElement content, JsonParser parser) {
      this.type = type;
      this.content = content;
      this.parser = parser;
   }

   public <T> Optional<T> as(Class<T> type) {
      if (this.content.isJsonObject()) {
         JsonObject jsonObject = this.content.getAsJsonObject();
         Field[] var3 = type.getFields();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            if (!Modifier.isTransient(field.getModifiers())) {
               SerializedName serializedName = (SerializedName)field.getAnnotation(SerializedName.class);
               String name = serializedName == null ? field.getName() : serializedName.value();
               if (!jsonObject.has(name)) {
                  return Optional.empty();
               }
            }
         }
      }

      return Optional.ofNullable(this.parser.parse(this.content, type));
   }

   public <T> T expect(Class<T> type) throws ProtocolException {
      return this.as(type).orElseThrow(this::unknownTypeException);
   }

   public <T> Optional<T> asGeneric(Type type) {
      return Optional.ofNullable(this.parser.parseGeneric(this.content, type));
   }

   public <T> T expectGeneric(Type type) throws ProtocolException {
      return this.asGeneric(type).orElseThrow(this::unknownTypeException);
   }

   private ProtocolException unknownTypeException() {
      return new ProtocolException("Unknown content, received: '" + this.content.toString() + "'");
   }
}
