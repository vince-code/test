package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class QueryResultViewModel {
   @SerializedName("status")
   private transient ModelCheckStatus status;
   @SerializedName("error")
   private ErrorMessage error;
   @SerializedName("result")
   private String result = "";
   @SerializedName("message")
   private String message = "";
   @SerializedName("plots")
   private List<PlotViewModel> plots = Collections.emptyList();
   @SerializedName("cyclelen")
   private int cycleLength;
   @SerializedName("strategy_decl")
   private String strategy;
   private transient TraceViewModel trace;

   public ModelCheckStatus parseModelCheckStatus(String s) {
      byte var3 = -1;
      switch(s.hashCode()) {
      case 69:
         if (s.equals("E")) {
            var3 = 4;
         }
         break;
      case 70:
         if (s.equals("F")) {
            var3 = 1;
         }
      case 71:
      case 72:
      case 74:
      case 75:
      case 76:
      case 79:
      case 80:
      case 81:
      case 82:
      case 83:
      default:
         break;
      case 73:
         if (s.equals("I")) {
            var3 = 5;
         }
         break;
      case 77:
         if (s.equals("M")) {
            var3 = 2;
         }
         break;
      case 78:
         if (s.equals("N")) {
            var3 = 3;
         }
         break;
      case 84:
         if (s.equals("T")) {
            var3 = 0;
         }
      }

      switch(var3) {
      case 0:
         return ModelCheckStatus.TRUE;
      case 1:
         return ModelCheckStatus.FALSE;
      case 2:
         return ModelCheckStatus.MAYBE_TRUE;
      case 3:
         return ModelCheckStatus.MAYBE_FALSE;
      case 4:
         return ModelCheckStatus.ERROR;
      case 5:
         return ModelCheckStatus.INTERRUPTED;
      default:
         return ModelCheckStatus.__INVALID;
      }
   }

   public void setTrace(TraceViewModel trace) {
      assert this.trace == null;

      this.trace = trace;
   }

   public void setStatus(String status) {
      assert this.status == null;

      this.status = this.parseModelCheckStatus(status);
   }

   public char getStatusRepresentation() {
      switch(this.status) {
      case TRUE:
         return 'T';
      case FALSE:
         return 'F';
      case MAYBE_TRUE:
         return 'M';
      case MAYBE_FALSE:
         return 'N';
      case ERROR:
         return 'E';
      case INTERRUPTED:
         return 'I';
      case __INVALID:
         return '\u0000';
      default:
         throw new RuntimeException("Error: Invalid value in query result status field");
      }
   }

   public ModelCheckStatus getStatus() {
      return this.status;
   }

   public void setError(ErrorMessage em) {
      this.error = em;
   }

   public ErrorMessage getError() {
      return this.error;
   }

   public void setResult(String result) {
      this.result = result;
   }

   public String getResult() {
      return this.result;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getMessage() {
      return this.message;
   }

   public void setPlots(List<PlotViewModel> plots) {
      this.plots = plots;
   }

   public List<PlotViewModel> getPlots() {
      return this.plots;
   }

   public void setCycleLength(int cycleLength) {
      this.cycleLength = cycleLength;
   }

   public int getCycleLength() {
      return this.cycleLength;
   }

   public TraceViewModel getTrace() {
      return this.trace;
   }

   public boolean hasSymbolicTrace() {
      return this.trace != null && this.trace instanceof SymbolicTraceViewModel && ((SymbolicTraceViewModel)this.trace).getTrace() != null;
   }

   public boolean hasConcreteTrace() {
      return this.trace != null && this.trace instanceof ConcreteTraceViewModel && ((ConcreteTraceViewModel)this.trace).transitions != null;
   }

   public Optional<List<SymbolicTraceNode>> getSymbolicTrace() {
      return this.trace instanceof SymbolicTraceViewModel && ((SymbolicTraceViewModel)this.trace).getTrace() != null ? Optional.of(((SymbolicTraceViewModel)this.trace).getTrace()) : Optional.empty();
   }

   public Optional<ConcreteTraceViewModel> getConcreteTrace() {
      return this.trace instanceof ConcreteTraceViewModel ? Optional.of((ConcreteTraceViewModel)this.trace) : Optional.empty();
   }

   public void setStrategy(String strategy) {
      this.strategy = strategy;
   }

   public Optional<String> getStrategy() {
      return Optional.ofNullable(this.strategy);
   }
}
