package com.uppaal.engine;

import com.uppaal.model.io2.Problem;
import com.uppaal.model.system.Polyhedron;
import com.uppaal.model.system.SystemEdgeSelect;
import com.uppaal.model.system.SystemLocation;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.symbolic.SymbolicState;
import com.uppaal.model.system.symbolic.SymbolicTrace;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class Parser {
   public static final int END = 1;
   public static final int WORD = 2;
   public static final int INT = 4;
   public static final int FLOAT = 7;
   public static final int LONG = 5;
   public static final int NONE = 6;
   public static final int PARTIAL_END = 8;
   private static final char EOS = '\u0000';
   private static final char CR = '\r';
   private static final char SPACE = ' ';
   private static final char DOT = '.';
   private static final char SEMICOLON = ';';
   private static String word;
   private int lookahead;
   private char peak;
   private final char[] cb = new char[256];
   private final char[] bb = new char[2048];
   private int offset = 0;
   private int bytes = 0;
   private final InputStreamReader in;
   private static final ResourceBundle locale = ResourceBundle.getBundle("locale.Parser");

   public Parser(InputStream in) {
      this.in = new InputStreamReader(in, StandardCharsets.UTF_8);
      this.peak = ' ';
      this.lookahead = 6;
   }

   public int getLookahead() throws IOException {
      if (this.lookahead == 6) {
         this.lookahead = this.nextToken();
      }

      return this.lookahead;
   }

   public String getWord() {
      return word;
   }

   public int getIntValue() {
      return Integer.valueOf(word);
   }

   public long getValue() {
      return Long.valueOf(word);
   }

   public BigDecimal getFloat() {
      return new BigDecimal(word);
   }

   private void fill() throws IOException {
      this.bytes = this.in.read(this.bb, 0, this.bb.length);
      this.offset = 0;
   }

   public String readBlock() throws IOException {
      assert this.lookahead == 6;

      StringBuilder s;
      for(s = new StringBuilder(); Character.isWhitespace(this.peak); this.peak = this.read()) {
      }

      while(true) {
         if (this.peak == '.') {
            this.peak = this.read();
            if (this.peak != '.') {
               int pos = 0;

               while((pos = s.indexOf("$", pos)) >= 0) {
                  int end = pos + 1;
                  int max = s.length();

                  label40:
                  while(end < max) {
                     switch(s.charAt(end)) {
                     case '\u0000':
                     case '\t':
                     case '\n':
                     case '\r':
                     case ' ':
                     case ':':
                        break label40;
                     default:
                        ++end;
                     }
                  }

                  try {
                     String v = locale.getString(s.substring(pos + 1, end));
                     s.replace(pos, end, v);
                     pos += v.length();
                  } catch (Exception var6) {
                     ++pos;
                  }
               }

               word = s.toString();
               return word;
            }
         }

         s.append(this.peak);
         this.peak = this.read();
      }
   }

   public char read() throws IOException {
      char c;
      do {
         if (this.offset == this.bytes) {
            this.fill();
            if (this.bytes == -1) {
               return '\u0000';
            }
         }

         c = this.bb[this.offset++];
      } while(c == '\r');

      return c;
   }

   public int nextToken() throws IOException {
      if (this.lookahead != 6) {
         int token = this.lookahead;
         this.lookahead = 6;
         return token;
      } else {
         int sign = 1;

         int i;
         for(i = 0; Character.isWhitespace(this.peak); this.peak = this.read()) {
         }

         switch(this.peak) {
         case '\u0000':
            throw new IOException("Server connection lost");
         case '\u0001':
         case '\u0002':
         case '\u0003':
         case '\u0004':
         case '\u0005':
         case '\u0006':
         case '\u0007':
         case '\b':
         case '\t':
         case '\n':
         case '\u000b':
         case '\f':
         case '\r':
         case '\u000e':
         case '\u000f':
         case '\u0010':
         case '\u0011':
         case '\u0012':
         case '\u0013':
         case '\u0014':
         case '\u0015':
         case '\u0016':
         case '\u0017':
         case '\u0018':
         case '\u0019':
         case '\u001a':
         case '\u001b':
         case '\u001c':
         case '\u001d':
         case '\u001e':
         case '\u001f':
         case ' ':
         case '!':
         case '"':
         case '#':
         case '$':
         case '%':
         case '&':
         case '\'':
         case '(':
         case ')':
         case '*':
         case '+':
         case ',':
         case '/':
         case ':':
         default:
            break;
         case '-':
            sign = -1;
            this.cb[i++] = this.peak;
            this.peak = this.read();
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
            int value;
            for(value = 0; this.peak >= '0' && this.peak <= '9'; this.peak = this.read()) {
               value = 10 * value + (this.peak - 48);
               this.cb[i++] = this.peak;
            }

            if (this.peak != 0 && !Character.isWhitespace(this.peak)) {
               if (this.peak != '.') {
                  break;
               }

               do {
                  this.cb[i++] = this.peak;
                  this.peak = this.read();
               } while(this.peak >= '0' && this.peak <= '9');

               if (this.peak != 0 && !Character.isWhitespace(this.peak)) {
                  break;
               }

               word = new String(this.cb, 0, i);
               return 7;
            }

            value = sign * value;
            if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
               word = new String(this.cb, 0, i);
               return 4;
            }

            word = new String(this.cb, 0, i);
            return 5;
         case '.':
            this.peak = this.read();
            if (Character.isWhitespace(this.peak)) {
               return 1;
            }

            this.cb[i++] = '.';
            break;
         case ';':
            this.peak = this.read();
            if (Character.isWhitespace(this.peak)) {
               return 8;
            }

            this.cb[i++] = ';';
         }

         while(this.peak != 0 && !Character.isWhitespace(this.peak)) {
            this.cb[i++] = this.peak;
            this.peak = this.read();
         }

         word = new String(this.cb, 0, i);
         return 2;
      }
   }

   protected ProtocolException getException(String errorMsg) {
      return word.equals("error") ? new ProtocolException("Concrete simulation does not currently support SMC") : new ProtocolException(errorMsg);
   }

   public void parseProlog() throws EngineException, IOException {
      String response = this.parseId();
      if (response.equals("error")) {
         throw new ServerException(this.readBlock());
      } else if (!response.equals("ok")) {
         throw new ProtocolException("Bug: Ok expected");
      }
   }

   public String parseId() throws ProtocolException, IOException {
      if (this.nextToken() != 2) {
         throw new ProtocolException("Bug: Word expected");
      } else {
         return this.getWord();
      }
   }

   public int parseInteger() throws ProtocolException, IOException, ServerException {
      int token = this.nextToken();
      if (token != 4) {
         if (token == 2 && this.getWord().equals("error")) {
            throw new ServerException(this.readBlock());
         } else {
            throw new ProtocolException("Bug: Integer expected");
         }
      } else {
         return this.getIntValue();
      }
   }

   public void parseEnd() throws ProtocolException, IOException {
      int token = this.nextToken();
      if (token != 1) {
         throw new ProtocolException("Bug: Dot expected");
      }
   }

   public void parsePartialEnd() throws ProtocolException, IOException {
      int token = this.nextToken();
      if (token != 8) {
         throw new ProtocolException("Bug: Semicolon expected");
      }
   }

   public SymbolicState parseSymbolicState(UppaalSystem system) throws ProtocolException, IOException, ServerException {
      SystemLocation[] l = this.locationVector(system);
      Polyhedron z = this.zone(system);
      int[] v = this.variableVector(system);
      return new SymbolicState(l, v, z);
   }

   private SystemLocation[] locationVector(UppaalSystem system) throws ProtocolException, IOException, ServerException {
      SystemLocation[] locations = new SystemLocation[system.getNoOfProcesses()];
      int i = 0;

      do {
         locations[i] = system.getLocation(i, this.parseInteger());
         ++i;
      } while(this.getLookahead() != 1);

      if (i != system.getNoOfProcesses()) {
         int var10002 = system.getNoOfProcesses();
         throw new ProtocolException("Invalid amount of locations. Expected " + var10002 + ", got " + i);
      } else {
         this.parseEnd();
         return locations;
      }
   }

   private int[] variableVector(UppaalSystem system) throws ProtocolException, IOException, ServerException {
      int[] vector = new int[system.getNoOfVariables()];

      for(int var3 = 0; this.getLookahead() != 1; vector[var3++] = this.parseInteger()) {
      }

      this.parseEnd();
      return vector;
   }

   private Polyhedron zone(UppaalSystem system) throws ProtocolException, IOException, ServerException {
      Polyhedron pol = new Polyhedron(system);

      while(this.getLookahead() != 1) {
         int i = this.parseInteger();
         int j = this.parseInteger();
         int b = this.parseInteger();
         pol.add(i, j, b ^ 1);
         this.parseEnd();
      }

      this.parseEnd();
      pol.trim();
      return pol;
   }

   private SystemEdgeSelect edgeCon(UppaalSystem system) throws ProtocolException, IOException, ServerException {
      int i = this.parseInteger();
      int j = this.parseInteger();
      LinkedList l = new LinkedList();

      while(this.getLookahead() != 8) {
         l.add(this.parseInteger());
      }

      this.parsePartialEnd();
      return system.createEdgeCon(i, j, l);
   }

   public Problem problem() throws EngineException, IOException {
      String type = this.parseId();
      String path = this.readBlock();
      int fline = this.parseInteger();
      int fcolumn = this.parseInteger();
      int lline = this.parseInteger();
      int lcolumn = this.parseInteger();
      String msg = this.readBlock();
      String context = this.readBlock();
      return new Problem(type, path, fline, fcolumn, lline, lcolumn, msg, context);
   }

   public char parseQueryResult() throws EngineException, IOException {
      this.parseProlog();
      return this.parseId().charAt(0);
   }

   public SymbolicTrace parseXTRTrace(UppaalSystem system) throws ProtocolException {
      SymbolicTrace trace = new SymbolicTrace();

      try {
         SymbolicState src = this.parseSymbolicState(system);
         trace.add(new SymbolicTransition((SymbolicState)null, (SystemEdgeSelect[])null, src));

         while(this.getLookahead() != 1) {
            SymbolicState dst = this.parseSymbolicState(system);
            SystemEdgeSelect[] edges = this.edgesXTR(system);
            trace.add(new SymbolicTransition(src, edges, dst));
            src = dst;
         }

         this.parseEnd();
         return trace;
      } catch (ServerException | IOException var7) {
         throw new ProtocolException(locale.getString("trace_mismatch"));
      }
   }

   private SystemEdgeSelect[] edgesXTR(UppaalSystem system) throws ProtocolException, IOException, ServerException {
      ArrayList edges = new ArrayList();

      do {
         edges.add(this.edgeCon(system));
      } while(this.getLookahead() != 1);

      this.parseEnd();
      SystemEdgeSelect[] se = new SystemEdgeSelect[edges.size()];
      return (SystemEdgeSelect[])edges.toArray(se);
   }
}
