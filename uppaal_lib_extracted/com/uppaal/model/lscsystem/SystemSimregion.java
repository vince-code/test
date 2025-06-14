package com.uppaal.model.lscsystem;

import com.uppaal.model.AbstractSystemLocation;
import com.uppaal.model.core2.lsc.Simregion;

public class SystemSimregion extends AbstractSystemLocation {
   private Simregion simregion;

   SystemSimregion(LscProcess process, int index, Simregion simregion) {
      super(process, index);
      this.simregion = simregion;
   }

   public String getName() {
      return (String)this.simregion.getPropertyValue("name");
   }

   public Simregion getSimregion() {
      return this.simregion;
   }
}
