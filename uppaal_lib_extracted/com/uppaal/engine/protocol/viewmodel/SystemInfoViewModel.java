package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;

public class SystemInfoViewModel {
   @SerializedName("virt")
   public long virtTotal;
   @SerializedName("phys")
   public long physTotal;
   @SerializedName("swap")
   public long swapTotal;
}
