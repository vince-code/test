package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;
import com.uppaal.engine.ConcreteTraceElement;
import com.uppaal.model.system.concrete.ConcreteTrace;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class ConcreteTraceViewModel extends TraceViewModel {
   @SerializedName("uppaaltraceversion")
   private static final String CONCRETE_TRACE_VERSION = "c1";
   public ConcreteStateViewModel init;
   public List<ConcreteTraceNode> transitions;

   public ConcreteTraceViewModel() {
   }

   public ConcreteTraceViewModel(ConcreteTrace trace) {
      if (trace.get(0) == null) {
         throw new IllegalArgumentException("Failed creating Concrete Trace, first element is missing");
      } else {
         this.init = new ConcreteStateViewModel(((ConcreteTraceElement)trace.get(0)).getTarget());
         this.transitions = new ArrayList();
         Iterable<ConcreteTraceElement> itt = () -> {
            return trace.iterator();
         };
         StreamSupport.stream(itt.spliterator(), false).skip(1L).forEach((e) -> {
            this.transitions.add(new ConcreteTraceNode(e));
         });
      }
   }

   public static String getUppaalTraceVersion() {
      return "c1";
   }
}
