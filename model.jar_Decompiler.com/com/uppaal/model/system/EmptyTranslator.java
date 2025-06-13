package com.uppaal.model.system;

public class EmptyTranslator implements com.uppaal.model.Translator {
   public String translate(String content) {
      return content;
   }
}
