package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;
import com.uppaal.model.system.symbolic.SymbolicState;

public class CustomStateMCCommand {
   private SymbolicStateViewModel state;
   @SerializedName("query")
   private String formula;

   public CustomStateMCCommand(SymbolicState state, String shortFormula) {
      this.state = new SymbolicStateViewModel(state);
      this.formula = shortFormula;
   }
}
