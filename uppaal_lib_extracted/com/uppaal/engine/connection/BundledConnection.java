package com.uppaal.engine.connection;

import com.uppaal.engine.BinaryResolution;
import com.uppaal.engine.ExecutableValidator;
import com.uppaal.model.OSUtils;
import java.io.File;
import java.util.Objects;

public class BundledConnection extends LocalConnection {
   public static final String BUNDLED = "Bundled";
   private static File bundledEngine = null;

   private static String getEngineName() {
      switch(OSUtils.getOS()) {
      case LINUX:
      case SUNOS:
      case MACOS:
         return "server";
      case WIN:
         return "server.exe";
      default:
         throw new RuntimeException("Unknown operating system");
      }
   }

   public static void setBundledEngine(File bundledEngine) {
      BundledConnection.bundledEngine = bundledEngine;
   }

   public BundledConnection(BinaryResolution binaryResolution) {
      super("Bundled", findExecutable(binaryResolution));
   }

   private static File findExecutable(BinaryResolution binaryResolution) {
      ExecutableValidator validator = new ExecutableValidator("close");
      String var10001 = getEngineName();
      Objects.requireNonNull(validator);
      return (File)binaryResolution.search(var10001, validator::isValid).orElseGet(() -> {
         return binaryResolution.defaultLocation(getEngineName());
      });
   }

   private BundledConnection(String name, File serverBinary) {
      super(name, serverBinary);
   }

   public Connection clone() {
      return new BundledConnection(this.getName(), this.getServerBinary());
   }
}
