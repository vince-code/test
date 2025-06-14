package com.uppaal.engine;

public abstract class GenericOptionVisitor implements OptionVisitor {
   public abstract <T> void handleOption(Option<T> var1);

   public void visit(BooleanOption option) {
      this.handleOption(option);
   }

   public void visit(ChoiceOption option) {
      this.handleOption(option);
   }

   public void visit(IntegerOption option) {
      this.handleOption(option);
   }

   public void visit(DecimalOption option) {
      this.handleOption(option);
   }

   public void visit(OptionSet option) {
      option.optionsAccept(this);
   }
}
