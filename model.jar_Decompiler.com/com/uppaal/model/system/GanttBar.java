package com.uppaal.model.system;

import com.uppaal.model.system.concrete.Limit;

public class GanttBar implements Cloneable {
   public int id;
   public Limit fromTime;
   public Limit toTime;
   public int value;
   public int xStart;
   public int xEnd;
   public int yStart;
   public int yEnd;
   public boolean overlaps;

   public GanttBar() {
      this.fromTime = new Limit();
      this.toTime = new Limit();
      this.overlaps = false;
   }

   public GanttBar(Limit from, Limit to, Double globaltime, int val) {
      if (to.isStrictLowerBoundOf(from.getValue())) {
         throw new IllegalArgumentException("Gantt bar cannot have end time before or equal to start time");
      } else {
         this.fromTime = from.add(globaltime);
         this.toTime = to.add(globaltime);
         this.overlaps = false;
         this.xStart = 0;
         this.xEnd = 0;
         this.yStart = 0;
         this.yEnd = 0;
         this.value = val;
      }
   }

   public GanttBar(double from, int val) {
      this.fromTime = new Limit(from, false);
      this.value = val;
      this.toTime = new Limit();
      this.overlaps = false;
      this.xStart = 0;
      this.xEnd = 0;
      this.yStart = 0;
      this.yEnd = 0;
   }

   public String toString() {
      String bracket1 = "(";
      String bracket2 = ")";
      String val1 = "infinity";
      String val2 = "infinity";
      if (this.fromTime.isStrict() && !this.fromTime.isUnbounded()) {
         bracket1 = "[";
      }

      if (this.toTime.isStrict() && !this.toTime.isUnbounded()) {
         bracket2 = "]";
      }

      if (!this.fromTime.isUnbounded()) {
         val1 = Double.toString(this.fromTime.getDoubleValue());
      }

      if (!this.toTime.isUnbounded()) {
         val2 = Double.toString(this.toTime.getDoubleValue());
      }

      return "Value " + this.value + " in interval " + bracket1 + val1 + ", " + val2 + bracket2;
   }

   public boolean insideBar(int x, int y) {
      return y >= this.yStart && y <= this.yEnd && x >= this.xStart && (this.toTime.isUnbounded() || x <= this.xEnd);
   }

   public Object clone() throws CloneNotSupportedException {
      GanttBar theClone = new GanttBar();
      theClone.value = this.value;
      if (this.fromTime == null) {
         theClone.fromTime = null;
      } else {
         theClone.fromTime = (Limit)this.fromTime.clone();
      }

      if (this.toTime == null) {
         theClone.toTime = null;
      } else {
         theClone.toTime = (Limit)this.toTime.clone();
      }

      theClone.xStart = this.xStart;
      theClone.yStart = this.yStart;
      theClone.xEnd = this.xEnd;
      theClone.yEnd = this.yEnd;
      theClone.overlaps = this.overlaps;
      return theClone;
   }
}
