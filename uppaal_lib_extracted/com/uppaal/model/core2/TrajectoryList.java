package com.uppaal.model.core2;

import java.util.Iterator;

public class TrajectoryList extends Node implements Iterable<Trajectory> {
   public TrajectoryList() {
      this((Element)null);
   }

   public TrajectoryList(Element prototype) {
      super(prototype);
   }

   public void addLast(Trajectory trajectory) {
      this.insert(trajectory, this.getLast());
   }

   public void insert(Trajectory h, int index) {
      Trajectory old = this.get(index);
      if (old == null) {
         this.insert(h, this.getLast());
      } else {
         this.insert(h, old.getPrevious());
      }

   }

   public Trajectory get(int index) {
      Trajectory trajectory = (Trajectory)this.getFirst();

      for(int i = 0; i < index; ++i) {
         trajectory = (Trajectory)trajectory.getNext();
         if (trajectory == null) {
            break;
         }
      }

      return trajectory;
   }

   public String getFriendlyName() {
      return "trajectories";
   }

   public Iterator<Trajectory> iterator() {
      return new Iterator<Trajectory>() {
         private Node next = TrajectoryList.this.getFirst();

         public boolean hasNext() {
            return this.next != null;
         }

         public Trajectory next() {
            Node n = this.next;
            this.next = this.next.getNext();
            return (Trajectory)n;
         }

         public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
         }
      };
   }
}
