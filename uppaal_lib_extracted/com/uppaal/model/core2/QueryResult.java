package com.uppaal.model.core2;

import com.uppaal.engine.CannotEvaluateException;
import com.uppaal.engine.protocol.viewmodel.ErrorMessage;
import com.uppaal.engine.protocol.viewmodel.QueryResultViewModel;
import com.uppaal.model.system.concrete.ConcreteTrace;
import com.uppaal.model.system.symbolic.SymbolicTrace;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class QueryResult extends Node {
   private static final String PLOTS = "plots";
   private static final String CONCRETE_TRACE = "concrete_trace";
   private static final String SYMBOLIC_TRACE = "symbolic_trace";
   private QueryValue value;
   private CannotEvaluateException exception;
   private String message;
   private String queryInfo;
   private String timestamp;
   private Optional<String> strategy;
   private List<QueryResource> resources;

   public QueryResult(ErrorMessage error) {
      this(new CannotEvaluateException(error));
   }

   public QueryResult(CannotEvaluateException e) {
      super((Element)null);
      this.value = new QueryValue();
      this.exception = null;
      this.message = null;
      this.strategy = Optional.empty();
      this.resources = new ArrayList();
      this.value = new QueryValue(QueryValue.Status.Unchecked, QueryValue.Kind.Unknown, "");
      this.exception = e;
   }

   public QueryResult(String errorMessage) {
      super((Element)null);
      this.value = new QueryValue();
      this.exception = null;
      this.message = null;
      this.strategy = Optional.empty();
      this.resources = new ArrayList();
      this.value = new QueryValue(QueryValue.Status.Unchecked, QueryValue.Kind.Unknown, errorMessage);
      this.message = errorMessage;
   }

   public QueryResult(char result) {
      super((Element)null);
      this.value = new QueryValue();
      this.exception = null;
      this.message = null;
      this.strategy = Optional.empty();
      this.resources = new ArrayList();
      this.setResult(result);
   }

   public QueryResult(QueryResultViewModel model) {
      this(model.getStatusRepresentation());
      this.strategy = model.getStrategy();
   }

   public QueryResult() {
      this('E');
   }

   public void setResult(char result) {
      this.value.setStatus(result);
   }

   public void setResult(String result) {
      this.value.setStatus(result);
   }

   public QueryValue getValue() {
      return this.value;
   }

   public List<QueryResource> getResources() {
      return this.resources;
   }

   public void setResources(List<QueryResource> resources) {
      this.resources = resources;
   }

   public char getResult() {
      return this.value.getStatusLabel();
   }

   public QueryValue.Status getStatus() {
      return this.value.getStatus();
   }

   public void set(CannotEvaluateException exc) {
      this.exception = exc;
   }

   public CannotEvaluateException getException() {
      return this.exception;
   }

   public void setMessage(String msg) {
      this.message = msg;
   }

   public String getMessage() {
      return this.message;
   }

   public EngineSettings getSettings() {
      return (EngineSettings)this.getPropertyValue("settings");
   }

   public void setSettings(EngineSettings settings) {
      this.setProperty("settings", settings);
   }

   public String getQueryInfo() {
      return this.queryInfo;
   }

   public void setQueryInfo(String queryInfo) {
      this.queryInfo = queryInfo;
   }

   public String getStatusString() {
      switch(this.value.getStatusLabel()) {
      case 'E':
      default:
         return "UNCHECKED";
      case 'F':
         return "NOT_OK";
      case 'M':
         return "MAYBE_OK";
      case 'N':
         return "MAYBE_NOT_OK";
      case 'T':
         return "OK";
      }
   }

   public String toString() {
      String res = this.getStatusString();
      if (this.message != null && !this.message.isBlank()) {
         res = res + ": " + this.message;
      }

      return res;
   }

   public String getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(String instance) {
      this.timestamp = instance;
   }

   public String getFriendlyName() {
      return "result";
   }

   public void addPlot(DataSet2D plot) {
      List<DataSet2D> plots = (List)this.getPropertyValue("plots");
      if (plots == null) {
         plots = new ArrayList();
      }

      ((List)plots).add(plot);
      this.setProperty("plots", plots);
   }

   public List<DataSet2D> getPlots() {
      List<DataSet2D> plots = (List)this.getPropertyValue("plots");
      return plots != null ? plots : Collections.emptyList();
   }

   public void setConcreteTrace(ConcreteTrace trace) {
      this.setProperty("concrete_trace", trace);
   }

   public Optional<ConcreteTrace> getConcreteTrace() {
      return Optional.ofNullable((ConcreteTrace)this.getPropertyValue("concrete_trace"));
   }

   public void setSymbolicTrace(SymbolicTrace trace) {
      this.setProperty("symbolic_trace", trace);
   }

   public Optional<SymbolicTrace> getSymbolicTrace() {
      return Optional.ofNullable((SymbolicTrace)this.getPropertyValue("symbolic_trace"));
   }

   public Optional<String> getStrategy() {
      return this.strategy;
   }
}
