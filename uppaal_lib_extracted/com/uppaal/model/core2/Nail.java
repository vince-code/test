package com.uppaal.model.core2;

public class Nail extends Node {
   Nail(Element prototype) {
      super(prototype);
   }

   public Nail setX(int x) {
      this.setProperty("x", x);
      return this;
   }

   public Nail setY(int y) {
      this.setProperty("y", y);
      return this;
   }

   public Nail setXY(int x, int y) {
      this.setX(x);
      this.setY(y);
      return this;
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitNail(this);
   }

   public Element getPrototypeFromParent(Element parent) {
      return (Element)parent.getPropertyValue("#nail");
   }

   public String getFriendlyName() {
      return "nail";
   }
}
