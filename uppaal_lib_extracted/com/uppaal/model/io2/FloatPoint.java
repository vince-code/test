package com.uppaal.model.io2;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

public class FloatPoint extends Float {
   public FloatPoint(float x, float y) {
      super(x, y);
   }

   public FloatPoint(Point2D point) {
      super((float)point.getX(), (float)point.getY());
   }

   public FloatPoint subtract(float subX, float subY) {
      return new FloatPoint(this.x - subX, this.y - subY);
   }

   public FloatPoint subtract(FloatPoint sub) {
      return new FloatPoint(this.x - sub.x, this.y - sub.y);
   }

   public FloatPoint add(int addX, int addY) {
      return new FloatPoint(this.x + (float)addX, this.y + (float)addY);
   }

   public FloatPoint add(FloatPoint add) {
      return new FloatPoint(this.x + add.x, this.y + add.y);
   }

   public FloatPoint multiply(float mult) {
      return new FloatPoint(this.x * mult, this.y * mult);
   }

   public IntPoint getIntPoint() {
      return new IntPoint(Math.round(this.x), Math.round(this.y));
   }

   public float length() {
      double sq = (double)(this.x * this.x + this.y * this.y);
      return (float)Math.sqrt(sq);
   }

   public FloatPoint unitVector() {
      float len = this.length();
      return new FloatPoint(this.x / len, this.y / len);
   }

   public FloatPoint rotate(double radAngle) {
      float len = this.length();
      double newAngle = Math.atan2((double)this.y, (double)this.x) + radAngle;
      return new FloatPoint(len * (float)Math.cos(newAngle), len * (float)Math.sin(newAngle));
   }

   public String toString() {
      String msg = this.getClass().getName();
      msg = msg + "[x=" + this.x;
      msg = msg + ",y=" + this.y;
      msg = msg + "]";
      return msg;
   }
}
