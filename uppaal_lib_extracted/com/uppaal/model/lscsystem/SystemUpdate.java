package com.uppaal.model.lscsystem;

import com.uppaal.model.core2.lsc.Update;

public class SystemUpdate {
   private LscProcess process;
   private int index;
   private String name;
   private Update update;

   public SystemUpdate(LscProcess process, int index, String name, Update update) {
      this.process = process;
      this.index = index;
      this.name = name;
      this.update = update;
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

   public String getFormatedName() {
      String var10000 = this.process.getName();
      return var10000 + "." + this.index + (this.name != null ? "." + this.name : "");
   }

   public Update getUpdate() {
      return this.update;
   }
}
