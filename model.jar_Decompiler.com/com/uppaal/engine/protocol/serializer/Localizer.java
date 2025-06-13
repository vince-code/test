package com.uppaal.engine.protocol.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Localizer implements JsonDeserializer<String> {
   private static final ResourceBundle locale = ResourceBundle.getBundle("locale.LexicalAnalyser");

   public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return this.localize(json.getAsString());
   }

   private String localize(String raw) {
      StringBuilder sb = new StringBuilder();
      int prevEnd = 0;
      Pattern delim = Pattern.compile("\\$([\\w_]+)");

      for(Matcher matcher = delim.matcher(raw); matcher.find(); prevEnd = matcher.end()) {
         sb.append(raw.substring(prevEnd, matcher.start()));
         String localize = raw.substring(matcher.start() + 1, matcher.end());
         if (locale.containsKey(localize)) {
            sb.append(locale.getString(raw.substring(matcher.start() + 1, matcher.end())));
         } else {
            sb.append(localize.replace("_", " "));
         }
      }

      sb.append(raw.substring(prevEnd));
      return sb.toString();
   }
}
