package com.uppaal.model.io2;

import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.QueryList;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class QueryListURLReader {
   private final URL url;

   public QueryListURLReader(URL url) {
      this.url = url;
   }

   public QueryList read(Document document) throws IOException {
      QueryList queryList = new QueryList();
      Object is;
      if ("file".equals(this.url.getProtocol())) {
         is = new FileInputStream(this.url.getPath());
      } else {
         is = this.url.openStream();
      }

      BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)is, StandardCharsets.ISO_8859_1));

      try {
         while(reader.ready()) {
            String s = reader.readLine();
            if (s.length() > 0 && !s.startsWith("//")) {
               String comment = "";
               if (s.equals("/*")) {
                  s = reader.readLine();
                  if (!s.equals("*/")) {
                     comment = comment + s;

                     for(s = reader.readLine(); !s.equals("*/"); s = reader.readLine()) {
                        comment = comment + "\n" + s;
                     }

                     comment = comment.replaceAll("([^\\\\]?)\\\\/", "$1/");
                     comment = comment.replaceAll("\\\\\\\\", "\\\\");
                     comment = decode(comment);
                  }

                  s = reader.readLine();
               }

               StringBuffer formula = new StringBuffer();
               if (!s.equals("//NO_QUERY")) {
                  while(s.endsWith("\\")) {
                     formula.append(s.substring(0, s.length() - 1)).append('\n');
                     s = reader.readLine();
                  }

                  formula.append(s);
               }

               Query query = new Query(new String(formula), comment);
               queryList.add(query);
            }
         }
      } catch (Throwable var10) {
         try {
            reader.close();
         } catch (Throwable var9) {
            var10.addSuppressed(var9);
         }

         throw var10;
      }

      reader.close();
      return queryList;
   }

   private static String decode(String s) {
      int i = 0;
      StringBuilder buf = new StringBuilder(s);

      while((i = buf.indexOf("\\u", i)) >= 0) {
         if (i + 6 > buf.length()) {
            return buf.toString();
         }

         char[] a = new char[4];
         buf.getChars(i + 2, i + 6, a, 0);

         for(int j = 0; j < 4; ++j) {
            if ((a[j] < '0' || a[j] > '9') && (a[j] < 'a' || a[j] > 'f')) {
               return buf.toString();
            }
         }

         char uni = (char)(hexToByte(a[0]) << 12 | hexToByte(a[1]) << 8 | hexToByte(a[2]) << 4 | hexToByte(a[3]));
         buf.delete(i, i + 6);
         buf.insert(i, uni);
      }

      return buf.toString();
   }

   private static int hexToByte(char c) {
      return c >= 'a' ? 10 + c - 97 : c - 48;
   }
}
