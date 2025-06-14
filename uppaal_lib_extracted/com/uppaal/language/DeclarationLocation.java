package com.uppaal.language;

import com.google.gson.annotations.SerializedName;

public class DeclarationLocation {
   @SerializedName("xpath")
   public String xpath;
   @SerializedName("start")
   public int start;
   @SerializedName("end")
   public int end;
}
