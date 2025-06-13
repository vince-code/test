package com.uppaal.engine;

import java.util.Iterator;
import java.util.List;

public class ChoiceOption extends ValueOption<Choice> {
   protected final List<Choice> choices;

   public ChoiceOption(String name, String display, List<Choice> choices, String defaultValue, String value) {
      super(name, display, findIn(defaultValue, choices), findIn(value, choices));
      this.choices = choices;
   }

   public List<Choice> getChoices() {
      return this.choices;
   }

   public void accept(OptionVisitor visitor) {
      visitor.visit(this);
   }

   public Choice fromString(String value) {
      return findIn(value, this.choices);
   }

   private static Choice findIn(String value, List<Choice> choices) {
      Iterator var2 = choices.iterator();

      Choice choice;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         choice = (Choice)var2.next();
      } while(!choice.getValue().equals(value));

      return choice;
   }
}
