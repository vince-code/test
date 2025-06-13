package com.uppaal.model.io2.XTAReaderParsing;

import com.uppaal.model.core2.AbstractLocation;
import com.uppaal.model.core2.BranchPoint;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Template;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class XTAReader implements XTAReaderConstants {
   private String errors;
   public XTAReaderTokenManager token_source;
   SimpleCharStream jj_input_stream;
   public Token token;
   public Token jj_nt;
   private int jj_gen;
   private final int[] jj_la1;
   private static int[] jj_la1_0;
   private static int[] jj_la1_1;
   private List<int[]> jj_expentries;
   private int[] jj_expentry;
   private int jj_kind;

   public Document parse(Element prototype) throws ParseException {
      Document doc = new Document(prototype);
      this.systemSpec(doc);
      return doc;
   }

   private void error(String str) {
      if (this.errors == null) {
         this.errors = str;
      } else {
         this.errors = this.errors + "\n" + str;
      }

   }

   public String getErrors() {
      return this.errors;
   }

   private StringBuffer specialImage(Token t) {
      return t != null ? this.specialImage(t, t.image) : new StringBuffer("");
   }

   private StringBuffer specialImage(Token t, String repl) {
      Stack stack = new Stack();
      StringBuffer s = new StringBuffer();
      stack.push(repl);

      for(t = t.specialToken; t != null; t = t.specialToken) {
         stack.push(t.image);
      }

      while(!stack.empty()) {
         s.append((String)stack.pop());
      }

      return s;
   }

   private String skipTo(XTAReader.TerminationCondition condition, boolean leading) throws ParseException {
      StringBuffer s;
      Token t;
      for(s = new StringBuffer(); !condition.terminate(); s.append(t.image)) {
         t = this.getNextToken();
         if (t.kind == 0) {
            if (!condition.terminate()) {
               throw new ParseException("Unexpected end of file.");
            }
            break;
         }

         if (leading) {
            s.append(this.specialImage(t.specialToken));
         } else {
            leading = true;
         }
      }

      return s.toString();
   }

   private String skipTo(int tokenKind, boolean leading) throws ParseException {
      StringBuffer s = new StringBuffer();

      for(Token t = this.getNextToken(); t.kind != tokenKind; t = this.getNextToken()) {
         if (t.kind == 0) {
            String var10002 = tokenImage[tokenKind];
            throw new ParseException("Token '" + var10002 + "'expected.");
         }

         if (leading) {
            s.append(this.specialImage(t.specialToken));
         } else {
            leading = true;
         }

         s.append(t.image);
      }

      return s.toString();
   }

   public final void systemSpec(Document document) throws ParseException {
      this.globalvardeclblock(document);

      while(this.getToken(1).kind != 0) {
         this.declaration(document);
      }

   }

   public final void declaration(Document document) throws ParseException {
      while(this.getToken(1).kind == 5) {
         this.processdecl(document);
      }

      this.systemDeclaration(document);
   }

   void systemDeclaration(Document document) throws ParseException {
      String decl = this.skipTo(new XTAReader.TerminationCondition() {
         public boolean terminate() {
            return XTAReader.this.getToken(1).kind == 25 || XTAReader.this.getToken(1).kind == 5 || XTAReader.this.getToken(1).kind == 0;
         }
      }, true);
      if (decl.length() > 0) {
         if (this.getToken(1).kind == 0) {
            document.setProperty("system", decl);
         } else {
            String declaration = (String)document.getPropertyValue("declaration");
            if (declaration == null) {
               declaration = decl;
            } else {
               declaration = declaration.concat(decl);
            }

            document.setProperty("declaration", declaration);
         }
      }

   }

   void globalvardeclblock(Document document) throws ParseException {
      String decl = this.skipTo(new XTAReader.TerminationCondition() {
         public boolean terminate() {
            return XTAReader.this.getToken(1).kind == 25 || XTAReader.this.getToken(1).kind == 5;
         }
      }, true);
      if (decl.length() > 0) {
         document.setProperty("declaration", decl);
      }

   }

   public final void processdecl(Document document) throws ParseException {
      Template tplt = document.createTemplate();
      this.PTHead(tplt);
      this.PTParamList(tplt);
      this.PTBody(tplt);
      document.insert(tplt, document.getLastTATemplate());
   }

   public final void PTHead(Template tplt) throws ParseException {
      XTAReader.TerminationCondition tc = new XTAReader.TerminationCondition() {
         public boolean terminate() {
            return XTAReader.this.getToken(1).kind == 30 || XTAReader.this.getToken(1).kind == 36;
         }
      };

      String s;
      try {
         this.jj_consume_token(5);
         Token t = this.jj_consume_token(53);
         s = t.image;
      } catch (ParseException var6) {
         s = this.skipTo(tc, true);
      }

      tplt.setProperty("name", s);
   }

   public final void PTParamList(Template tplt) throws ParseException {
      String param = "";
      switch(this.jj_nt.kind) {
      case 30:
         this.jj_consume_token(30);
         param = this.skipTo(31, true);
         break;
      default:
         this.jj_la1[0] = this.jj_gen;
      }

      tplt.setProperty("parameter", param);
   }

   public final void PTBody(Template tplt) throws ParseException {
      this.jj_consume_token(36);
      this.body(tplt);
      this.jj_consume_token(37);
   }

   public final void body(Template tplt) throws ParseException {
      Map locations = new HashMap();
      this.bodyLocal(tplt);
      switch(this.jj_nt.kind) {
      case 15:
         this.jj_consume_token(15);
         this.bodyStates(tplt, locations);
         break;
      default:
         this.jj_la1[1] = this.jj_gen;
      }

      switch(this.jj_nt.kind) {
      case 16:
         this.jj_consume_token(16);
         this.bodyBranches(tplt, locations);
         break;
      default:
         this.jj_la1[2] = this.jj_gen;
      }

      label58:
      switch(this.jj_nt.kind) {
      case 18:
         this.jj_consume_token(18);
         this.bodyCommitted(locations);

         while(true) {
            switch(this.jj_nt.kind) {
            case 34:
               this.jj_consume_token(34);
               this.bodyCommitted(locations);
               break;
            default:
               this.jj_la1[3] = this.jj_gen;
               this.jj_consume_token(35);
               break label58;
            }
         }
      default:
         this.jj_la1[4] = this.jj_gen;
      }

      label48:
      switch(this.jj_nt.kind) {
      case 13:
         this.jj_consume_token(13);
         this.bodyUrgent(locations);

         while(true) {
            switch(this.jj_nt.kind) {
            case 34:
               this.jj_consume_token(34);
               this.bodyUrgent(locations);
               break;
            default:
               this.jj_la1[5] = this.jj_gen;
               this.jj_consume_token(35);
               break label48;
            }
         }
      default:
         this.jj_la1[6] = this.jj_gen;
      }

      switch(this.jj_nt.kind) {
      case 14:
         this.jj_consume_token(14);
         this.bodyInit(tplt, locations);
         break;
      default:
         this.jj_la1[7] = this.jj_gen;
      }

      switch(this.jj_nt.kind) {
      case 17:
         this.jj_consume_token(17);
         this.bodyTransitions(tplt, locations);
         break;
      default:
         this.jj_la1[8] = this.jj_gen;
      }

   }

   void bodyLocal(Template tplt) throws ParseException {
      String decl = this.skipTo(new XTAReader.TerminationCondition() {
         int open = 0;

         public boolean terminate() {
            Token t = XTAReader.this.getToken(1);
            if (t.kind == 36) {
               ++this.open;
            } else if (t.kind == 37) {
               --this.open;
            }

            return t.kind == 15 || this.open == -1;
         }
      }, true);
      if (decl.length() != 0) {
         tplt.setProperty("declaration", decl.toString());
      }

   }

   public final void bodyStates(Template tplt, Map locations) throws ParseException {
      this.bodyState(tplt, locations);

      while(true) {
         switch(this.jj_nt.kind) {
         case 34:
            this.jj_consume_token(34);
            this.bodyState(tplt, locations);
            break;
         default:
            this.jj_la1[9] = this.jj_gen;
            this.jj_consume_token(35);
            return;
         }
      }
   }

   public final void bodyBranches(Template tplt, Map locations) throws ParseException {
      this.bodyBranch(tplt, locations);

      while(true) {
         switch(this.jj_nt.kind) {
         case 34:
            this.jj_consume_token(34);
            this.bodyBranch(tplt, locations);
            break;
         default:
            this.jj_la1[10] = this.jj_gen;
            this.jj_consume_token(35);
            return;
         }
      }
   }

   public final void bodyState(Template tplt, Map locations) throws ParseException {
      Token t = this.jj_consume_token(53);
      AbstractLocation location = tplt.createLocation();
      location.setProperty("name", t.image);
      tplt.insert(location, tplt.getLast());
      locations.put(t.image, location);
      switch(this.jj_nt.kind) {
      case 36:
         this.jj_consume_token(36);
         String[] p = this.skipTo(37, false).split(";", 2);
         if (p.length > 0 && p[0].length() > 0) {
            location.setProperty("invariant", p[0].trim());
         }

         if (p.length > 1 && p[1].length() > 0) {
            location.setProperty("exponentialrate", p[1].trim());
         }
         break;
      default:
         this.jj_la1[11] = this.jj_gen;
      }

   }

   public final void bodyBranch(Template tplt, Map locations) throws ParseException {
      Token t = this.jj_consume_token(53);
      BranchPoint branchPoint = tplt.createBranchPoint();
      branchPoint.setProperty("name", t.image);
      tplt.insert(branchPoint, tplt.getLast());
      locations.put(t.image, branchPoint);
   }

   public final void bodyCommitted(Map locations) throws ParseException {
      AbstractLocation loc = this.location(locations);
      if (loc != null) {
         loc.setProperty("committed", true);
      }

   }

   public final void bodyUrgent(Map locations) throws ParseException {
      AbstractLocation loc = this.location(locations);
      if (loc != null) {
         loc.setProperty("urgent", true);
      }

   }

   public final void bodyInit(Template tplt, Map locations) throws ParseException {
      AbstractLocation loc = this.location(locations);
      this.jj_consume_token(35);
      if (loc != null) {
         loc.setProperty("init", true);
      }

   }

   public final void bodyTransitions(Template tplt, Map locations) throws ParseException {
      AbstractLocation src = this.location(locations);
      boolean controllable = this.bodyTransitionSymbol();
      this.destination(tplt, locations, src, controllable);

      while(true) {
         switch(this.jj_nt.kind) {
         case 34:
            this.jj_consume_token(34);
            switch(this.jj_nt.kind) {
            case 53:
               src = this.location(locations);
               break;
            default:
               this.jj_la1[13] = this.jj_gen;
            }

            controllable = this.bodyTransitionSymbol();
            this.destination(tplt, locations, src, controllable);
            break;
         default:
            this.jj_la1[12] = this.jj_gen;
            this.jj_consume_token(35);
            return;
         }
      }
   }

   public final boolean bodyTransitionSymbol() throws ParseException {
      switch(this.jj_nt.kind) {
      case 46:
         this.jj_consume_token(46);
         return true;
      case 47:
         this.jj_consume_token(47);
         return false;
      default:
         this.jj_la1[14] = this.jj_gen;
         this.jj_consume_token(-1);
         throw new ParseException();
      }
   }

   public final AbstractLocation location(Map locations) throws ParseException {
      Token t = this.jj_consume_token(53);
      AbstractLocation loc = (AbstractLocation)locations.get(t.image);
      if (loc == null) {
         this.error("Location " + t.image + " not found.");
      }

      return loc;
   }

   public final void destination(Template tplt, Map locations, AbstractLocation src, boolean controllable) throws ParseException {
      try {
         AbstractLocation dst = this.location(locations);
         Edge edge = tplt.createEdge();
         edge.setSource(src);
         edge.setTarget(dst);
         edge.setProperty("controllable", controllable);
         if (src != null && dst != null) {
            tplt.insert(edge, tplt.getLast());
         }

         this.jj_consume_token(36);
         switch(this.jj_nt.kind) {
         case 19:
            this.jj_consume_token(19);
            edge.setProperty("select", this.skipTo(35, false));
            break;
         default:
            this.jj_la1[15] = this.jj_gen;
         }

         switch(this.jj_nt.kind) {
         case 20:
            this.jj_consume_token(20);
            edge.setProperty("guard", this.skipTo(35, false));
            break;
         default:
            this.jj_la1[16] = this.jj_gen;
         }

         switch(this.jj_nt.kind) {
         case 21:
            this.jj_consume_token(21);
            edge.setProperty("synchronisation", this.skipTo(35, false));
            break;
         default:
            this.jj_la1[17] = this.jj_gen;
         }

         switch(this.jj_nt.kind) {
         case 22:
            this.jj_consume_token(22);
            edge.setProperty("assignment", this.skipTo(35, false));
            break;
         default:
            this.jj_la1[18] = this.jj_gen;
         }

         switch(this.jj_nt.kind) {
         case 23:
            this.jj_consume_token(23);
            edge.setProperty("probability", this.skipTo(35, false));
            break;
         default:
            this.jj_la1[19] = this.jj_gen;
         }

         this.jj_consume_token(37);
      } catch (ParseException var8) {
         this.skipTo(37, false);
      }

   }

   private static void jj_la1_init_0() {
      jj_la1_0 = new int[]{1073741824, 32768, 65536, 0, 262144, 0, 8192, 16384, 131072, 0, 0, 0, 0, 0, 0, 524288, 1048576, 2097152, 4194304, 8388608};
   }

   private static void jj_la1_init_1() {
      jj_la1_1 = new int[]{0, 0, 0, 4, 0, 4, 0, 0, 0, 4, 4, 16, 4, 2097152, 49152, 0, 0, 0, 0, 0};
   }

   public XTAReader(InputStream stream) {
      this(stream, (String)null);
   }

   public XTAReader(InputStream stream, String encoding) {
      this.errors = null;
      this.jj_la1 = new int[20];
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;

      try {
         this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }

      this.token_source = new XTAReaderTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.token.next = this.jj_nt = this.token_source.getNextToken();
      this.jj_gen = 0;

      for(int i = 0; i < 20; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public void ReInit(InputStream stream) {
      this.ReInit(stream, (String)null);
   }

   public void ReInit(InputStream stream, String encoding) {
      try {
         this.jj_input_stream.ReInit(stream, encoding, 1, 1);
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }

      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.token.next = this.jj_nt = this.token_source.getNextToken();
      this.jj_gen = 0;

      for(int i = 0; i < 20; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public XTAReader(Reader stream) {
      this.errors = null;
      this.jj_la1 = new int[20];
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;
      this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
      this.token_source = new XTAReaderTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.token.next = this.jj_nt = this.token_source.getNextToken();
      this.jj_gen = 0;

      for(int i = 0; i < 20; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public void ReInit(Reader stream) {
      this.jj_input_stream.ReInit((Reader)stream, 1, 1);
      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.token.next = this.jj_nt = this.token_source.getNextToken();
      this.jj_gen = 0;

      for(int i = 0; i < 20; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public XTAReader(XTAReaderTokenManager tm) {
      this.errors = null;
      this.jj_la1 = new int[20];
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;
      this.token_source = tm;
      this.token = new Token();
      this.token.next = this.jj_nt = this.token_source.getNextToken();
      this.jj_gen = 0;

      for(int i = 0; i < 20; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public void ReInit(XTAReaderTokenManager tm) {
      this.token_source = tm;
      this.token = new Token();
      this.token.next = this.jj_nt = this.token_source.getNextToken();
      this.jj_gen = 0;

      for(int i = 0; i < 20; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   private Token jj_consume_token(int kind) throws ParseException {
      Token oldToken = this.token;
      if ((this.token = this.jj_nt).next != null) {
         this.jj_nt = this.jj_nt.next;
      } else {
         this.jj_nt = this.jj_nt.next = this.token_source.getNextToken();
      }

      if (this.token.kind == kind) {
         ++this.jj_gen;
         return this.token;
      } else {
         this.jj_nt = this.token;
         this.token = oldToken;
         this.jj_kind = kind;
         throw this.generateParseException();
      }
   }

   public final Token getNextToken() {
      if ((this.token = this.jj_nt).next != null) {
         this.jj_nt = this.jj_nt.next;
      } else {
         this.jj_nt = this.jj_nt.next = this.token_source.getNextToken();
      }

      ++this.jj_gen;
      return this.token;
   }

   public final Token getToken(int index) {
      Token t = this.token;

      for(int i = 0; i < index; ++i) {
         if (t.next != null) {
            t = t.next;
         } else {
            t = t.next = this.token_source.getNextToken();
         }
      }

      return t;
   }

   public ParseException generateParseException() {
      this.jj_expentries.clear();
      boolean[] la1tokens = new boolean[56];
      if (this.jj_kind >= 0) {
         la1tokens[this.jj_kind] = true;
         this.jj_kind = -1;
      }

      int i;
      int j;
      for(i = 0; i < 20; ++i) {
         if (this.jj_la1[i] == this.jj_gen) {
            for(j = 0; j < 32; ++j) {
               if ((jj_la1_0[i] & 1 << j) != 0) {
                  la1tokens[j] = true;
               }

               if ((jj_la1_1[i] & 1 << j) != 0) {
                  la1tokens[32 + j] = true;
               }
            }
         }
      }

      for(i = 0; i < 56; ++i) {
         if (la1tokens[i]) {
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
         }
      }

      int[][] exptokseq = new int[this.jj_expentries.size()][];

      for(j = 0; j < this.jj_expentries.size(); ++j) {
         exptokseq[j] = (int[])this.jj_expentries.get(j);
      }

      return new ParseException(this.token, exptokseq, tokenImage);
   }

   public final void enable_tracing() {
   }

   public final void disable_tracing() {
   }

   static {
      jj_la1_init_0();
      jj_la1_init_1();
   }

   interface TerminationCondition {
      boolean terminate();
   }
}
