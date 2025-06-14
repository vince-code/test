package com.uppaal.engine;

public class IntegerOption extends ValueOption<Integer> {
   private final int rangeMin;
   private final int rangeMax;

   public IntegerOption(String name, String display, String defaultValue, String value, String rangeMin, String rangeMax) {
      super(name, display, Integer.parseInt(defaultValue), Integer.parseInt(value));
      this.rangeMin = Integer.parseInt(rangeMin);
      this.rangeMax = Integer.parseInt(rangeMax);
   }

   public int getRangeMin() {
      return this.rangeMin;
   }

   public int getRangeMax() {
      return this.rangeMax;
   }

   public Integer fromString(String valueStr) {
      int value = Integer.parseInt(valueStr);

      assert this.rangeMin <= value;

      assert value <= this.rangeMax;

      return value;
   }

   public void accept(OptionVisitor visitor) {
      visitor.visit(this);
   }
}
