package com.uppaal.engine;

import com.uppaal.model.core2.EngineSettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OptionSet extends Option<Void> {
   protected ArrayList<Option<?>> options = new ArrayList();
   protected Map<String, Option<?>> index = new HashMap();

   public OptionSet(String name, String display) {
      super(name, display);
   }

   public void add(Option<?> p) {
      this.options.add(p);
      this.index.put(p.getName(), p);
   }

   public Void getValue() {
      throw new UnsupportedOperationException("Not supported.");
   }

   public Void getDefaultValue() {
      throw new UnsupportedOperationException("Not supported.");
   }

   public Void fromString(String value) {
      return null;
   }

   public boolean isSet(EngineSettings settings) {
      Iterator var2 = this.options.iterator();

      Option vo;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         vo = (Option)var2.next();
      } while(!vo.isSet(settings));

      return true;
   }

   public void accept(OptionVisitor visitor) {
      visitor.visit(this);
   }

   public void optionsAccept(OptionVisitor visitor) {
      Iterator var2 = this.options.iterator();

      while(var2.hasNext()) {
         Option<?> option = (Option)var2.next();
         option.accept(visitor);
      }

   }
}
