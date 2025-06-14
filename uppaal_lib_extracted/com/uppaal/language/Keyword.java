package com.uppaal.language;

import com.google.gson.annotations.SerializedName;

public class Keyword {
   @SerializedName("id")
   public String identifier;
   @SerializedName("type")
   public String type;
   @SerializedName("start")
   public int start;
   @SerializedName("end")
   public int end;
}
