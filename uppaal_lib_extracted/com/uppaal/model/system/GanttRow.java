package com.uppaal.model.system;

import com.uppaal.model.system.concrete.Limit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GanttRow implements Cloneable {
   public transient List<GanttBar> bars = new ArrayList();
   public String label;

   public GanttRow() {
   }

   public GanttRow(String l) {
      this.label = l;
   }

   public void clearRow() {
      this.bars.clear();
   }

   public void addBar(GanttBar bar) {
      assert bar != null;

      if (!bar.toTime.isEqualTo(bar.fromTime.getValue())) {
         if (bar.fromTime.isUnbounded()) {
            throw new RuntimeException("Gantt: Unbounded from value in bar");
         } else {
            if (this.bars.size() > 0) {
               GanttBar lastBar = (GanttBar)this.bars.get(this.bars.size() - 1);
               if (lastBar.value == bar.value && lastBar.fromTime.isLowerBoundOf(bar.fromTime) && lastBar.toTime.isUpperBoundOf(bar.fromTime)) {
                  lastBar.toTime = bar.toTime;
                  return;
               }

               for(int i = 0; i < this.bars.size(); ++i) {
                  GanttBar tmpBar = (GanttBar)this.bars.get(i);
                  if (bar.fromTime.isStrictLowerBoundOf(tmpBar.toTime) && bar.toTime.isStrictUpperBoundOf(tmpBar.fromTime)) {
                     bar.overlaps = true;
                  }
               }
            }

            this.bars.add(bar);
         }
      }
   }

   public int noOfBars() {
      return this.bars.size();
   }

   public GanttBar getFromNum(int i) {
      return (GanttBar)this.bars.get(i);
   }

   public void printRowInfo() {
      System.out.println("No of bars " + this.bars.size());
   }

   public void truncate(double t) {
      Iterator i = this.bars.iterator();

      while(i.hasNext()) {
         GanttBar bar = (GanttBar)i.next();
         if (bar.toTime.isUnbounded()) {
            bar.toTime.setValue(t);
            bar.toTime.setStrict(true);
         }

         if (bar.fromTime.isUpperBoundOf(t)) {
            i.remove();
         } else if (bar.toTime.isUpperBoundOf(t)) {
            bar.toTime.setValue(t);
            bar.toTime.setStrict(true);
         }
      }

   }

   public Limit maximalNonInfiniteEndTime() {
      Limit tmp = new Limit(0.0D, true);
      boolean flagInfinite = true;
      Iterator i = this.bars.iterator();

      while(i.hasNext()) {
         GanttBar bar = (GanttBar)i.next();
         Limit barEndTime = bar.toTime;
         if (!barEndTime.isUnbounded() && barEndTime.isUpperBoundOf(tmp.getValue())) {
            tmp = barEndTime;
            flagInfinite = false;
         }
      }

      if (flagInfinite) {
         tmp.setValue(Double.POSITIVE_INFINITY);
      }

      return tmp;
   }

   public Object clone() throws CloneNotSupportedException {
      GanttRow theClone = new GanttRow(this.label);
      Iterator var2 = this.bars.iterator();

      while(var2.hasNext()) {
         GanttBar bar = (GanttBar)var2.next();
         theClone.bars.add((GanttBar)bar.clone());
      }

      return theClone;
   }
}
