package com.uppaal.model.system;

public interface TraceListener<Transition extends AbstractTransition> {
   void reset();

   void append(Transition var1);

   void remove(Transition var1);

   void cover(Transition var1);

   void uncover(Transition var1);
}
