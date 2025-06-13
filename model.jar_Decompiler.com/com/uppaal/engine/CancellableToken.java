package com.uppaal.engine;

public class CancellableToken {
   private final EventDispatcher dispatcher;
   private boolean isCancelled;

   public CancellableToken(EventDispatcher dispatcher) {
      this.dispatcher = dispatcher;
   }

   public void invokeLater(Runnable runnable) {
      this.dispatcher.invokeLater(() -> {
         if (!this.isCancelled) {
            runnable.run();
         }

      });
   }

   public void cancel() {
      assert this.dispatcher.isEventDispatchThread();

      this.isCancelled = true;
   }
}
