package com.uppaal.engine.protocol.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class EnumSerializer<T extends Enum> implements JsonSerializer<T>, JsonDeserializer<T> {
   private Class<T> enumClass;

   public EnumSerializer(Class<T> enumClass) {
      this.enumClass = enumClass;
   }

   public JsonPrimitive serialize(Enum src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.ordinal());
   }

   public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      T[] types = (Enum[])this.enumClass.getEnumConstants();
      int index = json.getAsInt();
      return index <= types.length && index >= 1 ? types[index - 1] : null;
   }
}
