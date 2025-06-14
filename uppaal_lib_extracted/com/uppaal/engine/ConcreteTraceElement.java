package com.uppaal.engine;

import com.uppaal.model.core2.DataSet2D;
import com.uppaal.model.system.AbstractTransition;
import com.uppaal.model.system.GanttAddition;
import com.uppaal.model.system.SystemEdgeSelect;
import com.uppaal.model.system.concrete.ConcreteState;
import com.uppaal.model.system.concrete.ConcreteTransitionDelays;
import java.util.List;

public class ConcreteTraceElement extends AbstractTransition {
   protected ConcreteState start;
   protected ConcreteState target;
   private final double delay;
   private DataSet2D trajectoryData;
   private ConcreteTransitionDelays[] transitions;
   private double maxDelay;
   private double horizon;
   private List<GanttAddition> ganttChart;

   public ConcreteTraceElement(double delay, SystemEdgeSelect[] edges, ConcreteState start, ConcreteState target) {
      super(edges);
      this.transitions = new ConcreteTransitionDelays[0];
      this.maxDelay = -1.0D;
      this.horizon = -1.0D;

      assert start != target;

      this.target = target;
      this.start = start;
      this.delay = delay;
   }

   public ConcreteTraceElement(double delay, SystemEdgeSelect[] edges, ConcreteState start, ConcreteState target, ConcreteTransitionDelays[] transitions, List<GanttAddition> ganttChart, double maxDelay) {
      this(delay, edges, start, target);
      this.transitions = transitions;
      this.ganttChart = ganttChart;
      this.maxDelay = maxDelay;
   }

   public ConcreteState getStart() {
      return this.start;
   }

   public ConcreteState getTarget() {
      return this.target;
   }

   public void setTarget(ConcreteState t) {
      this.target = t;
   }

   public double getDelay() {
      return this.delay;
   }

   public void setTransitions(ConcreteTransitionDelays[] transitions) {
      this.transitions = transitions;
   }

   public void setMaxDelay(double maxDelay) {
      this.maxDelay = maxDelay;
   }

   public void setHorizon(double horizon) {
      this.horizon = horizon;
   }

   public double getHorizon() {
      return this.horizon;
   }

   public void setTrajectory(DataSet2D trajectory) {
      this.trajectoryData = trajectory;
   }

   public DataSet2D getTrajectory() {
      return this.trajectoryData;
   }

   public String traceFormat() {
      StringBuilder s = new StringBuilder("(");
      boolean start = true;
      SystemEdgeSelect[] var3 = this.edges_ws;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         SystemEdgeSelect edge = var3[var5];
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

   public boolean equals(Object obj) {
      if (!(obj instanceof ConcreteTraceElement)) {
         return false;
      } else {
         ConcreteTraceElement other = (ConcreteTraceElement)obj;
         if (this.getDelay() != other.getDelay()) {
            return false;
         } else {
            return this.getTarget().equals(other.getTarget());
         }
      }
   }

   public List<GanttAddition> getGanttChart() {
      return this.ganttChart;
   }

   public ConcreteTransitionDelays[] getTransitions() {
      return this.transitions;
   }

   public double getMaxDelay() {
      return this.maxDelay;
   }

   public String getTransitionDescription() {
      if (this.hasEdges()) {
         double var10000 = this.getDelay();
         return "Delay: " + var10000 + "; " + super.getTransitionDescription();
      } else {
         return "Delay: " + this.getDelay();
      }
   }

   public String toString() {
      return "(ConcreteTransitionRecord (" + this.target + ", " + this.edges_ws + ", " + this.delay + ")";
   }
}
