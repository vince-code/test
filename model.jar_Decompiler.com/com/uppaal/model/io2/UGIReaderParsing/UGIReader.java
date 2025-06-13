package com.uppaal.model.io2.UGIReaderParsing;

import com.uppaal.model.core2.AbstractLocation;
import com.uppaal.model.core2.AbstractVisitor;
import com.uppaal.model.core2.BranchPoint;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Node;
import com.uppaal.model.io2.EdgeId;
import com.uppaal.model.io2.RelToAbsVisitor;
import java.awt.Color;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UGIReader implements UGIReaderConstants {
   private Map<String, AbstractLocation> locations;
   private Map<EdgeId, Edge> edges;
   public UGIReaderTokenManager token_source;
   SimpleCharStream jj_input_stream;
   public Token token;
   public Token jj_nt;
   private int jj_ntk;
   private int jj_gen;
   private final int[] jj_la1;
   private static int[] jj_la1_0;
   private static int[] jj_la1_1;
   private List<int[]> jj_expentries;
   private int[] jj_expentry;
   private int jj_kind;

   public void parse(Document document) throws Exception {
      this.graphInfo(document);
      document.accept(new RelToAbsVisitor());
   }

   private void setPosition(Element element, int x, int y) {
      if (element != null) {
         element.setProperty("x", x);
         element.setProperty("y", y);
      }

   }

   public final void graphInfo(Document document) throws ParseException, Exception {
      switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
      case 12:
         this.importGI();
         break;
      default:
         this.jj_la1[0] = this.jj_gen;
      }

      switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
      case 11:
         this.globalDeclGI();
         break;
      default:
         this.jj_la1[1] = this.jj_gen;
      }

      switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
      case 13:
         this.procAssignGI();
         break;
      default:
         this.jj_la1[2] = this.jj_gen;
      }

      switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
      case 14:
         this.systemDefGI();
         break;
      default:
         this.jj_la1[3] = this.jj_gen;
      }

      while(true) {
         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 5:
            this.PTGraphInfo(document);
            break;
         default:
            this.jj_la1[4] = this.jj_gen;
            this.jj_consume_token(0);
            return;
         }
      }
   }

   public final void importGI() throws ParseException {
      this.jj_consume_token(12);
      this.singleCoord((Element)null);
   }

   public final void globalDeclGI() throws ParseException {
      this.jj_consume_token(11);
      this.singleCoord((Element)null);
   }

   public final void procAssignGI() throws ParseException {
      this.jj_consume_token(13);
      this.singleCoord((Element)null);
   }

   public final void systemDefGI() throws ParseException {
      this.jj_consume_token(14);
      this.singleCoord((Element)null);
   }

   public final void PTGraphInfo(Document document) throws ParseException, Exception {
      this.jj_consume_token(5);
      Token t = this.jj_consume_token(33);

      Node node;
      for(node = document.getFirst(); node != null && !t.image.equals(node.getPropertyValue("name")); node = node.getNext()) {
      }

      if (node == null) {
         throw new ParseException("Template " + t.image + "undeclared, but used in ugi file.");
      } else {
         this.locations.clear();
         this.edges.clear();
         node.accept(new AbstractVisitor() {
            public void visitLocation(Location location) throws Exception {
               String name = (String)location.getPropertyValue("name");
               if (name != null) {
                  UGIReader.this.locations.put(name, location);
               }

            }

            public void visitBranchPoint(BranchPoint branchPoint) throws Exception {
               String name = (String)branchPoint.getPropertyValue("name");
               if (name != null) {
                  UGIReader.this.locations.put(name, branchPoint);
               }

            }

            public void visitEdge(Edge edge) throws Exception {
               String src = edge.getSource().getPropertyValue("name").toString();
               String dst = edge.getTarget().getPropertyValue("name").toString();
               EdgeId id = new EdgeId(src, dst, 1);

               while(UGIReader.this.edges.containsKey(id)) {
                  id.increment();
               }

               UGIReader.this.edges.put(id, edge);
            }
         });
         this.jj_consume_token(25);
         this.jj_consume_token(36);
         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 9:
            this.nameGraphInfo();
            break;
         default:
            this.jj_la1[5] = this.jj_gen;
         }

         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 10:
            this.paramListGraphInfo();
            break;
         default:
            this.jj_la1[6] = this.jj_gen;
         }

         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 15:
            this.localDeclGraphInfo();
            break;
         default:
            this.jj_la1[7] = this.jj_gen;
         }

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 6:
            case 7:
            case 8:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 26:
            case 27:
               this.graphInfoItem();
               break;
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 25:
            default:
               this.jj_la1[8] = this.jj_gen;
               this.jj_consume_token(37);
               return;
            }
         }
      }
   }

   public final void nameGraphInfo() throws ParseException, ParseException {
      this.jj_consume_token(9);
      this.singleCoord((Element)null);
   }

   public final void paramListGraphInfo() throws ParseException, ParseException {
      this.jj_consume_token(10);
      this.singleCoord((Element)null);
   }

   public final void localDeclGraphInfo() throws ParseException {
      this.jj_consume_token(15);
      this.singleCoord((Element)null);
   }

   public final void graphInfoItem() throws ParseException, ParseException {
      AbstractLocation l;
      Edge e;
      switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
      case 6:
         this.jj_consume_token(6);
         l = this.locIdent();
         this.singleCoord(l);
         break;
      case 7:
         this.jj_consume_token(7);
         l = this.locIdent();
         this.singleCoord(l.getProperty("name"));
         break;
      case 8:
         this.jj_consume_token(8);
         l = this.locIdent();
         this.singleCoord(l);
         break;
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 25:
      default:
         this.jj_la1[9] = this.jj_gen;
         this.jj_consume_token(-1);
         throw new ParseException();
      case 17:
         this.jj_consume_token(17);
         l = this.locIdent();
         this.singleCoord(l.getProperty("invariant"));
         break;
      case 18:
         this.jj_consume_token(18);
         l = this.locIdent();
         this.singleCoord(l.getProperty("exponentialrate"));
         break;
      case 19:
         this.jj_consume_token(19);
         e = this.transIdent();
         this.nails(e);
         break;
      case 20:
         this.jj_consume_token(20);
         e = this.transIdent();
         this.singleCoord(e.getProperty("select"));
         break;
      case 21:
         this.jj_consume_token(21);
         e = this.transIdent();
         this.singleCoord(e.getProperty("guard"));
         break;
      case 22:
         this.jj_consume_token(22);
         e = this.transIdent();
         this.singleCoord(e.getProperty("synchronisation"));
         break;
      case 23:
         this.jj_consume_token(23);
         e = this.transIdent();
         this.singleCoord(e.getProperty("assignment"));
         break;
      case 24:
         this.jj_consume_token(24);
         e = this.transIdent();
         this.singleCoord(e.getProperty("probability"));
         break;
      case 26:
         this.jj_consume_token(26);
         l = this.locIdent();
         this.color(l);
         this.jj_consume_token(29);
         break;
      case 27:
         this.jj_consume_token(27);
         e = this.transIdent();
         this.color(e);
         this.jj_consume_token(29);
      }

   }

   public final AbstractLocation locIdent() throws ParseException, ParseException {
      Token t = this.jj_consume_token(33);
      AbstractLocation location = (AbstractLocation)this.locations.get(t.image);
      if (location == null) {
         throw new ParseException("Location " + t.image + "undeclared, but used in ugi file.");
      } else {
         return location;
      }
   }

   public final Edge transIdent() throws ParseException, ParseException {
      Token source = this.jj_consume_token(33);
      Token dest = this.jj_consume_token(33);
      Token no = this.jj_consume_token(34);
      EdgeId id = new EdgeId(source.image, dest.image, Integer.parseInt(no.image));
      Edge edge = (Edge)this.edges.get(id);
      if (edge == null) {
         throw new ParseException("Transition no. " + no.image + " from '" + source.image + "' to '" + dest.image + "' undeclared, but used in ugi file.");
      } else {
         return edge;
      }
   }

   public final void color(Element element) throws ParseException {
      Token color = this.jj_consume_token(35);
      element.setProperty("color", Color.decode(color.image));
   }

   public final void nails(Edge edge) throws ParseException {
      this.jj_consume_token(31);
      int x = this.integer();
      this.jj_consume_token(28);
      int y = this.integer();
      this.jj_consume_token(32);
      this.setPosition(edge.insert(edge.createNail(), edge.getLast()), x, y);

      while(true) {
         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 28:
            this.jj_consume_token(28);
            this.jj_consume_token(31);
            x = this.integer();
            this.jj_consume_token(28);
            y = this.integer();
            this.jj_consume_token(32);
            this.setPosition(edge.insert(edge.createNail(), edge.getLast()), x, y);
            break;
         default:
            this.jj_la1[10] = this.jj_gen;
            this.jj_consume_token(29);
            return;
         }
      }
   }

   public final void singleCoord(Element element) throws ParseException {
      this.jj_consume_token(31);
      int x = this.integer();
      this.jj_consume_token(28);
      int y = this.integer();
      this.jj_consume_token(32);
      this.jj_consume_token(29);
      this.setPosition(element, x, y);
   }

   public final int integer() throws ParseException {
      Token t;
      switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
      case 34:
         t = this.jj_consume_token(34);
         return Integer.parseInt(t.image);
      case 38:
         this.jj_consume_token(38);
         t = this.jj_consume_token(34);
         return -Integer.parseInt(t.image);
      case 39:
         this.jj_consume_token(39);
         t = this.jj_consume_token(34);
         return Integer.parseInt(t.image);
      default:
         this.jj_la1[11] = this.jj_gen;
         this.jj_consume_token(-1);
         throw new ParseException();
      }
   }

   private static void jj_la1_init_0() {
      jj_la1_0 = new int[]{4096, 2048, 8192, 16384, 32, 512, 1024, 32768, 234750400, 234750400, 268435456, 0};
   }

   private static void jj_la1_init_1() {
      jj_la1_1 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 196};
   }

   public UGIReader(InputStream stream) {
      this(stream, (String)null);
   }

   public UGIReader(InputStream stream, String encoding) {
      this.locations = new HashMap();
      this.edges = new HashMap();
      this.jj_la1 = new int[12];
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;

      try {
         this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }

      this.token_source = new UGIReaderTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      for(int i = 0; i < 12; ++i) {
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
      this.jj_ntk = -1;
      this.jj_gen = 0;

      for(int i = 0; i < 12; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public UGIReader(Reader stream) {
      this.locations = new HashMap();
      this.edges = new HashMap();
      this.jj_la1 = new int[12];
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;
      this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
      this.token_source = new UGIReaderTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      for(int i = 0; i < 12; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public void ReInit(Reader stream) {
      this.jj_input_stream.ReInit((Reader)stream, 1, 1);
      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      for(int i = 0; i < 12; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public UGIReader(UGIReaderTokenManager tm) {
      this.locations = new HashMap();
      this.edges = new HashMap();
      this.jj_la1 = new int[12];
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;
      this.token_source = tm;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      for(int i = 0; i < 12; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   public void ReInit(UGIReaderTokenManager tm) {
      this.token_source = tm;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      for(int i = 0; i < 12; ++i) {
         this.jj_la1[i] = -1;
      }

   }

   private Token jj_consume_token(int kind) throws ParseException {
      Token oldToken;
      if ((oldToken = this.token).next != null) {
         this.token = this.token.next;
      } else {
         this.token = this.token.next = this.token_source.getNextToken();
      }

      this.jj_ntk = -1;
      if (this.token.kind == kind) {
         ++this.jj_gen;
         return this.token;
      } else {
         this.token = oldToken;
         this.jj_kind = kind;
         throw this.generateParseException();
      }
   }

   public final Token getNextToken() {
      if (this.token.next != null) {
         this.token = this.token.next;
      } else {
         this.token = this.token.next = this.token_source.getNextToken();
      }

      this.jj_ntk = -1;
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

   private int jj_ntk() {
      return (this.jj_nt = this.token.next) == null ? (this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind) : (this.jj_ntk = this.jj_nt.kind);
   }

   public ParseException generateParseException() {
      this.jj_expentries.clear();
      boolean[] la1tokens = new boolean[40];
      if (this.jj_kind >= 0) {
         la1tokens[this.jj_kind] = true;
         this.jj_kind = -1;
      }

      int i;
      int j;
      for(i = 0; i < 12; ++i) {
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

      for(i = 0; i < 40; ++i) {
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
}
