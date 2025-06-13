package com.uppaal.model.core2;

import java.util.List;
import java.util.Optional;

public class QueryExpected extends Node {
   private QueryValue value;
   private List<QueryResource> resources;

   public QueryExpected() {
      super((Element)null);
   }

   public QueryExpected(QueryValue value, List<QueryResource> resources) {
      super((Element)null);
      this.value = value;
      this.resources = resources;
   }

   public QueryValue.Status getStatus() {
      return this.value.getStatus();
   }

   public void setStatus(QueryValue.Status status) {
      this.value.setStatus(status);
   }

   public QueryValue getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value.setValue(value);
   }

   public Optional<QueryResource> getResource(String type) {
      return this.resources.stream().filter((res) -> {
         return res.type.equals(type);
      }).findFirst();
   }

   public List<QueryResource> getResources() {
      return this.resources;
   }

   public String getFriendlyName() {
      return "expectation";
   }
}
