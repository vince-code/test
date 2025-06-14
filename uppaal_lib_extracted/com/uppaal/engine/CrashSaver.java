package com.uppaal.engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class CrashSaver {
   public static final String ENGINE_CRASH_PREFIX = "enginecrash";

   public static Path getCrashDirectory() {
      Path p = Path.of(System.getProperty("user.home"), new String[]{".uppaal", "crashes"});
      File dir = p.toFile();
      if (!dir.exists() && !dir.mkdirs()) {
      }

      return p;
   }

   public static void SaveCrash(String prefix, String trace) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
      String currentTime = formatter.format((new Timestamp(System.currentTimeMillis())).toLocalDateTime());
      String crashFile = "";
      int i = 0;

      while(true) {
         String suffix = i == 0 ? "" : "-" + i;
         crashFile = prefix + "-" + currentTime + suffix + ".txt";
         File file = getCrashDirectory().resolve(crashFile).toFile();
         if (!file.exists()) {
            try {
               Files.writeString(getCrashDirectory().resolve(crashFile).toAbsolutePath(), trace, new OpenOption[0]);
            } catch (IOException var8) {
               var8.printStackTrace();
            }

            return;
         }

         ++i;
      }
   }
}
