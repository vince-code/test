package com.uppaal.engine.connection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class MonitorWriter extends BufferedWriter {
   public static final int DEFAULT_BUFFER_SIZE = 8388608;
   private final BufferedWriter target;
   private CircularByteOutputStream buffer;
   private final BufferedWriter snapshot;

   public MonitorWriter(BufferedWriter target) {
      this(target, 8388608);
   }

   public MonitorWriter(BufferedWriter target, int bufferSize) {
      super(nullWriter());
      this.target = target;
      this.buffer = new CircularByteOutputStream(bufferSize);
      this.snapshot = new BufferedWriter(new OutputStreamWriter(this.buffer, StandardCharsets.UTF_8));
   }

   public String getSnapshot() {
      return this.buffer.toString();
   }

   public void write(int c) throws IOException {
      this.target.write(c);
      this.snapshot.write(c);
   }

   public void write(char[] cbuf, int off, int len) throws IOException {
      this.target.write(cbuf, off, len);
      this.snapshot.write(cbuf, off, len);
   }

   public void write(String s, int off, int len) throws IOException {
      this.target.write(s, off, len);
      this.snapshot.write(s, off, len);
   }

   public void newLine() throws IOException {
      this.target.newLine();
      this.snapshot.newLine();
   }

   public void flush() throws IOException {
      this.target.flush();
      this.snapshot.flush();
   }

   public void close() throws IOException {
      this.target.close();
      this.snapshot.close();
   }

   public void write(char[] cbuf) throws IOException {
      this.target.write(cbuf);
      this.snapshot.write(cbuf);
   }

   public void write(String str) throws IOException {
      this.target.write(str);
      this.snapshot.write(str);
   }

   public Writer append(CharSequence csq) throws IOException {
      this.target.append(csq);
      this.snapshot.append(csq);
      return this;
   }

   public Writer append(CharSequence csq, int start, int end) throws IOException {
      this.target.append(csq, start, end);
      this.snapshot.append(csq, start, end);
      return this;
   }

   public Writer append(char c) throws IOException {
      this.target.append(c);
      this.snapshot.append(c);
      return this;
   }
}
