package com.uppaal.engine.protocol.viewmodel;

import com.uppaal.engine.ConcreteTraceElement;
import com.uppaal.model.system.SystemEdgeSelect;
import java.util.ArrayList;
import java.util.List;

public class ConcreteTraceNode {
   private double delay;
   private List<EdgeFieldViewModel> edges;
   private ConcreteStateViewModel state;

   public double getDelay() {
      return this.delay;
   }

   public List<EdgeFieldViewModel> getEdges() {
      return this.edges;
   }

   public ConcreteStateViewModel getState() {
      return this.state;
   }

   public ConcreteTraceNode() {
   }

   public ConcreteTraceNode(ConcreteTraceElement record) {
      this.delay = record.getDelay();
      this.edges = new ArrayList();
      SystemEdgeSelect[] var2 = record.getEdges();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SystemEdgeSelect edge = var2[var4];
         this.edges.add(new EdgeFieldViewModel(edge));
      }

      this.state = new ConcreteStateViewModel(record.getTarget());
   }
}
