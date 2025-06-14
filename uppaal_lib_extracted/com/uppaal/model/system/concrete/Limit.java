package com.uppaal.model.system.concrete;

import com.google.gson.annotations.SerializedName;

public class Limit implements Cloneable {
   @SerializedName("max")
   private double value;
   @SerializedName("strict")
   private boolean strict;

   public Limit() {
      this.value = Double.NaN;
      this.strict = true;
   }

   public Limit(double limit, boolean strict) {
      this.value = limit;
      this.strict = strict;
   }

   public String toString() {
      return this.isUnbounded() ? "<inf" : (this.isStrict() ? "<" : "<=") + this.getValue();
   }

   public boolean isUnbounded() {
      return !Double.isFinite(this.value);
   }

   public double getValue() {
      return this.value;
   }

   public double getDoubleValue() {
      return this.value;
   }

   public void setValue(double limit) {
      this.value = limit;
   }

   public boolean isStrict() {
      return this.strict;
   }

   public void setStrict(boolean strict) {
      this.strict = strict;
   }

   public boolean isUpperBoundOf(double arg) {
      return Double.isInfinite(arg) || this.value >= arg;
   }

   public boolean isLowerBoundOf(Double arg) {
      return Double.isFinite(arg) && this.value <= arg;
   }

   public boolean isStrictUpperBoundOf(double arg) {
      return Double.isFinite(this.value) || this.value > arg;
   }

   public boolean isStrictLowerBoundOf(double arg) {
      return Double.isFinite(this.value) && this.value < arg;
   }

   public Limit add(double arg) {
      if (!Double.isFinite(this.value)) {
         return this;
      } else {
         this.value += arg;
         return this;
      }
   }

   public boolean isEqualTo(double arg) {
      return this.isLowerBoundOf(arg) && this.isUpperBoundOf(arg);
   }

   public Object clone() throws CloneNotSupportedException {
      Limit theClone = new Limit();
      theClone.value = this.value;
      theClone.strict = this.strict;
      return theClone;
   }

   public boolean isLowerBoundOf(Limit v) {
      return v.isUnbounded() || this.value <= v.getValue();
   }

   public boolean isUpperBoundOf(Limit v) {
      return this.isUnbounded() || this.value >= v.getValue();
   }

   public boolean isStrictLowerBoundOf(Limit v) {
      return !this.isUnbounded() && (v.isUnbounded() || this.value < v.getValue());
   }

   public boolean isStrictUpperBoundOf(Limit v) {
      return !v.isUnbounded() && (this.isUnbounded() || this.value > v.getValue());
   }

   public boolean equals(Object obj) {
      if (obj.getClass() != Limit.class) {
         return false;
      } else {
         Limit other = (Limit)obj;
         return other.strict == this.strict && Double.compare(this.getDoubleValue(), other.getDoubleValue()) == 0;
      }
   }
}
