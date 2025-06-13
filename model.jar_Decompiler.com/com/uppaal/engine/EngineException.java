package com.uppaal.engine;

public class EngineException extends Exception {
   public EngineException() {
   }

   public EngineException(String s) {
      super(s);
   }

   public EngineException(String s, Throwable cause) {
      super(cause.getMessage() + ": " + s, cause);
   }

   public EngineException(Throwable cause) {
      super(cause.getMessage(), cause);
   }

   public String toString() {
      return this.getMessage() == null ? "Engine exception" : this.getMessage();
   }
}
