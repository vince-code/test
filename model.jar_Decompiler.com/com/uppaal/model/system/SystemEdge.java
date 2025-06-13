package com.uppaal.model.system;

import com.uppaal.model.core2.Edge;
import java.io.IOException;
import java.io.Writer;

public class SystemEdge {
   private final Process process;
   private final int index;
   private final String name;
   private final Edge edge;

   public SystemEdge(Process process, int index, String name, Edge edge) {
      this.process = process;
      this.index = index;
      this.name = name;
      this.edge = edge;
   }

   public Process getProcess() {
      return this.process;
   }

   public String getProcessName() {
      return this.process.getName();
   }

   public int getIndex() {
      return this.index;
   }

   public String getName() {
      return this.name;
   }

   public String getFormatedName() {
      String var10000 = this.process.getName();
      return var10000 + "." + this.index + (this.name != null ? "." + this.name : "");
   }

   public Edge getEdge() {
      return this.edge;
   }

   public void writeXTRFormat(Writer writer) throws IOException {
      writer.write(String.valueOf(this.getProcess().getIndex()));
      writer.write(32);
      writer.write(String.valueOf(this.getIndex() + 1));
      writer.write(10);
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof SystemEdge)) {
         return false;
      } else {
         SystemEdge other = (SystemEdge)obj;
         if (!this.process.equals(other.process)) {
            return false;
         } else if (other.index != this.index) {
            return false;
         } else if (!this.name.equals(other.name)) {
            return false;
         } else {
            return this.edge.equals(other.edge);
         }
      }
   }
}
