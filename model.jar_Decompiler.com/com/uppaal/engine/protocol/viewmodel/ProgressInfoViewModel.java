package com.uppaal.engine.protocol.viewmodel;

public class ProgressInfoViewModel {
   int load;
   long vm;
   long rss;
   long cache;
   long avail;
   long swap;
   long swapfree;
   long user;
   long sys;
   long timestamp;

   public int getLoad() {
      return this.load;
   }

   public long getVm() {
      return this.vm;
   }

   public long getRss() {
      return this.rss;
   }

   public long getCache() {
      return this.cache;
   }

   public long getAvail() {
      return this.avail;
   }

   public long getSwap() {
      return this.swap;
   }

   public long getSwapfree() {
      return this.swapfree;
   }

   public long getUser() {
      return this.user;
   }

   public long getSys() {
      return this.sys;
   }

   public long getTimestamp() {
      return this.timestamp;
   }
}
