package com.uppaal.model;

public class OSUtils {
   private static OSUtils.OS detected = null;

   public static OSUtils.OS getOS() {
      if (detected == null) {
         String os = System.getProperty("os.name");
         if (os.equals("Linux")) {
            detected = OSUtils.OS.LINUX;
         } else if (!os.equals("SunOS") && !os.equals("Solaris")) {
            if (os.contains("Mac")) {
               detected = OSUtils.OS.MACOS;
            } else {
               detected = OSUtils.OS.WIN;
            }
         } else {
            detected = OSUtils.OS.SUNOS;
         }
      }

      return detected;
   }

   public static enum OS {
      WIN,
      MACOS,
      SUNOS,
      LINUX;
   }
}
