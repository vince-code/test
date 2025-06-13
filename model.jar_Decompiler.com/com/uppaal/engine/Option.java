package com.uppaal.engine;

import com.uppaal.model.core2.EngineSettings;

public abstract class Option<T> {
   protected final String name;
   protected final String display;

   public Option(String name, String display) {
      assert name != null && !name.isBlank();

      assert display != null;

      this.name = name;
      this.display = display;
   }

   public String getName() {
      return this.name;
   }

   public String getDisplay() {
      return this.display;
   }

   public abstract T getDefaultValue();

   public abstract T getValue();

   public abstract T fromString(String var1);

   public abstract void accept(OptionVisitor var1);

   public boolean isSet(EngineSettings settings) {
      assert settings != null;

      return settings.isSet(this.name);
   }

   public String toString() {
      return this.display;
   }
}
