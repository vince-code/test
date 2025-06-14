package com.uppaal.engine.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Objects;

public class CircularByteOutputStream extends ByteArrayOutputStream {
   private boolean firstIteration = true;

   public CircularByteOutputStream(int size) {
      super(size);
   }

   public void reset() {
      super.reset();
      this.firstIteration = true;
   }

   public int size() {
      return this.firstIteration ? this.count : this.buf.length;
   }

   public void write(int b) {
      super.write(b);
      if (this.count >= this.buf.length) {
         this.firstIteration = false;
         this.count = 0;
      }

   }

   public void write(byte[] b) {
      this.write(b, 0, b.length);
   }

   public void write(byte[] b, int off, int len) {
      Objects.checkFromIndexSize(off, len, b.length);
      int remaining;
      if (len > this.buf.length) {
         remaining = len - this.buf.length;
         off += remaining;
         len = this.buf.length;
      }

      for(remaining = this.buf.length - this.count; len >= remaining; off += remaining) {
         super.write(b, off, remaining);
         this.count = 0;
         this.firstIteration = false;
         len -= remaining;
      }

      super.write(b, off, len);
   }

   public void writeTo(OutputStream out) throws IOException {
      if (this.firstIteration) {
         out.write(this.buf, 0, this.count);
      } else {
         out.write(this.buf, this.count, this.buf.length - this.count);
         out.write(this.buf, 0, this.count);
      }

   }

   public byte[] toByteArray() {
      ByteArrayOutputStream out = new ByteArrayOutputStream(this.size());

      try {
         this.writeTo(out);
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }

      return out.toByteArray();
   }

   public String toString() {
      return this.toString(Charset.defaultCharset());
   }

   public String toString(String charset) throws UnsupportedEncodingException {
      return this.toString(Charset.forName(charset));
   }

   public String toString(Charset charset) {
      ByteArrayOutputStream out = new ByteArrayOutputStream(this.size());

      try {
         this.writeTo(out);
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }

      return out.toString(charset);
   }
}
