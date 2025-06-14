package com.uppaal.model.core2;

public class SetPropertyWithPositionCommand extends SetPropertyCommand {
   protected int x;
   protected int y;

   public SetPropertyWithPositionCommand(Element element, String property, Object value, int x, int y) {
      super(element, property, value);
      this.x = x;
      this.y = y;
   }

   public void execute() {
      if (this.value == null) {
         this.property = this.getLocalProperty();
         if (this.property != null) {
            this.property.setProperty("#errors", (Object)null);
         }

         this.element.setProperty(this.name, (Object)null);
      } else if (this.swap() == null) {
         Property p = this.element.getProperty(this.name);
         p.setProperty("x", this.x);
         p.setProperty("y", this.y);
         if (this.name.equals("message") && (Float)p.getPropertyValue("f") == 10.0F) {
            p.setProperty("f", 0.5F);
         }
      }

   }
}
