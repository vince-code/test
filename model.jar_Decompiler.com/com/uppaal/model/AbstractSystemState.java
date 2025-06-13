package com.uppaal.model;

import com.uppaal.model.system.Polyhedron;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

public abstract class AbstractSystemState {
   private final int[] variables;
   private final Polyhedron zone;

   public AbstractSystemState(int[] v, Polyhedron z) {
      this.variables = v;
      this.zone = z;
   }

   public void writeServerFormat(Writer writer) throws IOException {
      AbstractSystemLocation[] locations = this.getLocationVector();
      AbstractSystemLocation[] var3 = locations;
      int var4 = locations.length;

      int var5;
      for(var5 = 0; var5 < var4; ++var5) {
         AbstractSystemLocation location = var3[var5];
         writer.write(String.valueOf(location.getIndex()));
         writer.write(10);
      }

      writer.write(".\n");
      this.zone.writeTextualFormat(writer);
      int[] var7 = this.variables;
      var4 = var7.length;

      for(var5 = 0; var5 < var4; ++var5) {
         int i = var7[var5];
         writer.write(String.valueOf(i));
         writer.write(10);
      }

      writer.write(46);
   }

   public void writeXTRFormat(Writer writer) throws IOException {
      this.writeServerFormat(writer);
      writer.write(10);
   }

   public String traceFormat() {
      StringBuilder s = new StringBuilder("(");
      boolean first = true;
      AbstractSystemLocation[] locations = this.getLocationVector();
      AbstractSystemLocation[] var4 = locations;
      int var5 = locations.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         AbstractSystemLocation location = var4[var6];
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         String name = location.getName();
         s.append(name != null && name.length() != 0 ? name : "-");
      }

      s.append(")");
      return s.toString();
   }

   public Polyhedron getPolyhedron() {
      return this.zone;
   }

   public int[] getVariables() {
      return this.variables;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else {
         AbstractSystemState s = (AbstractSystemState)obj;
         return Arrays.equals(this.getLocationVector(), s.getLocationVector()) && Arrays.equals(this.variables, s.variables) && this.zone.equals(s.zone);
      }
   }

   public abstract AbstractSystemLocation[] getLocationVector();
}
