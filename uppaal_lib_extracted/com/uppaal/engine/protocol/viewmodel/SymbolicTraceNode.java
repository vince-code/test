package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;
import com.uppaal.model.system.SystemEdgeSelect;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import java.util.ArrayList;
import java.util.List;

public class SymbolicTraceNode {
   @SerializedName("trans")
   protected List<EdgeFieldViewModel> transition;
   @SerializedName("state")
   protected SymbolicStateViewModel dest;

   public List<EdgeFieldViewModel> getTransition() {
      return this.transition;
   }

   public SymbolicStateViewModel getDest() {
      return this.dest;
   }

   public SymbolicTraceNode() {
   }

   public SymbolicTraceNode(SymbolicTransition model) {
      this.dest = new SymbolicStateViewModel(model.getTarget());
      this.transition = new ArrayList();
      SystemEdgeSelect[] var2 = model.getEdges();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SystemEdgeSelect edge = var2[var4];
         this.transition.add(new EdgeFieldViewModel(edge));
      }

   }
}
