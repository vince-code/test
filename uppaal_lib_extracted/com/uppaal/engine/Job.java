package com.uppaal.engine;

public interface Job<T> {
   T run(Engine var1) throws Exception;

   default void error(Throwable e) {
   }
}
