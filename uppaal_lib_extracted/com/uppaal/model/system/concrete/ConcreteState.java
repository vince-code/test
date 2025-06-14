package com.uppaal.model.system.concrete;

import com.uppaal.model.system.SystemLocation;
import com.uppaal.model.system.SystemState;

public class ConcreteState extends SystemState {
   private double time;
   private Limit invariant;
   private final int[] vars;
   private final double[] fpvars;
   private final double[] clocks;

   public ConcreteState(Limit invariant, SystemLocation[] locations, int[] vars, double[] fpvars, double[] clocks) {
      super(locations);
      this.invariant = invariant;
      this.vars = vars;
      this.fpvars = fpvars;
      this.clocks = clocks;
      if (clocks.length > 1) {
         this.time = clocks[1];
      }

   }

   public Limit getInvariant() {
      return this.invariant;
   }

   public void setInvariant(Limit invariant) {
      this.invariant = invariant;
   }

   public int[] getVars() {
      return this.vars;
   }

   public double[] getFPVars() {
      return this.fpvars;
   }

   public double[] getClocks() {
      return this.clocks;
   }

   public double getTime() {
      return this.time;
   }

   public void setTime(double t) {
      this.time = t;
   }

   private boolean cmpArrays(double[] arrOne, double[] arrTwo) {
      if (arrOne.length != arrTwo.length) {
         return false;
      } else {
         for(int i = 0; i < arrOne.length; ++i) {
            if (arrOne[i] != arrTwo[i]) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean cmpArrays(int[] arrOne, int[] arrTwo) {
      if (arrOne.length != arrTwo.length) {
         return false;
      } else {
         for(int i = 0; i < arrOne.length; ++i) {
            if (arrOne[i] != arrTwo[i]) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean equals(Object obj) {
      if (obj.getClass() != ConcreteState.class) {
         return false;
      } else {
         ConcreteState other = (ConcreteState)obj;
         if (!this.cmpArrays(this.vars, other.getVars())) {
            return false;
         } else if (!this.cmpArrays(this.fpvars, other.getFPVars())) {
            return false;
         } else {
            return this.cmpArrays(this.clocks, other.getClocks());
         }
      }
   }
}
