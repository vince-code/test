package com.uppaal.model.core2;

import com.uppaal.engine.Option;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;

public class EngineSettings extends Element {
   public EngineSettings(Element prototype) {
      super(prototype);
   }

   public String getFriendlyName() {
      return "settings";
   }

   public boolean isSet(String name) {
      return this.isPropertyLocal(name);
   }

   public boolean allDefault() {
      if (this.properties != null && !this.properties.isEmpty()) {
         Iterator var1 = this.properties.entrySet().iterator();

         String key;
         Property property;
         do {
            if (!var1.hasNext()) {
               return true;
            }

            Entry<String, Property> key_value = (Entry)var1.next();
            key = (String)key_value.getKey();
            property = (Property)key_value.getValue();
         } while(property.getValue().equals(this.prototype.getPropertyValue(key)));

         return false;
      } else {
         return true;
      }
   }

   public void resetToDefault(String name) {
      this.setProperty(name, (Object)null);
   }

   public void resetToDefault(List<String> names) {
      Iterator var2 = names.iterator();

      while(var2.hasNext()) {
         String name = (String)var2.next();
         this.resetToDefault(name);
      }

   }

   public String getValue(String name) {
      return (String)this.getPropertyValue(name);
   }

   public <T> T getValue(Option<T> option) {
      String value = (String)this.getPropertyValue(option.getName());
      return value == null ? option.getDefaultValue() : option.fromString(value);
   }

   protected void populateChange(String name, String value) {
      if ("--diagnostic".equals(name) && "0".equals(value)) {
         if ("3".equals(this.getValue("--state-representation"))) {
            this.setValue("--state-representation", "0");
         }
      } else if ("--state-representation".equals(name) && "3".equals(value)) {
         if ("0".equals(this.getValue("--diagnostic"))) {
            this.setValue("--diagnostic", "0");
         }
      } else if ("--state-representation".equals(name) && "2".equals(value)) {
         if ("1".equals(this.getValue("--reuse"))) {
            this.setValue("--reuse", "0");
         }
      } else if ("--reuse".equals(name) && "1".equals(value) && "2".equals(this.getValue("--state-representation"))) {
         this.setValue("--state-representation", "0");
      }

   }

   public <T> void setValue(Option<T> option, T value) {
      if (Objects.equals(option.getDefaultValue(), value)) {
         this.setProperty(option.getName(), (Object)null);
      } else {
         this.setProperty(option.getName(), value.toString());
      }

   }

   public void setValue(String name, String value) {
      this.setProperty(name, value);
   }

   public EngineSettings copy() {
      try {
         EngineSettings settings = new EngineSettings(this.prototype);
         this.copyInto(settings);
         return settings;
      } catch (CloneNotSupportedException var2) {
         throw new RuntimeException(var2);
      }
   }
}
