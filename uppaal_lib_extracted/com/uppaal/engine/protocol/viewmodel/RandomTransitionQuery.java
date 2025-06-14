package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;

public class RandomTransitionQuery {
   private final ConcreteStateViewModel state;
   @SerializedName("semantics")
   private final RandomTransitionQuery.RandomSemantics randomSemantics;
   private final double horizon;

   public RandomTransitionQuery(ConcreteStateViewModel state, RandomTransitionQuery.RandomSemantics randomSemantics, double horizon) {
      this.state = state;
      this.randomSemantics = randomSemantics;
      this.horizon = horizon;
   }

   public static enum RandomSemantics {
      Random,
      Stochastic;
   }
}
