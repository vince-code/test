package com.uppaal.model.system.concrete;

import com.uppaal.model.system.SystemEdgeSelect;

public class RandomTransition {
   public final SystemEdgeSelect[] edges;
   public final double delay;

   public RandomTransition(SystemEdgeSelect[] edges, double delay) {
      this.edges = edges;
      this.delay = delay;
   }
}
