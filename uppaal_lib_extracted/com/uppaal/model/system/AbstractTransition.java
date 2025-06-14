package com.uppaal.model.system;

import com.uppaal.model.core2.BranchPoint;
import com.uppaal.model.core2.Edge;
import java.util.Objects;

public abstract class AbstractTransition {
   protected final SystemEdgeSelect[] edges_ws;
   private String transitionDescription;

   public AbstractTransition(SystemEdgeSelect[] edges) {
      this.edges_ws = (SystemEdgeSelect[])Objects.requireNonNullElseGet(edges, () -> {
         return new SystemEdgeSelect[0];
      });
   }

   public String getTransitionDescription() {
      if (this.transitionDescription == null) {
         this.transitionDescription = this.computeTransitionDescription();
      }

      return this.transitionDescription;
   }

   private String computeTransitionDescription() {
      if (!this.hasEdges()) {
         return "deadlock";
      } else {
         StringBuilder s = new StringBuilder();
         this.appendChannel(s);
         this.appendEdges(s);
         return s.toString();
      }
   }

   public boolean hasEdges() {
      return this.edges_ws.length != 0;
   }

   private void appendEdges(StringBuilder s) {
      boolean drawArrow = true;
      int i = 0;

      while(i < this.edges_ws.length) {
         s.append(this.edges_ws[i].getProcessName());
         if (this.edges_ws.length > i + 1 && this.isBranchingEdge(i + 1, i)) {
            this.appendSelectList(s, i + 1);
            this.appendSelectList(s, i);
            i += 2;
         } else {
            this.appendSelectList(s, i);
            ++i;
         }

         if (drawArrow) {
            s.append(" → ");
            drawArrow = false;
         } else if (i < this.edges_ws.length) {
            s.append(", ");
         }
      }

   }

   private void appendSelectList(StringBuilder s, int edgeIndex) {
      if (this.edges_ws[edgeIndex].getSelectList().size() > 0) {
         s.append(this.edges_ws[edgeIndex].getSelectList());
      }

   }

   private void appendChannel(StringBuilder s) {
      String channel = this.edges_ws[0].getName();
      if (channel.isEmpty() && this.edges_ws.length >= 2 && this.isBranchingEdge(0, 1)) {
         channel = this.edges_ws[1].getName();
      }

      if (channel.length() > 0) {
         s.append(channel, 0, channel.length() - 1);
         s.append(": ");
      }

   }

   private boolean isBranchingEdge(int edgeIndex1, int edgeIndex2) {
      Edge edge1 = this.edges_ws[edgeIndex1].getEdge();
      Edge edge2 = this.edges_ws[edgeIndex2].getEdge();
      if (edge1.getTarget() == edge2.getSource()) {
         assert edge1.getTarget() instanceof BranchPoint;

         return true;
      } else {
         return false;
      }
   }

   public SystemEdgeSelect[] getEdges() {
      return this.edges_ws;
   }

   public SystemEdgeSelect getEdge(int i) {
      return this.edges_ws[i];
   }

   public int getSize() {
      return this.edges_ws.length;
   }

   public abstract SystemState getTarget();
}
