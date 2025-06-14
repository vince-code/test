package com.uppaal.engine.connection;

import com.uppaal.engine.EngineException;
import com.uppaal.model.OSUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class CommandConnection extends Connection {
   private final String[] preSplitCommand;
   private final String command;
   private Process process;

   public CommandConnection(String name, String[] command) {
      super(name);
      this.preSplitCommand = command;
      this.command = "";
   }

   public CommandConnection(String name, String command) {
      super(name);
      this.command = command;
      this.preSplitCommand = new String[0];
   }

   private static String[] splitGeneric(String command, char escapeChar) {
      ArrayList<String> split = new ArrayList();
      StringBuilder current = new StringBuilder(command.length());
      boolean singleQuoted = false;
      boolean doubleQuoted = false;

      for(int i = 0; i < command.length(); ++i) {
         char c = command.charAt(i);
         if (c == ' ') {
            if (!singleQuoted && !doubleQuoted) {
               split.add(current.toString());
               current.setLength(0);
            } else {
               current.append(' ');
            }
         } else if (c == '\'') {
            if (doubleQuoted) {
               current.append('\'');
            } else {
               singleQuoted = !singleQuoted;
            }
         } else if (c == '"') {
            if (singleQuoted) {
               current.append('"');
            } else {
               doubleQuoted = !doubleQuoted;
            }
         } else if (c != escapeChar) {
            current.append(c);
         } else {
            char next = command.charAt(i + 1);
            ++i;
            if (next == ' ') {
               current.append(' ');
            } else if (next == escapeChar) {
               current.append(escapeChar);
            } else if (next == '"') {
               current.append('"');
            } else {
               if (next != '\'') {
                  throw new RuntimeException("Invalid escape sequence \\" + next);
               }

               current.append('\'');
            }
         }
      }

      if (!singleQuoted && !doubleQuoted) {
         split.add(current.toString());
         return (String[])split.toArray(new String[0]);
      } else {
         throw new RuntimeException("Unclosed quote");
      }
   }

   public static String[] splitUnix(String command) {
      return splitGeneric(command, '\\');
   }

   public static String[] splitWindows(String command) {
      return command.indexOf(96) >= 0 ? splitGeneric(command, '`') : splitGeneric(command, '^');
   }

   public static String joinCommand(String[] command) {
      String escapedSpace = OSUtils.getOS() == OSUtils.OS.WIN ? "` " : "\\ ";
      return String.join(" ", (CharSequence[])Arrays.stream(command).map((x) -> {
         return x.replace(" ", escapedSpace);
      }).toArray((x$0) -> {
         return new String[x$0];
      }));
   }

   public static String[] split(String command) {
      switch(OSUtils.getOS()) {
      case LINUX:
      case MACOS:
      case SUNOS:
         return splitUnix(command);
      case WIN:
         return splitWindows(command);
      default:
         throw new RuntimeException("Unsupported operating system");
      }
   }

   public String getCommand() {
      return this.command;
   }

   public InitialConnection connect() throws EngineException {
      try {
         ProcessBuilder pb = new ProcessBuilder(this.preSplitCommand.length > 0 ? this.preSplitCommand : split(this.command));
         Map<String, String> env = pb.environment();
         env.put("LD_LIBRARY_PATH", System.getProperty("java.library.path"));
         this.process = pb.start();
         OutputStream out = this.process.getOutputStream();
         InputStream in = this.process.getInputStream();
         final InputStream err = this.process.getErrorStream();
         if (in != null && out != null && err != null) {
            final StringBuffer errors = new StringBuffer();
            (new Thread("ServerErrorStream") {
               public void run() {
                  BufferedReader errorReader = new BufferedReader(new InputStreamReader(err, StandardCharsets.UTF_8));

                  String line;
                  try {
                     while((line = errorReader.readLine()) != null) {
                        System.err.println(line);
                        errors.append(line).append(System.lineSeparator());
                     }
                  } catch (IOException var4) {
                  }

               }
            }).start();
            BufferedWriter bufferedOut = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            return new InitialConnection(in, bufferedOut);
         } else {
            int v = this.process.exitValue();
            throw new EngineException("Engine stopped with error code " + v);
         }
      } catch (IOException var8) {
         throw new ServerUnavailableException(var8.getMessage());
      } catch (IllegalThreadStateException var9) {
         throw new EngineException("Could not get I/O streams from engine.");
      }
   }

   public void disconnect() {
      if (this.isConnected()) {
         if (this.process != null) {
            final Object lock = new Object();
            synchronized(lock) {
               Thread t = new Thread("EngineWaiter") {
                  public void run() {
                     try {
                        CommandConnection.this.process.waitFor();
                     } catch (InterruptedException var4) {
                     }

                     synchronized(lock) {
                        lock.notify();
                     }
                  }
               };
               t.start();

               try {
                  lock.wait(2000L);
               } catch (InterruptedException var6) {
               }

               this.process.destroy();
               this.process = null;
            }
         }

      }
   }

   public void kill() {
      if (this.isConnected()) {
         this.process.destroy();
         this.process = null;
      }
   }

   public boolean isConnected() {
      return this.process != null && this.process.isAlive();
   }

   public String getErrorStream() {
      if (this.process.getErrorStream() == null) {
         return "";
      } else {
         try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            this.process.getErrorStream().transferTo(result);
            return result.toString(StandardCharsets.UTF_8);
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      }
   }

   public Connection clone() {
      return this.preSplitCommand.length > 0 ? new CommandConnection(this.getName(), this.preSplitCommand) : new CommandConnection(this.getName(), this.command);
   }
}
