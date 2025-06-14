package com.uppaal.model.core2;

import com.uppaal.engine.EngineOptions;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Query extends Node {
   private static final String PROP_FORMULA = "formula";
   private static final String PROP_COMMENT = "comment";
   private final ArrayList<QueryResultListener> queryResultListeners;

   public Query(String formula, String comment) {
      super((Element)null);
      this.queryResultListeners = new ArrayList();

      assert formula != null && comment != null;

      this.setProperty("formula", formula);
      this.setProperty("comment", comment);
   }

   public Query(String formula) {
      this(formula, "");
   }

   public Query() {
      this("", "");
   }

   public CommandManager getCommandManager() {
      return this.getParent().getCommandManager();
   }

   public String getFriendlyName() {
      return "query";
   }

   public EngineSettings getSettings() {
      return (EngineSettings)this.getPropertyValue("settings");
   }

   public EngineSettings deriveSettings(EngineOptions options) {
      EngineSettings res = this.getSettings();
      if (res != null) {
         res = res.copy();
      } else {
         Document doc = this.getDocument();
         EngineSettings docSettings = doc.getSettings();
         if (docSettings != null) {
            res = docSettings.copy();
         } else {
            res = new EngineSettings(options.getDefaultSettings());
         }
      }

      return res;
   }

   public void setSettings(EngineSettings settings) {
      this.setProperty("settings", settings);
   }

   public void clearSettings() {
      this.setProperty("settings", (Object)null);
   }

   public boolean hasPlots() {
      return (Boolean)this.getFirstInstance(QueryResult.class).map((res) -> {
         return !res.getPlots().isEmpty();
      }).orElse(false);
   }

   public void addQueryResultListener(QueryResultListener l) {
      this.queryResultListeners.add(l);
   }

   public void removeQueryResultListener(QueryResultListener l) {
      this.queryResultListeners.remove(l);
   }

   public void remove() {
      super.remove();
   }

   /** @deprecated */
   @Deprecated
   private void fireResultsChanged(QueryResult result) {
      Object[] ls = this.queryResultListeners.toArray();
      Object[] var3 = ls;
      int var4 = ls.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object l = var3[var5];
         ((QueryResultListener)l).resultsChanged(this, result);
      }

   }

   public String getFormula() {
      return (String)Objects.requireNonNullElse((String)this.getPropertyValue("formula"), "");
   }

   public String getComment() {
      return (String)Objects.requireNonNullElse((String)this.getPropertyValue("comment"), "");
   }

   public void setFormula(String formula) {
      assert formula != null;

      this.setProperty("formula", formula);
   }

   public void setComment(String comment) {
      assert comment != null;

      this.setProperty("comment", comment);
   }

   public Property getFormulaProperty() {
      return this.getProperty("formula");
   }

   public Property getCommentProperty() {
      return this.getProperty("comment");
   }

   public void setFormulaAndComment(String formula, String comment) {
      if (formula != null) {
         this.setProperty("formula", formula);
      }

      if (comment != null) {
         this.setProperty("comment", comment);
      }

   }

   public void setResult(QueryResult result) {
      Optional<QueryResult> old = this.getFirstInstance(QueryResult.class);
      if (old.isPresent()) {
         this.insert(result, (Node)old.get());
         ((QueryResult)old.get()).remove();
      } else {
         this.insert(result, this.getLast());
      }

      this.fireResultsChanged(result);
   }

   public QueryResult getResult() {
      return (QueryResult)this.getFirstInstance(QueryResult.class).orElse(new QueryResult());
   }

   public QueryExpected getExpected() {
      return (QueryExpected)this.getFirstInstance(QueryExpected.class).orElse(new QueryExpected());
   }

   public void setExpected(QueryExpected expect) {
      Optional<QueryExpected> old = this.getFirstInstance(QueryExpected.class);
      if (old.isPresent()) {
         this.insert(expect, (Node)old.get());
         ((QueryExpected)old.get()).remove();
      } else {
         this.insert(expect, this.getLast());
      }

   }

   public String getShortFormula() {
      String s = this.getFormula();
      StringBuilder out = new StringBuilder();
      boolean spacePrinted = false;
      int last = 0;
      int i = 0;

      while(i < s.length()) {
         char c = s.charAt(i);
         ++i;
         if (Character.isWhitespace(c)) {
            if (spacePrinted) {
               last = i;
            } else {
               out.append(s, last, i - 1).append(' ');
               last = i;
               spacePrinted = true;
            }
         } else {
            spacePrinted = false;
         }
      }

      out.append(s.substring(last));
      return new String(out);
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitQuery(this);
   }

   public String toString() {
      String res = this.getShortFormula();
      if (res.length() > 60) {
         res = res.substring(0, 60) + "…";
      }

      return res;
   }
}
