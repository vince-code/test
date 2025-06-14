package com.uppaal.model.io2;

public class EdgeId {
   private String source;
   private String target;
   private int idNo;
   private int hashCode;

   public EdgeId(String source, String target, int idNo) {
      this.source = source;
      this.target = target;
      this.idNo = idNo;
   }

   public String getSource() {
      return this.source;
   }

   public String getTarget() {
      return this.target;
   }

   public int getIdNo() {
      return this.idNo;
   }

   public void increment() {
      ++this.idNo;
   }

   public boolean equals(Object some) {
      EdgeId someTransId = (EdgeId)some;
      return this.source.equals(someTransId.getSource()) && this.target.equals(someTransId.getTarget()) && this.idNo == someTransId.getIdNo();
   }

   public String toString() {
      return this.source + " " + this.target + " " + this.idNo;
   }

   public int hashCode() {
      String str = new String(this.source + this.target + this.idNo);
      return str.hashCode();
   }
}
