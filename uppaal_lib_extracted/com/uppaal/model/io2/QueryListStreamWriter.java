package com.uppaal.model.io2;

import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.QueryList;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class QueryListStreamWriter {
   private final OutputStream os;
   private static final char[] hexDigit = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

   public QueryListStreamWriter(OutputStream os) {
      this.os = os;
   }

   public void write(QueryList queryList, String version) throws IOException {
      OutputStreamWriter osw = new OutputStreamWriter(this.os, StandardCharsets.ISO_8859_1);
      osw.write("//This file was generated from " + version + "\n");

      String formula;
      for(Iterator var4 = queryList.iterator(); var4.hasNext(); osw.write(formula + "\n")) {
         Query query = (Query)var4.next();
         String comment = query.getComment();
         comment = comment.replaceAll("\\\\", "\\\\\\\\");
         comment = comment.replaceAll("/", "\\\\/");
         osw.write("\n/*\n" + encode(comment) + "\n*/\n");
         formula = query.getFormula();
         if (formula.trim().equals("")) {
            formula = "//NO_QUERY";
         } else {
            formula = formula.replaceAll("(?m)\\n", "\\\\\n");
         }
      }

      osw.flush();
   }

   private static void byteToHex(StringBuffer buf, byte b) {
      buf.append(hexDigit[b >> 4 & 15]).append(hexDigit[b & 15]);
   }

   private static String encode(String s) {
      StringBuffer buf = new StringBuffer();
      int n = s.length();

      for(int i = 0; i < n; ++i) {
         char c = s.charAt(i);
         if ((c & -128) == 0) {
            buf.append(c);
         } else {
            byte hi = (byte)(c >>> 8);
            byte lo = (byte)(c & 255);
            buf.append("\\u");
            byteToHex(buf, hi);
            byteToHex(buf, lo);
         }
      }

      return buf.toString();
   }
}
