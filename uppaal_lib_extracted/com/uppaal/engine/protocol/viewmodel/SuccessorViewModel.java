package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;

public class SuccessorViewModel extends SymbolicTraceNode {
   @SerializedName("src")
   private SymbolicStateViewModel source;
   @SerializedName("dest")
   protected SymbolicStateViewModel dest;

   public SymbolicStateViewModel getSource() {
      return this.source;
   }

   public SymbolicStateViewModel getDest() {
      return this.dest;
   }
}
