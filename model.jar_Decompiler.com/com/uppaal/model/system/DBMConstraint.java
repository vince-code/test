package com.uppaal.model.system;

public class DBMConstraint {
   static int MAXBOUND = Integer.MAX_VALUE;
   static int MINBOUND = Integer.MIN_VALUE;
   public int i;
   public int j;
   public int bound;

   public DBMConstraint(int a, int b, int c) {
      this.i = a;
      this.j = b;
      this.bound = c;
   }

   public boolean isStrict() {
      return Polyhedron.isStrict(this.bound);
   }

   public int getValue() {
      return Polyhedron.getValue(this.bound);
   }

   public boolean isValid() {
      return this.bound < MAXBOUND && this.bound > MINBOUND;
   }

   public boolean equals(Object o) {
      if (o == null) {
         return false;
      } else {
         DBMConstraint c = (DBMConstraint)o;
         return this.i == c.i && this.j == c.j && this.bound == c.bound;
      }
   }
}
