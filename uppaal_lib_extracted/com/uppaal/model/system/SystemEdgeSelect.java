package com.uppaal.model.system;

import com.uppaal.model.core2.Edge;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

public class SystemEdgeSelect extends SystemEdge {
   private final List<Integer> selectValues;

   public SystemEdgeSelect(Process process, int index, String name, Edge edge, List<Integer> v) {
      super(process, index, name, edge);
      this.selectValues = v;
   }

   public SystemEdgeSelect(SystemEdge system_edge, List<Integer> select_values) {
      super(system_edge.getProcess(), system_edge.getIndex(), system_edge.getName(), system_edge.getEdge());
      this.selectValues = select_values;
   }

   public List<Integer> getSelectList() {
      return this.selectValues;
   }

   public void writeXTRFormat(Writer writer) throws IOException {
      writer.write(String.valueOf(this.getProcess().getIndex()));
      writer.write(32);
      writer.write(String.valueOf(this.getIndex()));
      writer.write(32);
      Iterator var2 = this.getSelectList().iterator();

      while(var2.hasNext()) {
         Integer i = (Integer)var2.next();
         writer.write(String.valueOf(i));
         writer.write(32);
      }

      writer.write(59);
      writer.write(10);
   }

   public boolean isTheSame(SystemEdgeSelect e) {
      return this.getSelectList().equals(e.getSelectList()) && this.getProcess().getIndex() == e.getProcess().getIndex() && this.getIndex() == e.getIndex() && this.getName().equals(e.getName()) && this.getEdge().equals(e.getEdge());
   }

   public static boolean isSame(SystemEdgeSelect[] edges1, SystemEdgeSelect[] edges2) {
      if (edges1.length != edges2.length) {
         return false;
      } else {
         for(int i = 0; i < edges1.length; ++i) {
            if (!edges1[i].isTheSame(edges2[i])) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof SystemEdgeSelect)) {
         return false;
      } else {
         SystemEdgeSelect other = (SystemEdgeSelect)obj;
         return !this.selectValues.equals(other.getSelectList()) ? false : super.equals(other);
      }
   }
}
