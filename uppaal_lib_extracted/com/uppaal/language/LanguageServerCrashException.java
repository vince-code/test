package com.uppaal.language;

public class LanguageServerCrashException extends RuntimeException {
   public LanguageServerCrashException(String message) {
      super("Language server exception: " + message + "<br>If errors persist, you can disable autocomplete in preferences");
   }
}
