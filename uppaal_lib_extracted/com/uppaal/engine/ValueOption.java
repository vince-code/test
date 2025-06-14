package com.uppaal.engine;

public abstract class ValueOption<T> extends Option<T> {
   protected final T defaultValue;
   protected final T value;

   public ValueOption(String name, String display, T defaultValue, T value) {
      super(name, display);
      this.defaultValue = defaultValue;
      this.value = value;
   }

   public final T getDefaultValue() {
      return this.defaultValue;
   }

   public final T getValue() {
      return this.value;
   }
}
