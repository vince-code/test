package com.uppaal.model.core2;

import java.awt.Color;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Data2D implements Iterable<Double> {
   public static final Double emptyMin = new Double(0.0D, 0.0D);
   public static final Double emptyMax = new Double(0.0D, 0.0D);
   String title;
   String type;
   Color color;
   Double min;
   Double max;
   Double logmin;
   Double logmax;
   double dx;
   double logdx;
   boolean logx = false;
   boolean logy = false;
   ArrayList<Double> samples = new ArrayList();

   public Data2D(String title, String type, Color color) {
      this.title = title;
      this.type = type;
      this.color = color;
      this.min = new Double(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY);
      this.max = new Double(java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.NEGATIVE_INFINITY);
      this.logmin = new Double(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY);
      this.logmax = new Double(java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.NEGATIVE_INFINITY);
      this.dx = java.lang.Double.POSITIVE_INFINITY;
      this.logdx = java.lang.Double.POSITIVE_INFINITY;
      int b = type.indexOf(98);
      if (b >= 0 && b < type.length() - 2 && type.charAt(b + 1) == '(') {
         b += 2;
         int till = type.indexOf(41, b);
         if (till > b + 1) {
            try {
               this.dx = java.lang.Double.parseDouble(type.substring(b, till));
            } catch (NumberFormatException var7) {
            }
         }
      }

   }

   private void updateMinMax(Double p) {
      this.min.x = this.min.x < p.x ? this.min.x : p.x;
      this.min.y = this.min.y < p.y ? this.min.y : p.y;
      this.max.x = this.max.x > p.x ? this.max.x : p.x;
      this.max.y = this.max.y > p.y ? this.max.y : p.y;
   }

   private void updateMinMax() {
      this.min = new Double(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY);
      this.max = new Double(java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.NEGATIVE_INFINITY);
      Iterator var1 = this.samples.iterator();

      while(var1.hasNext()) {
         Double p = (Double)var1.next();
         this.updateMinMax(p);
      }

   }

   public void addSample(Double p) {
      this.updateMinMax(p);
      double lx = Math.log10(p.x);
      double ly = Math.log10(p.y);
      if (p.x > 0.0D && lx < this.logmin.x) {
         this.logmin.x = lx;
      }

      if (p.y > 0.0D && ly < this.logmin.y) {
         this.logmin.y = ly;
      }

      if (p.x > 0.0D && lx > this.logmax.x) {
         this.logmax.x = lx;
      }

      if (p.y > 0.0D && ly > this.logmax.y) {
         this.logmax.y = ly;
      }

      if (!this.samples.isEmpty()) {
         Double p2 = (Double)this.samples.get(this.samples.size() - 1);
         double d = Math.abs(p2.x - p.x);
         if (d > 0.0D && d < this.dx) {
            this.dx = d;
         }

         d = Math.abs(Math.log10(p2.x) - Math.log10(p.x));
         if (d > 0.0D && d < this.logdx) {
            this.logdx = d;
         }
      }

      this.samples.add(p);
   }

   public void addSample(double x, double y) {
      this.addSample(new Double(x, y));
   }

   public Color getColor() {
      return this.color;
   }

   public String getTitle() {
      return this.title;
   }

   public String getType() {
      return this.type;
   }

   public void setColor(Color newcolor) {
      this.color = newcolor;
   }

   public void setTitle(String newtitle) {
      this.title = newtitle;
   }

   public void setType(String newtype) {
      this.type = newtype;
   }

   public void setLogX(boolean logx) {
      this.logx = logx;
   }

   public void setLogY(boolean logy) {
      this.logy = logy;
   }

   public double getXMinDifference() {
      return this.logx ? this.logdx : this.dx;
   }

   public Double getNearestSampleAt(double x) {
      if (this.samples.isEmpty()) {
         return null;
      } else {
         if (this.logx) {
            x = Math.pow(10.0D, x);
         }

         int i = (int)Math.floor((x - this.min.x) / this.dx);
         if (i >= this.samples.size()) {
            i = this.samples.size() - 1;
         }

         if (i < 0) {
            i = 0;
         }

         Double p;
         for(p = (Double)this.samples.get(i); x < p.x && i > 0; p = (Double)this.samples.get(i)) {
            --i;
         }

         while(x > p.x + this.dx && i < this.samples.size() - 1) {
            ++i;
            p = (Double)this.samples.get(i);
         }

         return p;
      }
   }

   public Iterator<Double> iterator() {
      return (Iterator)(!this.logx && !this.logy ? this.samples.iterator() : new LogIterator(this.samples.iterator(), this.logx, this.logy));
   }

   public boolean isEmpty() {
      return this.samples.isEmpty();
   }

   public Double getMinimum() {
      return this.samples.isEmpty() ? new Double(emptyMin.x, emptyMin.y) : new Double(this.logx ? this.logmin.x : this.min.x, this.logy ? this.logmin.y : this.min.y);
   }

   public Double getMaximum() {
      return this.samples.isEmpty() ? new Double(emptyMax.x, emptyMax.y) : new Double(this.logx ? this.logmax.x : this.max.x, this.logy ? this.logmax.y : this.max.y);
   }

   public void removeAbove(double x) {
      Double closestBeforePoint = new Double(java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.NEGATIVE_INFINITY);
      Double closestAfterPoint = new Double(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY);
      Iterator var7 = this.samples.iterator();

      while(var7.hasNext()) {
         Double p = (Double)var7.next();
         if (p.x >= x && p.x < closestAfterPoint.x) {
            closestAfterPoint = p;
         }

         if (p.x < x && p.x >= closestBeforePoint.x) {
            closestBeforePoint = p;
         }
      }

      this.samples.removeIf((point) -> {
         return point.getX() >= x;
      });
      if (closestBeforePoint != closestBeforePoint && closestAfterPoint != closestAfterPoint) {
         double gradient = (closestAfterPoint.y - closestBeforePoint.y) / (closestAfterPoint.x - closestBeforePoint.x);
         double distance = x - closestBeforePoint.x;
         Double newPoint = new Double(x, closestBeforePoint.y + distance * gradient);
         this.addSample(newPoint);
      }

      this.updateMinMax();
   }

   public int size() {
      return this.samples.size();
   }

   public double getArg() {
      try {
         Pattern p = Pattern.compile("\\((\\d+\\.?\\d*)\\)");
         Matcher m = p.matcher(this.type);
         m.find();
         return java.lang.Double.parseDouble(m.group(1));
      } catch (IllegalStateException | NumberFormatException var3) {
         return -1.0D;
      }
   }

   public static enum AreaStyle {
      Bars,
      Polygon,
      None;
   }

   public static enum LineStyle {
      Solid,
      Dashed,
      Dotted,
      DashDotted,
      None;
   }

   public static enum PointStyle {
      Dot,
      Diamond,
      TriangleUp,
      TriangleDown,
      TriangleLeft,
      TriangleRight,
      Rectangle,
      Circle,
      None;
   }
}
