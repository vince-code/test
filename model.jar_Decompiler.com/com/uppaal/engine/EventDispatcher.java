package com.uppaal.engine;

public interface EventDispatcher {
   void invokeLater(Runnable var1);

   boolean isEventDispatchThread();
}
