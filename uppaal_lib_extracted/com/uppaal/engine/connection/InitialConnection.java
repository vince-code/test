package com.uppaal.engine.connection;

import java.io.BufferedWriter;
import java.io.InputStream;

public class InitialConnection {
   public final InputStream in;
   public final BufferedWriter out;

   public InitialConnection(InputStream in, BufferedWriter out) {
      this.in = in;
      this.out = out;
   }
}
