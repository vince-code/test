package com.uppaal.model.system;

import com.uppaal.model.system.concrete.Limit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GanttChart implements Cloneable {
   private final List<GanttRow> rows = new ArrayList();

   public void addRow(GanttRow row) {
      this.rows.add(row);
   }

   public int noOfRows() {
      return this.rows.size();
   }

   public GanttRow getRow(int i) {
      return (GanttRow)this.rows.get(i);
   }

   public void truncate(double t) {
      Iterator i = this.rows.iterator();

      while(i.hasNext()) {
         GanttRow r = (GanttRow)i.next();
         r.truncate(t);
      }

   }

   public void clearChart() {
      Iterator i = this.rows.iterator();

      while(i.hasNext()) {
         ((GanttRow)i.next()).clearRow();
      }

   }

   public Limit maximalNonInfiniteEndTime() {
      Limit tmp = new Limit(0.0D, true);
      boolean flagInfinite = true;
      Iterator i = this.rows.iterator();

      while(i.hasNext()) {
         GanttRow r = (GanttRow)i.next();
         Limit rowEndTime = r.maximalNonInfiniteEndTime();
         if (!rowEndTime.isUnbounded() && rowEndTime.isUpperBoundOf(tmp.getValue())) {
            tmp = rowEndTime;
            flagInfinite = false;
         }
      }

      if (flagInfinite) {
         tmp.setValue(Double.POSITIVE_INFINITY);
      }

      return tmp;
   }

   public void printGanttInfo() {
      System.out.println();
      System.out.println("Gantt bar dump");
      System.out.println("Number of rows: " + this.rows.size());
      System.out.println();

      for(int i = 0; i < this.rows.size(); ++i) {
         System.out.print("Row: " + i + " ");
         ((GanttRow)this.rows.get(i)).printRowInfo();
      }

   }

   public Object clone() throws CloneNotSupportedException {
      GanttChart theClone = new GanttChart();
      Iterator var2 = this.rows.iterator();

      while(var2.hasNext()) {
         GanttRow row = (GanttRow)var2.next();
         theClone.rows.add((GanttRow)row.clone());
      }

      return theClone;
   }

   public void add(double globalTime, List<GanttAddition> additions) {
      this.truncate(globalTime);
      Iterator var4 = additions.iterator();

      while(var4.hasNext()) {
         GanttAddition addition = (GanttAddition)var4.next();
         GanttRow row = this.getRow(addition.getRowId());
         GanttBar bar = new GanttBar(globalTime, addition.getValue());
         row.addBar(bar);
      }

   }
}
