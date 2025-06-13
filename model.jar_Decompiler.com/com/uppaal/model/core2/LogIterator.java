package com.uppaal.model.core2;

import java.awt.geom.Point2D.Double;
import java.util.Iterator;

public class LogIterator implements Iterator<Double> {
   Double p = new Double();
   Iterator<Double> it;
   boolean logx;
   boolean logy;

   public LogIterator(Iterator<Double> it, boolean logx, boolean logy) {
      this.it = it;
      this.logx = logx;
      this.logy = logy;
   }

   public boolean hasNext() {
      return this.it.hasNext();
   }

   public Double next() {
      Double t = (Double)this.it.next();
      if (this.logx) {
         this.p.x = Math.log10(t.x);
      } else {
         this.p.x = t.x;
      }

      if (this.logy) {
         this.p.y = Math.log10(t.y);
      } else {
         this.p.y = t.y;
      }

      return this.p;
   }

   public void remove() {
      this.it.remove();
   }
}
