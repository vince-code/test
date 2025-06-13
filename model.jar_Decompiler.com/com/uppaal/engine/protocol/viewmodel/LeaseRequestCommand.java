package com.uppaal.engine.protocol.viewmodel;

public class LeaseRequestCommand {
   final String license_key;
   final String duration;

   public LeaseRequestCommand(String license_key, String duration) {
      this.license_key = license_key;
      this.duration = duration;
   }
}
