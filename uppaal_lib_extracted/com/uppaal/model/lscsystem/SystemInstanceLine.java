package com.uppaal.model.lscsystem;

import com.uppaal.model.core2.lsc.InstanceLine;

public class SystemInstanceLine {
   private LscProcess process;
   private InstanceLine instanceLine;
   private int index;

   public SystemInstanceLine(LscProcess process, int index, InstanceLine instanceLine) {
      this.process = process;
      this.instanceLine = instanceLine;
      this.index = index;
   }

   public LscProcess getProcess() {
      return this.process;
   }

   public String getName() {
      return (String)this.instanceLine.getPropertyValue("name");
   }

   public int getIndex() {
      return this.index;
   }

   public InstanceLine getInstanceLine() {
      return this.instanceLine;
   }
}
