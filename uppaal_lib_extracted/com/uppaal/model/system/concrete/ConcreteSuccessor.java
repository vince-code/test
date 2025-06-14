package com.uppaal.model.system.concrete;

import com.uppaal.engine.CannotEvaluateException;
import com.uppaal.model.system.GanttAddition;
import java.util.List;

public class ConcreteSuccessor {
   private final ConcreteState state;
   private final ConcreteTransitionDelays[] transitions;
   private final List<GanttAddition> chart;
   private final double maxDelay;
   private final CannotEvaluateException e;

   public ConcreteSuccessor(ConcreteState state, ConcreteTransitionDelays[] transitions, List<GanttAddition> chart, double maxDelay) {
      this.state = state;
      this.transitions = transitions;
      this.e = null;
      this.chart = chart;
      this.maxDelay = maxDelay;
   }

   public ConcreteSuccessor(ConcreteState state, CannotEvaluateException e, List<GanttAddition> chart, double maxDelay) {
      this.state = state;
      this.transitions = new ConcreteTransitionDelays[0];
      this.e = e;
      this.chart = chart;
      this.maxDelay = maxDelay;
   }

   public ConcreteState getState() {
      return this.state;
   }

   public ConcreteTransitionDelays[] getTransitions() {
      return this.transitions;
   }

   public CannotEvaluateException getException() {
      return this.e;
   }

   public List<GanttAddition> getGanttChart() {
      return this.chart;
   }

   public double getMaxDelay() {
      return this.maxDelay;
   }
}
