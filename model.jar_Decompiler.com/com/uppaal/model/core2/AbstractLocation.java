package com.uppaal.model.core2;

public abstract class AbstractLocation extends Node {
   public AbstractLocation(Element prototype) {
      super(prototype);
   }

   public AbstractLocation setX(int x) {
      this.setProperty("x", x);
      return this;
   }

   public AbstractLocation setY(int y) {
      this.setProperty("y", y);
      return this;
   }

   public AbstractLocation setXY(int x, int y) {
      this.setX(x);
      this.setY(y);
      return this;
   }

   public abstract String getName();
}
