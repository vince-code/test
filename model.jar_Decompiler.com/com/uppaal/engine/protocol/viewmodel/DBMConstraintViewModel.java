package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;

public class DBMConstraintViewModel {
   private int i;
   private int j;
   @SerializedName("b")
   private int constraint;

   public DBMConstraintViewModel(int i, int j, int constraint) {
      this.i = i;
      this.j = j;
      this.constraint = constraint;
   }

   public DBMConstraintViewModel() {
   }

   public int getI() {
      return this.i;
   }

   public int getJ() {
      return this.j;
   }

   public int getConstraint() {
      return this.constraint;
   }
}
