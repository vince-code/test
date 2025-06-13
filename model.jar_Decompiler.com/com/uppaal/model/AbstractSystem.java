package com.uppaal.model;

import com.uppaal.model.core2.Document;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class AbstractSystem {
   protected ArrayList<String> variables;
   protected ArrayList<String> clocks;
   protected Document document;

   public AbstractSystem() {
      this.clocks = new ArrayList();
      this.variables = new ArrayList();
      this.document = null;
   }

   public AbstractSystem(Document aDocument) {
      this.document = aDocument;
      this.clocks = new ArrayList();
      this.variables = new ArrayList();
   }

   public abstract void addProcess(String var1, String var2, Translator var3);

   public void setVariables(ArrayList<String> value) {
      this.variables = value;
   }

   public void setClocks(ArrayList<String> value) {
      this.clocks = value;
   }

   public int getNoOfVariables() {
      return this.variables.size();
   }

   public String getVariableName(int i) {
      return (String)this.variables.get(i);
   }

   public int getNoOfClocks() {
      return this.clocks.size();
   }

   public String getClockName(int i) {
      return (String)this.clocks.get(i);
   }

   public ArrayList<String> getClockNames() {
      return this.clocks;
   }

   public ArrayList<String> getVariables() {
      return this.variables;
   }

   public Document getDocument() {
      return this.document;
   }

   public boolean[] getVisible() {
      boolean[] visible = new boolean[this.getNoOfVariables() + this.getNoOfClocks()];
      Arrays.fill(visible, true);

      int i;
      for(i = 0; i < this.getNoOfVariables(); ++i) {
         visible[i] &= !this.getVariableName(i).startsWith("#");
      }

      for(i = this.getNoOfVariables(); i < this.getNoOfVariables() + this.getNoOfClocks(); ++i) {
         visible[i] &= !this.getClockName(i - this.getNoOfVariables()).startsWith("#");
      }

      return visible;
   }
}
