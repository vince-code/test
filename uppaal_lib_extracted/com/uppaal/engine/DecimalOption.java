package com.uppaal.engine;

public class DecimalOption extends ValueOption<Double> {
   protected final int fracMin;
   protected final int fracMax;
   private final double rangeMin;
   private final double rangeMax;

   public DecimalOption(String name, String display, String defaultValue, String value, String rangeMin, String rangeMax, String fracMin, String fracMax) {
      super(name, display, Double.parseDouble(defaultValue), Double.parseDouble(value));
      this.rangeMin = Double.parseDouble(rangeMin);
      this.rangeMax = Double.parseDouble(rangeMax);
      this.fracMin = Integer.parseInt(fracMin);
      this.fracMax = Integer.parseInt(fracMax);
   }

   public Double fromString(String valueStr) {
      double value = Double.parseDouble(valueStr);

      assert this.rangeMin <= value;

      assert value <= this.rangeMax;

      return value;
   }

   public double getRangeMin() {
      return this.rangeMin;
   }

   public double getRangeMax() {
      return this.rangeMax;
   }

   public void accept(OptionVisitor visitor) {
      visitor.visit(this);
   }

   public final int getFracMin() {
      return this.fracMin;
   }

   public final int getFracMax() {
      return this.fracMax;
   }
}
