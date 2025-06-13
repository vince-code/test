package com.uppaal.model.system.concrete;

import com.uppaal.engine.RangeSet;
import com.uppaal.model.system.AbstractTransition;
import com.uppaal.model.system.SystemEdgeSelect;
import com.uppaal.model.system.SystemState;

public class ConcreteTransitionDelays extends AbstractTransition {
   private final int player;
   private final int status;
   private final RangeSet ranges;

   public ConcreteTransitionDelays(SystemEdgeSelect[] edges, int player, int status, RangeSet ranges) {
      super(edges);
      this.player = player;
      this.status = status;
      this.ranges = ranges;
   }

   public int getPlayer() {
      return this.player;
   }

   public int getStatus() {
      return this.status;
   }

   public RangeSet getRanges() {
      return this.ranges;
   }

   public boolean isSelectable() {
      return true;
   }

   public SystemState getTarget() {
      return null;
   }
}
