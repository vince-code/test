package com.uppaal.engine.protocol.viewmodel;

public class ConcreteHorizonQuery {
   ConcreteStateViewModel state;
   double horizon;

   public ConcreteHorizonQuery(ConcreteStateViewModel state, double horizon) {
      this.state = state;
      this.horizon = horizon;
   }
}
