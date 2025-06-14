package com.uppaal.model.core2;

public class Series extends Node {
   private static final String PROP_TITLE = "title";
   private static final String PROP_EXPR = "expr";

   public Series(String title, String expression) {
      super((Element)null);
      this.setProperty("title", title);
      this.setProperty("expr", expression);
   }

   public String getTitle() {
      return (String)this.getPropertyValue("title");
   }

   public String getExpression() {
      return (String)this.getPropertyValue("expr");
   }

   public String getFriendlyName() {
      return "series";
   }
}
