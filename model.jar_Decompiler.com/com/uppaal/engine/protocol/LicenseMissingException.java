package com.uppaal.engine.protocol;

public class LicenseMissingException extends RuntimeException {
   public LicenseMissingException() {
      super("License not found");
   }
}
