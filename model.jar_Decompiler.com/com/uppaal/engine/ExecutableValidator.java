package com.uppaal.engine;

import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

public class ExecutableValidator {
   private final String closeCommand;

   public ExecutableValidator(String closeCommand) {
      this.closeCommand = closeCommand;
   }

   public boolean isValid(File path) {
      try {
         Process server = Runtime.getRuntime().exec(path.getAbsolutePath());
         PrintStream ps = new PrintStream(server.getOutputStream());
         ps.println("{\"cmd\":\"" + this.closeCommand + "\"}");
         ps.close();
         if (server.waitFor(10L, TimeUnit.SECONDS)) {
            if (server.exitValue() == 0) {
               return true;
            }
         } else {
            server.destroyForcibly();
         }
      } catch (Exception var4) {
      }

      return false;
   }
}
