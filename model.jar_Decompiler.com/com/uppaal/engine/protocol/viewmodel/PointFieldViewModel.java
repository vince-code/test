package com.uppaal.engine.protocol.viewmodel;

public class PointFieldViewModel {
   private double x;
   private double y;

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public String toString() {
      return String.format("x:%f, y:%f", this.x, this.y);
   }
}
