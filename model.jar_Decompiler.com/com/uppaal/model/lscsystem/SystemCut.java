package com.uppaal.model.lscsystem;

import com.uppaal.model.AbstractSystemState;
import com.uppaal.model.system.Polyhedron;

public class SystemCut extends AbstractSystemState {
   private SystemSimregion[] simregions;

   public SystemCut(SystemSimregion[] s, int[] v, Polyhedron z) {
      super(v, z);
      this.simregions = s;
   }

   public SystemSimregion[] getLocationVector() {
      return this.simregions;
   }
}
