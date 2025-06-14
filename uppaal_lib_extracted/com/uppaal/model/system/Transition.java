package com.uppaal.model.system;

import com.uppaal.model.system.symbolic.SymbolicState;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class Transition {
   private SystemEdge[] edges;
   private SymbolicState source;
   private SymbolicState target;
   private String edgeDescription;

   public Transition(SymbolicState source, SystemEdge[] edges, SymbolicState target) {
      this(source, edges, target, (String)null);
   }

   public Transition(SymbolicState source, SystemEdge[] edges, SymbolicState target, String edgeDescription) {
      this.source = source;
      this.edges = edges;
      this.target = target;
      if (edgeDescription != null) {
         this.edgeDescription = edgeDescription;
      } else {
         this.edgeDescription = makeEdgeDescription(edges);
      }

   }

   public String traceFormat() {
      StringBuffer s = new StringBuffer("(");
      boolean start = true;
      SystemEdge[] var3 = this.edges;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         SystemEdge edge = var3[var5];
         if (start) {
            start = false;
         } else {
            s.append(", ");
         }

         s.append(edge.getFormatedName());
      }

      s.append(')');
      return s.toString();
   }

   public String toServerFormat() {
      StringBuilder buf = new StringBuilder();
      buf.append(this.edges.length);
      SystemEdge[] var2 = this.edges;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SystemEdge edge = var2[var4];
         buf.append('\n');
         buf.append(edge.getProcess().getIndex()).append('\n');
         buf.append(edge.getIndex() + 1);
      }

      return buf.toString();
   }

   public void writeXTRFormat(Writer writer) throws IOException {
      this.getTarget().writeXTRFormat(writer);
      SystemEdge[] var2 = this.edges;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SystemEdge edge = var2[var4];
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

   public SystemEdge[] getEdges() {
      return this.edges;
   }

   public SystemEdge getEdge(int i) {
      return this.edges[i];
   }

   public int getSize() {
      return this.edges.length;
   }

   public boolean involvesProcess(int process) {
      SystemEdge[] var2 = this.edges;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SystemEdge edge = var2[var4];
         if (edge.getProcess().getIndex() == process) {
            return true;
         }
      }

      return false;
   }

   public String getEdgeDescription() {
      return this.edgeDescription;
   }

   private static String makeEdgeDescription(SystemEdge[] edges) {
      if (edges == null) {
         return null;
      } else if (edges.length == 0) {
         return "deadlock";
      } else {
         String channel = edges[0].getName();
         if (channel != null && channel.length() > 0) {
            String channelName = channel.substring(0, channel.length() - 1);
            LinkedHashSet<String> processNames = new LinkedHashSet();
            SystemEdge[] var4 = edges;
            int var5 = edges.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               SystemEdge e = var4[var6];
               processNames.add(e.getProcessName());
            }

            Iterator<String> namesIter = processNames.iterator();
            StringBuilder s = new StringBuilder();
            s.append(channelName);
            s.append(": ");
            s.append((String)namesIter.next());
            if (edges.length != 1 && edges.length <= 2) {
               s.append(" → ");
            } else {
               s.append(" ⇒ ");
            }

            while(namesIter.hasNext()) {
               s.append((String)namesIter.next());
               if (namesIter.hasNext()) {
                  s.append(", ");
               }
            }

            return s.toString();
         } else {
            return edges[0].getProcessName();
         }
      }
   }
}
