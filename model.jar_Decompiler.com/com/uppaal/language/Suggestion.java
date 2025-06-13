package com.uppaal.language;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
   @SerializedName("name")
   public String content;
   @SerializedName("type")
   public Suggestion.Type type;

   public Suggestion(String content, Suggestion.Type type) {
      this.content = content;
      this.type = type;
   }

   public static enum Type {
      @SerializedName("function")
      FUNCTION,
      @SerializedName("variable")
      VARIABLE,
      @SerializedName("channel")
      CHANNEL,
      @SerializedName("type")
      TYPE,
      @SerializedName("process")
      PROCESS,
      @SerializedName("unknown")
      UNKNOWN;
   }
}
