package com.uppaal.engine;

import com.google.gson.annotations.SerializedName;

public class Range {
   @SerializedName("start")
   public double start;
   @SerializedName("finish")
   public double end;

   public Range(double start, double end) {
      this.start = start;
      this.end = end;
   }

   public Range() {
   }
}
