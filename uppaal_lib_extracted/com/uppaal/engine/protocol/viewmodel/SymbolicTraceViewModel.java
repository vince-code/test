package com.uppaal.engine.protocol.viewmodel;

import java.util.List;

public class SymbolicTraceViewModel extends TraceViewModel {
   private List<SymbolicTraceNode> trace;

   public SymbolicTraceViewModel(List<SymbolicTraceNode> trace) {
      this.trace = trace;
   }

   public List<SymbolicTraceNode> getTrace() {
      return this.trace;
   }
}
