package com.uppaal.model.system.concrete;

import com.uppaal.engine.ConcreteTraceElement;
import com.uppaal.model.system.AbstractTrace;
import java.util.List;

public class ConcreteTrace extends AbstractTrace<ConcreteTraceElement> {
   public ConcreteTrace() {
   }

   public ConcreteTrace(List<ConcreteTraceElement> trace) {
      super(trace);
   }

   public ConcreteTrace copy() {
      return new ConcreteTrace(this.transitions);
   }
}
