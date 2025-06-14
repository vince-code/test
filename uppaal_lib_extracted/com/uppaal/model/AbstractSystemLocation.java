package com.uppaal.model;

public abstract class AbstractSystemLocation {
   private final AbstractProcess process;
   private final int index;

   protected AbstractSystemLocation(AbstractProcess process, int index) {
      this.process = process;
      this.index = index;
   }

   public AbstractProcess getProcess() {
      return this.process;
   }

   public String getProcessName() {
      return this.process.getName();
   }

   public abstract String getName();

   public int getIndex() {
      return this.index;
   }
}
