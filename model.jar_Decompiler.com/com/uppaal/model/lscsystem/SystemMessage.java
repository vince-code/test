package com.uppaal.model.lscsystem;

import com.uppaal.model.core2.lsc.Message;

public class SystemMessage {
   private LscProcess process;
   private int index;
   private String name;
   private Message message;

   public SystemMessage(LscProcess process, int index, String name, Message message) {
      this.process = process;
      this.index = index;
      this.name = name;
      this.message = message;
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

   public Message getMessage() {
      return this.message;
   }
}
