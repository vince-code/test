package com.uppaal.model.core2;

import com.uppaal.engine.Option;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class SettingsChangeTransaction {
   private EngineSettings settings;
   Map<String, SettingsChangeTransaction.Update> updates = new HashMap();

   public SettingsChangeTransaction(EngineSettings settings) {
      this.settings = settings;
   }

   public <T> void setValue(Option<T> option, T value) {
      String oldValue = this.settings.getValue(option.getName());
      String newValue = value != null && !option.getDefaultValue().equals(value) ? value.toString() : null;
      this.updates.put(option.getName(), new SettingsChangeTransaction.Update(oldValue, newValue));
   }

   public boolean willBeDefaultValue(Option<?> option) {
      SettingsChangeTransaction.Update update = (SettingsChangeTransaction.Update)this.updates.get(option.getName());
      if (update != null) {
         return update.newValue == null;
      } else {
         return this.settings.isPropertyLocal(option.getName());
      }
   }

   public void execute() {
      Iterator var1 = this.updates.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<String, SettingsChangeTransaction.Update> entry = (Entry)var1.next();
         this.settings.setValue((String)entry.getKey(), ((SettingsChangeTransaction.Update)entry.getValue()).newValue);
      }

   }

   private class Update {
      public final String oldValue;
      public final String newValue;

      public Update(String oldValue, String newValue) {
         this.oldValue = oldValue;
         this.newValue = newValue;
      }
   }
}
