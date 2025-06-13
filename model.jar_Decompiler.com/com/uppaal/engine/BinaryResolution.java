package com.uppaal.engine;

import com.uppaal.model.OSUtils;
import java.io.File;
import java.util.Optional;
import java.util.function.Function;

public class BinaryResolution {
   private final File installDirectory;

   public BinaryResolution(File installDirectory) {
      this.installDirectory = installDirectory;
   }

   public BinaryResolution(String installDirectory) {
      this(new File(installDirectory));
   }

   public Optional<File> search(String executableName, Function<File, Boolean> validator) {
      File[] searchPath = new File[]{new File(this.installDirectory, "bin/" + executableName), (new File("bin/" + executableName)).getAbsoluteFile(), new File(this.detectEngineLocation(), executableName), new File(this.installDirectory, "bin/" + executableName + ".sh"), (new File("bin/" + executableName + ".sh")).getAbsoluteFile(), new File(this.detectEngineLocation(), executableName + ".sh")};
      File[] var4 = searchPath;
      int var5 = searchPath.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         File path = var4[var6];
         if (path.exists() && path.canExecute() && (Boolean)validator.apply(path)) {
            return Optional.of(path);
         }
      }

      return Optional.empty();
   }

   public Optional<File> search(String executableName) {
      return this.search(executableName, (file) -> {
         return true;
      });
   }

   /** @deprecated */
   @Deprecated
   public File defaultLocation(String executableName) {
      return new File(this.installDirectory, "bin/" + executableName);
   }

   public File getInstallDirectory() {
      return this.installDirectory;
   }

   private File detectEngineLocation() {
      switch(OSUtils.getOS()) {
      case LINUX:
         return new File(this.installDirectory, "bin-Linux");
      case SUNOS:
         return new File(this.installDirectory, "bin-SunOS");
      case MACOS:
         return new File(this.installDirectory, "bin-Darwin");
      case WIN:
         return new File(this.installDirectory, "bin-Windows");
      default:
         throw new RuntimeException("Unknown operating system");
      }
   }
}
