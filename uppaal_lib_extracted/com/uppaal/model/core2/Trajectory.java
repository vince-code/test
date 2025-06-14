package com.uppaal.model.core2;

import java.util.Iterator;

public class Trajectory extends Node implements Iterable<Series> {
   private static final String PROP_TITLE = "title";

   public Trajectory(String title) {
      super((Element)null);
      this.setProperty("title", title);
   }

   public String getTitle() {
      return (String)this.getPropertyValue("title");
   }

   public void addLast(Series series) {
      this.insert(series, this.getLast());
   }

   public Iterator<Series> iterator() {
      return new Iterator<Series>() {
         private Node next = Trajectory.this.getFirst();

         public boolean hasNext() {
            return this.next != null;
         }

         public Series next() {
            Node n = this.next;
            this.next = this.next.getNext();
            return (Series)n;
         }

         public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
         }
      };
   }

   public String getFriendlyName() {
      return "trajectory";
   }
}
