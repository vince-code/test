package com.uppaal.model.system.symbolic;

import com.uppaal.model.system.AbstractTransition;
import com.uppaal.model.system.SystemEdgeSelect;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

public class SymbolicTransition extends AbstractTransition {
   private final SymbolicState source;
   private final SymbolicState target;

   public SymbolicTransition(SymbolicState source, SystemEdgeSelect[] edges, SymbolicState target) {
      super(edges);
      this.source = source;
      this.target = target;
   }

   public void writeXTRFormat(Writer writer) throws IOException {
      this.getTarget().writeXTRFormat(writer);
      SystemEdgeSelect[] var2 = this.edges_ws;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SystemEdgeSelect edge = var2[var4];
         edge.writeXTRFormat(writer);
      }

      writer.write(".\n");
   }

   public SymbolicState getSource() {
      return this.source;
   }

   public SymbolicState getTarget() {
      return this.target;
   }

   public String getEdgeDescription() {
      return this.getTransitionDescription();
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof SymbolicTransition)) {
         return false;
      } else {
         SymbolicTransition other = (SymbolicTransition)obj;
         return this.source.equals(other.getSource()) && this.target.equals(other.getTarget()) && Arrays.equals(this.edges_ws, other.getEdges());
      }
   }

   public void removeVariablesByIndices(int[] sortedIndices) {
      this.target.removeVariablesByIndices(sortedIndices);
   }
}
