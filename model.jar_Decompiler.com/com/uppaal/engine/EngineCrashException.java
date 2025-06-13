package com.uppaal.engine;

public class EngineCrashException extends RuntimeException {
   public EngineCrashException(String message) {
      super("Engine exception: " + message);
   }
}
