package com.uppaal.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RangeSet {
   private final List<Range> ranges;

   public RangeSet(List<Range> ranges) {
      this.ranges = ranges;
      double lastEnd = -1.0D;

      Range r;
      for(Iterator var4 = ranges.iterator(); var4.hasNext(); lastEnd = r.end) {
         r = (Range)var4.next();

         assert r.start > lastEnd : "RangeSet assumes ranges are increasing";
      }

   }

   public RangeSet(Range range) {
      this.ranges = new ArrayList();
      this.ranges.add(range);
   }

   public List<Range> getRanges() {
      return this.ranges;
   }

   public Range getContainingRange(double d) {
      Iterator var3 = this.ranges.iterator();

      Range r;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         r = (Range)var3.next();
      } while(!(r.start <= d) || !(d <= r.end));

      return r;
   }

   public boolean contains(double d) {
      return this.getContainingRange(d) != null;
   }

   public Range getClosestRange(double d) {
      assert this.ranges.size() > 0;

      double shortestDistance = Double.POSITIVE_INFINITY;
      Range closestRange = (Range)this.ranges.get(0);
      Iterator var6 = this.ranges.iterator();

      while(var6.hasNext()) {
         Range r = (Range)var6.next();
         double distanceStart = Math.abs(d - r.start);
         if (distanceStart < shortestDistance) {
            shortestDistance = distanceStart;
            closestRange = r;
         }

         double distanceEnd = Math.abs(d - r.end);
         if (distanceEnd < shortestDistance) {
            shortestDistance = distanceEnd;
            closestRange = r;
         }
      }

      return closestRange;
   }

   public double getStart() {
      assert this.ranges.size() > 0;

      return ((Range)this.ranges.get(0)).start;
   }

   public double getEnd() {
      assert this.ranges.size() > 0;

      return ((Range)this.ranges.get(this.ranges.size() - 1)).end;
   }
}
