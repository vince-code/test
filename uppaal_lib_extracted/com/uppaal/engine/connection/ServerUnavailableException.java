package com.uppaal.engine.connection;

import com.uppaal.engine.EngineException;

public class ServerUnavailableException extends EngineException {
   public ServerUnavailableException() {
   }

   public ServerUnavailableException(String s) {
      super(s);
   }

   public ServerUnavailableException(String s, Throwable cause) {
      super(cause.getMessage() + ": " + s, cause);
   }

   public ServerUnavailableException(Throwable cause) {
      super(cause.getMessage(), cause);
   }
}
