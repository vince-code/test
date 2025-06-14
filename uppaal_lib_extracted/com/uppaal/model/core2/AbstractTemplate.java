package com.uppaal.model.core2;

public abstract class AbstractTemplate extends Node {
   public AbstractTemplate(Element prototype) {
      super(prototype);
   }

   public Object clone() throws CloneNotSupportedException {
      AbstractTemplate template = (AbstractTemplate)super.clone();
      return template;
   }

   public AbstractTemplate getTemplate() {
      return this;
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitTemplate(this);
   }

   public abstract Element getPrototypeFromParent(Element var1);
}
