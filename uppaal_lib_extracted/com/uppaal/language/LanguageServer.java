package com.uppaal.language;

import com.uppaal.engine.BinaryResolution;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.ExecutableValidator;
import com.uppaal.engine.SwingFuture;
import com.uppaal.engine.connection.CommandConnection;
import com.uppaal.model.OSUtils;
import com.uppaal.model.core2.Document;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class LanguageServer {
   private static BinaryResolution binaryResolution;
   private static LanguageServer instance;
   private CommandConnection connection;
   private ExecutorService executor;
   private Protocol protocol;

   private LanguageServer() {
   }

   private LanguageServer(File languageServer) {
      this.connection = new CommandConnection("ULS connection", new String[]{languageServer.getAbsolutePath()});
      this.executor = Executors.newSingleThreadExecutor();
   }

   public static void disable() {
      instance = new LanguageServer();
   }

   public static void setBinaryResolutionStrategy(BinaryResolution resolutionStrategy) {
      binaryResolution = resolutionStrategy;
   }

   public static Optional<LanguageServer> initializedInstance() {
      LanguageServer server = getOrCreateInstance();
      return server != null && server.isInitialized() ? Optional.of(server) : Optional.empty();
   }

   private static LanguageServer getOrCreateInstance() {
      if (instance == null) {
         Optional<File> uls = findExecutable();
         if (uls.isPresent()) {
            launchServer((File)uls.get());
         } else {
            disable();
         }
      }

      return instance;
   }

   private static Optional<File> findExecutable() {
      ExecutableValidator validator = new ExecutableValidator("exit");
      BinaryResolution var10000 = (BinaryResolution)Objects.requireNonNull(binaryResolution);
      String var10001 = executableName();
      Objects.requireNonNull(validator);
      return var10000.search(var10001, validator::isValid);
   }

   private static void launchServer(File uls) {
      try {
         instance = new LanguageServer(uls);
         instance.checkAvailability();
      } catch (Exception var2) {
         instance = new LanguageServer();
         instance.connection = null;
         instance.executor = null;
         throw new RuntimeException("Failed to start language server", var2);
      }
   }

   private static String executableName() {
      return OSUtils.getOS() == OSUtils.OS.WIN ? "uls.exe" : "uls";
   }

   public boolean isInitialized() {
      return this.connection != null;
   }

   public SwingFuture<Void> upload(Document document) {
      SwingFuture<Void> future = new SwingFuture();
      this.submit(() -> {
         if (!future.isCancelled()) {
            try {
               this.protocol.upload(document);
               future.complete((Object)null);
            } catch (Exception var4) {
               future.completeExceptionally(var4);
            }

         }
      });
      return future;
   }

   public SwingFuture<Void> setCurrentNode(String xpath) {
      SwingFuture<Void> future = new SwingFuture();
      this.submit(() -> {
         try {
            this.protocol.setCurrentNode(xpath);
            future.complete((Object)null);
         } catch (Exception var4) {
            future.completeExceptionally(var4);
         }

      });
      return future;
   }

   public SwingFuture<List<Suggestion>> autocomplete(String xpath, String identifier, int offset) {
      SwingFuture<List<Suggestion>> future = new SwingFuture();
      this.submit(() -> {
         if (!future.isCancelled()) {
            try {
               List<Suggestion> suggestions = this.protocol.autocomplete(xpath, identifier, offset);
               if (future.isCancelled()) {
                  return;
               }

               if (suggestions != null) {
                  future.complete(suggestions);
               } else {
                  future.complete(Collections.emptyList());
               }
            } catch (Exception var6) {
               future.completeExceptionally(var6);
            }

         }
      });
      return future;
   }

   public void kill() {
      if (this.isInitialized()) {
         if (this.connection.isConnected()) {
            this.executor.submit(this::silentClose);
            this.executor.shutdown();

            try {
               if (!this.executor.awaitTermination(10L, TimeUnit.MILLISECONDS)) {
                  this.connection.kill();
               }
            } catch (InterruptedException var5) {
               this.connection.kill();
            } finally {
               this.executor = null;
               this.connection = null;
               this.protocol = null;
            }
         } else {
            this.executor = null;
            this.connection = null;
            this.protocol = null;
         }

      }
   }

   private void silentClose() {
      try {
         this.protocol.close();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   private void submit(Runnable task) {
      try {
         this.checkAvailability();
         this.executor.execute(task);
      } catch (IOException | EngineException var3) {
         throw new RuntimeException(var3);
      }
   }

   private void checkAvailability() throws EngineException, IOException {
      if (!this.isInitialized()) {
         throw new RuntimeException("Language server must be initialized before use");
      } else {
         if (!this.connection.isConnected()) {
            this.protocol = Protocol.handshake(this.connection.connect());
         }

      }
   }
}
