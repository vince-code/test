package com.uppaal.engine.protocol;

import com.uppaal.engine.ServerException;

public class ParseException extends ServerException {
   public ParseException() {
   }

   public ParseException(String s) {
      super(s);
   }
}
