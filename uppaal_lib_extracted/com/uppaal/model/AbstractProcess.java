package com.uppaal.model;

import com.uppaal.model.core2.AbstractTemplate;

public class AbstractProcess {
   private final String name;
   private final AbstractTemplate template;
   protected Translator translator;
   private final int index;

   protected AbstractProcess(String name, int index, AbstractTemplate template, Translator translator) {
      this.name = name;
      this.template = template;
      this.translator = translator;
      this.index = index;
   }

   public String getName() {
      return this.name;
   }

   public int getIndex() {
      return this.index;
   }

   public AbstractTemplate getTemplate() {
      return this.template;
   }

   public Translator getTranslator() {
      return this.translator;
   }
}
