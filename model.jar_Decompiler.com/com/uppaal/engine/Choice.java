package com.uppaal.engine;

public class Choice {
   private final String value;
   private final String display;

   public Choice(String value, String display) {
      this.value = value;
      this.display = display;

      assert value != null;

      assert display != null;

   }

   public String getValue() {
      return this.value;
   }

   public String getDisplay() {
      return this.display;
   }

   public String toString() {
      return this.value;
   }
}
