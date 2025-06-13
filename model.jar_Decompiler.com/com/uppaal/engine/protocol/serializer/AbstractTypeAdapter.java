package com.uppaal.engine.protocol.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class AbstractTypeAdapter {
   public <T> T parse(Class<T> classType, JsonElement json, JsonDeserializationContext context) {
      if (json != null && context != null) {
         try {
            Object obj = classType.getConstructor().newInstance();
            Field[] var5 = classType.getDeclaredFields();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Field field = var5[var7];
               if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                  field.setAccessible(true);
                  String variableName = field.getName();
                  String serializedName = null;
                  if (field.isAnnotationPresent(SerializedName.class)) {
                     serializedName = ((SerializedName)field.getAnnotation(SerializedName.class)).value();
                  }

                  if (serializedName != null) {
                     field.set(obj, context.deserialize(json.getAsJsonObject().get(serializedName), field.getGenericType()));
                  } else {
                     field.set(obj, context.deserialize(json.getAsJsonObject().get(variableName), field.getGenericType()));
                  }
               }
            }

            return obj;
         } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException var11) {
            throw new RuntimeException(var11);
         }
      } else {
         throw new RuntimeException("AbstractTypeAdapter isn't initialized correctly");
      }
   }
}
