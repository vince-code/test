package com.uppaal.model.core2;

public class Location extends AbstractLocation {
   public Location(Element prototype) {
      super(prototype);
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitLocation(this);
   }

   public Element getPrototypeFromParent(Element parent) {
      return (Element)parent.getPropertyValue("#location");
   }

   public String getFriendlyName() {
      return this.getName();
   }

   public String getName() {
      String name = (String)this.getPropertyValue("name");
      if (name != null && !name.isBlank()) {
         return name;
      } else if (this.hasFlag("committed")) {
         return "Ⓒ";
      } else if (this.hasFlag("urgent")) {
         return "Ⓤ";
      } else {
         return this.hasFlag("init") ? "ⓞ" : "❍";
      }
   }
}
