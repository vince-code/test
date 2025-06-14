package com.uppaal.model.core2;

import java.awt.Color;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Iterator;

public class DataSet2D implements Iterable<Data2D> {
   public static final Double emptyMin = new Double(0.0D, 0.0D);
   public static final Double emptyMax = new Double(0.0D, 1.0D);
   public String title;
   public String xlabel;
   public String ylabel;
   boolean logx = false;
   boolean logy = false;
   ArrayList<Data2D> data = new ArrayList();
   ArrayList<String> comments = new ArrayList();
   Data2D last = null;
   static Color[] predefinedColors = new Color[]{new Color(0, 0, 0), new Color(230, 25, 75), new Color(60, 180, 75), new Color(255, 225, 25), new Color(0, 130, 200), new Color(245, 130, 48), new Color(145, 30, 180), new Color(70, 240, 240), new Color(240, 50, 230), new Color(210, 245, 60), new Color(250, 190, 212), new Color(0, 128, 128), new Color(220, 190, 255), new Color(170, 110, 40), new Color(255, 250, 200), new Color(128, 0, 0), new Color(170, 255, 195), new Color(128, 128, 0), new Color(255, 215, 180), new Color(0, 0, 128), new Color(128, 128, 128), new Color(255, 255, 255)};

   public DataSet2D(String title, String xlabel, String ylabel) {
      this.title = title;
      this.xlabel = xlabel;
      this.ylabel = ylabel;
   }

   public void addData(String legend, String type, Color color) {
      this.last = new Data2D(legend, type, color);
      this.data.add(this.last);
   }

   public void addData(String legend, String type) {
      Color color;
      if (this.data.size() < predefinedColors.length) {
         color = predefinedColors[this.data.size()];
      } else {
         color = predefinedColors[0];
      }

      this.last = new Data2D(legend, type, color);
      this.data.add(this.last);
   }

   public void setTitle(String newtitle) {
      this.title = newtitle;
   }

   public void setXLabel(String newx) {
      this.xlabel = newx;
   }

   public void setYLabel(String newy) {
      this.ylabel = newy;
   }

   public void setLogX(boolean logx) {
      this.logx = logx;
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Data2D d = (Data2D)var2.next();
         d.setLogX(logx);
      }

   }

   public void setLogY(boolean logy) {
      this.logy = logy;
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Data2D d = (Data2D)var2.next();
         d.setLogY(logy);
      }

   }

   public void addData2D(Data2D data2d) {
      data2d.setLogX(this.logx);
      data2d.setLogY(this.logy);
      this.data.add(data2d);
   }

   public void removeData2D(Data2D data2d) {
      this.data.remove(data2d);
   }

   public boolean isEmpty() {
      return this.data.isEmpty();
   }

   public int size() {
      return this.data.size();
   }

   public void addSample(Double p) {
      this.last.addSample(p);
   }

   public void addSample(double x, double y) {
      this.addSample(new Double(x, y));
   }

   public void addComment(String comment) {
      this.comments.add(comment);
   }

   public ArrayList<String> getComments() {
      return this.comments;
   }

   public String getTitle() {
      return this.title;
   }

   public String getXLabel() {
      return this.xlabel;
   }

   public String getYLabel() {
      return this.ylabel;
   }

   public Double getMinimum() {
      if (this.data.isEmpty()) {
         return new Double(emptyMin.x, emptyMin.y);
      } else {
         Double min = new Double(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY);

         Double p;
         for(Iterator var2 = this.iterator(); var2.hasNext(); min.y = min.y < p.y ? min.y : p.y) {
            Data2D d = (Data2D)var2.next();
            p = d.getMinimum();
            min.x = min.x < p.x ? min.x : p.x;
         }

         return min;
      }
   }

   public Double getMaximum() {
      if (this.data.isEmpty()) {
         return new Double(emptyMax.x, emptyMax.y);
      } else {
         Double max = new Double(java.lang.Double.NEGATIVE_INFINITY, java.lang.Double.NEGATIVE_INFINITY);

         Double p;
         for(Iterator var2 = this.iterator(); var2.hasNext(); max.y = max.y > p.y ? max.y : p.y) {
            Data2D d = (Data2D)var2.next();
            p = d.getMaximum();
            max.x = max.x > p.x ? max.x : p.x;
         }

         return max;
      }
   }

   public double getMaxXDifference() {
      if (this.data.isEmpty()) {
         return emptyMax.x;
      } else {
         double max = emptyMax.x;

         double d;
         for(Iterator var3 = this.iterator(); var3.hasNext(); max = max > d ? max : d) {
            Data2D data2D = (Data2D)var3.next();
            d = data2D.getXMinDifference();
         }

         return max;
      }
   }

   public Data2D getData(int index) {
      return (Data2D)this.data.get(index);
   }

   public Iterator<Data2D> iterator() {
      return this.data.iterator();
   }

   public void removeAbove(double x) {
      Iterator var3 = this.data.iterator();

      while(var3.hasNext()) {
         Data2D set = (Data2D)var3.next();
         set.removeAbove(x);
      }

   }
}
