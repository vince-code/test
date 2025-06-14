package com.uppaal.model.io2;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.io.IOException;
import java.io.Writer;

public class IntPoint extends Point {
   public IntPoint(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public IntPoint(Point point) {
      this(point.x, point.y);
   }

   public IntPoint(Point2D point) {
      this((int)Math.round(point.getX()), (int)Math.round(point.getY()));
   }

   public IntPoint copy() {
      return new IntPoint(this);
   }

   public IntPoint subtract(int subX, int subY) {
      return new IntPoint(this.x - subX, this.y - subY);
   }

   public IntPoint subtract(Point2D sub) {
      return new IntPoint((int)((double)this.x - sub.getX()), (int)((double)this.y - sub.getY()));
   }

   public IntPoint add(int addX, int addY) {
      return new IntPoint(this.x + addX, this.y + addY);
   }

   public IntPoint add(Point add) {
      return new IntPoint(this.x + add.x, this.y + add.y);
   }

   public IntPoint multiply(int mult) {
      return new IntPoint(this.x * mult, this.y * mult);
   }

   public IntPoint multiply(float mult) {
      return new IntPoint(Math.round((float)this.x * mult), Math.round((float)this.y * mult));
   }

   public IntPoint divide(float f) {
      return new IntPoint(Math.round((float)this.x / f), Math.round((float)this.y / f));
   }

   public float length() {
      double sq = (double)(this.x * this.x + this.y * this.y);
      return (float)Math.sqrt(sq);
   }

   public Point2D unitVector() {
      float len = this.length();
      return new Float((float)this.x / len, (float)this.y / len);
   }

   public void unparse(Writer out) throws IOException {
      out.write("(");
      out.write(String.valueOf(this.x));
      out.write(", ");
      out.write(String.valueOf(this.y));
      out.write(")");
   }

   public String toString() {
      String msg = this.getClass().getName();
      msg = msg + "[x=" + this.x;
      msg = msg + ",y=" + this.y;
      msg = msg + "]";
      return msg;
   }

   public boolean equals(Object other) {
      return other instanceof IntPoint && this.x == ((IntPoint)other).x && this.y == ((IntPoint)other).y;
   }
}
