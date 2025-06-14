package com.uppaal.model.core2;

public abstract class AbstractCommand implements Command {
   protected int version;

   public void setVersion(int version) {
      this.version = version;
   }

   public int getVersion() {
      return this.version;
   }

   public boolean merge(Command next) {
      return false;
   }
}
