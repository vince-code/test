package com.uppaal.model.core2;

public class SetPropertyCommand extends AbstractCommand {
   protected Element element;
   protected String name;
   protected Object value;
   protected Property property;

   public SetPropertyCommand(Element element, String name, Object value) {
      this.element = element;
      this.name = name;
      this.value = value;
   }

   public void execute() {
      if (this.value == null) {
         this.property = this.getLocalProperty();
         this.element.setProperty(this.name, (Object)null);
      } else {
         this.swap();
      }

   }

   public void undo() {
      if (this.property != null) {
         this.element.restoreProperty(this.name, this.property);
         this.property.setProperty("#errors", (Object)null);
         this.property = null;
      } else {
         this.swap();
      }

   }

   protected Object swap() {
      Object old = this.getLocalPropertyValue();
      Property p = this.element.setProperty(this.name, this.value);
      if (p != null) {
         p.setProperty("#errors", (Object)null);
      }

      this.value = old;
      return old;
   }

   protected Property getLocalProperty() {
      return this.element.isPropertyLocal(this.name) ? this.element.getProperty(this.name) : null;
   }

   protected Object getLocalPropertyValue() {
      return this.element.isPropertyLocal(this.name) ? this.element.getPropertyValue(this.name) : null;
   }

   public Element getModifiedElement() {
      return this.element;
   }
}
