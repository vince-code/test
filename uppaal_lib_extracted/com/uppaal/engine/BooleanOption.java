package com.uppaal.engine;

public class BooleanOption extends ValueOption<Boolean> {
   public BooleanOption(String name, String display, String defaultValue, String value) {
      super(name, display, Boolean.parseBoolean(defaultValue), Boolean.parseBoolean(value));
   }

   public Boolean fromString(String value) {
      return Boolean.parseBoolean(value);
   }

   public final void accept(OptionVisitor visitor) {
      visitor.visit(this);
   }
}
