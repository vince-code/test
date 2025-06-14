package com.uppaal.model.core2;

public class DocumentParseException extends RuntimeException {
   public DocumentParseException() {
   }

   public DocumentParseException(String message) {
      super(message);
   }

   public DocumentParseException(String message, Throwable cause) {
      super(message, cause);
   }
}
