package com.uppaal.engine;

public interface OptionVisitor {
   void visit(BooleanOption var1);

   void visit(ChoiceOption var1);

   void visit(IntegerOption var1);

   void visit(DecimalOption var1);

   void visit(OptionSet var1);
}
