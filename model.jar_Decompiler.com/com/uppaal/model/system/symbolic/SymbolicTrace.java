package com.uppaal.model.system.symbolic;

import com.uppaal.model.system.AbstractTrace;
import com.uppaal.model.system.SystemEdgeSelect;
import java.util.List;

public class SymbolicTrace extends AbstractTrace<SymbolicTransition> {
   public int cycle = -1;

   public SymbolicTrace() {
   }

   public SymbolicTrace(List<SymbolicTransition> transitionList) {
      super(transitionList);
   }

   public SymbolicTrace(SymbolicState state) {
      this.add(new SymbolicTransition((SymbolicState)null, (SystemEdgeSelect[])null, state));
   }

   public SymbolicTrace copy() {
      return new SymbolicTrace(this.transitions);
   }
}
