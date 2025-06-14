package com.uppaal.model.system.symbolic;

import com.uppaal.model.system.Polyhedron;
import com.uppaal.model.system.SystemLocation;
import com.uppaal.model.system.SystemState;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

public class SymbolicState extends SystemState {
   private int[] varValues;
   private final Polyhedron zone;

   public SymbolicState(SystemLocation[] l, int[] v, Polyhedron z) {
      super(l);
      this.varValues = v;
      this.zone = z;
   }

   public void writeServerFormat(Writer writer) throws IOException {
      SystemLocation[] var2 = this.getLocations();
      int var3 = var2.length;

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         SystemLocation location = var2[var4];
         writer.write(String.valueOf(location.getIndex()));
         writer.write(10);
      }

      writer.write(".\n");
      this.zone.writeTextualFormat(writer);
      int[] var6 = this.varValues;
      var3 = var6.length;

      for(var4 = 0; var4 < var3; ++var4) {
         int i = var6[var4];
         writer.write(String.valueOf(i));
         writer.write(10);
      }

      writer.write(46);
   }

   public void writeXTRFormat(Writer writer) throws IOException {
      this.writeServerFormat(writer);
      writer.write(10);
   }

   /** @deprecated */
   @Deprecated
   public SystemLocation[] getLocationVector() {
      return this.locations;
   }

   public Polyhedron getPolyhedron() {
      return this.zone;
   }

   public int[] getVariableValues() {
      return this.varValues;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else {
         SymbolicState s = (SymbolicState)obj;
         return Arrays.equals(this.locations, s.locations) && Arrays.equals(this.varValues, s.varValues) && this.zone.equals(s.zone);
      }
   }

   public void removeVariablesByIndices(int... sortedIndices) {
      int[] newValues = new int[this.varValues.length - sortedIndices.length];
      int j = 0;
      int l = 0;

      for(int i = 0; i < this.varValues.length; ++i) {
         if (l < sortedIndices.length && i == sortedIndices[l]) {
            ++l;
         } else {
            newValues[j++] = this.varValues[i];
         }
      }

      this.varValues = newValues;
   }
}
