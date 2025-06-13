package com.uppaal.model.lscsystem;

import com.uppaal.model.core2.lsc.Prechart;

public class SystemPrechart {
   private final LscProcess process;
   private final Prechart prechart;

   public SystemPrechart(LscProcess process, Prechart prechart) {
      this.process = process;
      this.prechart = prechart;
   }

   public LscProcess getProcess() {
      return this.process;
   }

   public Prechart getPrechart() {
      return this.prechart;
   }
}
