package com.uppaal.model.core2;

class Position {
   private final Element element;
   private int x;
   private int y;

   Position(Element element, int x, int y) {
      this.element = element;
      this.x = x;
      this.y = y;
   }

   void swap() {
      int n = this.element.getX();
      int m = this.element.getY();
      this.element.setProperty("x", this.x);
      this.element.setProperty("y", this.y);
      this.x = n;
      this.y = m;
   }
}
