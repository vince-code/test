package com.uppaal.model.io2;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CachedOutputStream extends BufferedOutputStream {
   public CachedOutputStream(OutputStream out) {
      super(out);
   }

   public CachedOutputStream(OutputStream out, int size) {
      super(out, size);
   }

   public CachedOutputStream(File file) throws FileNotFoundException {
      super(new FileOutputStream(file));
   }

   public CachedOutputStream(File file, int size) throws FileNotFoundException {
      super(new FileOutputStream(file), size);
   }

   public void flush() {
   }

   public void close() throws IOException {
      super.flush();
      super.close();
   }
}
