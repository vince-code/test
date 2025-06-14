package com.uppaal.model.lscsystem;

import com.uppaal.model.core2.lsc.Condition;

public class SystemCondition {
   private LscProcess process;
   private int index;
   private String name;
   private Condition condition;

   public SystemCondition(LscProcess process, int index, String name, Condition condition) {
      this.process = process;
      this.index = index;
      this.name = name;
      this.condition = condition;
   }

   public LscProcess getProcess() {
      return this.process;
   }

   public String getProcessName() {
      return this.process.getName();
   }

   public int getIndex() {
      return this.index;
   }

   public String getName() {
      return this.name;
   }

   public Condition getCondition() {
      return this.condition;
   }

   public String getFormatedName() {
      String var10000 = this.process.getName();
      return var10000 + "." + this.index + (this.name != null ? "." + this.name : "");
   }
}
