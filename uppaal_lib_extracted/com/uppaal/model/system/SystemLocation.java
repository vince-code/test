package com.uppaal.model.system;

import com.uppaal.model.AbstractSystemLocation;
import com.uppaal.model.core2.Location;

public class SystemLocation extends AbstractSystemLocation {
   private final Location location;

   public SystemLocation(Process process, int index, Location location) {
      super(process, index);
      this.location = location;
   }

   public String getName() {
      return this.location.getName();
   }

   public Location getLocation() {
      return this.location;
   }
}
