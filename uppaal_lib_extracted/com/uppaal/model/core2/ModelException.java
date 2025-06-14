package com.uppaal.model.core2;

public class ModelException extends Exception {
   public ModelException() {
   }

   public ModelException(String s) {
      super(s);
   }

   public ModelException(String s, Throwable cause) {
      super(cause.getMessage() + ": " + s, cause);
   }

   public ModelException(Throwable cause) {
      super(cause.getMessage(), cause);
   }

   public String toString() {
      return this.getMessage() == null ? "Model exception" : this.getMessage();
   }
}
