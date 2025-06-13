package com.uppaal.model.system;

public class SystemState {
   protected SystemLocation[] locations;
   private String traceText = null;

   public SystemState(SystemLocation[] l) {
      this.locations = l;
   }

   public String traceFormat() {
      if (this.traceText == null) {
         StringBuilder s = new StringBuilder("(");
         boolean first = true;
         SystemLocation[] var3 = this.locations;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            SystemLocation location = var3[var5];
            if (first) {
               first = false;
            } else {
               s.append(", ");
            }

            s.append(location.getName());
         }

         s.append(")");
         this.traceText = s.toString();
      }

      return this.traceText;
   }

   public SystemLocation[] getLocations() {
      return this.locations;
   }

   public void setLocations(SystemLocation[] locations) {
      this.locations = locations;
      this.traceText = null;
   }
}
