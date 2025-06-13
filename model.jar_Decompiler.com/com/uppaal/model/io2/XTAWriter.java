package com.uppaal.model.io2;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.AbstractVisitor;
import com.uppaal.model.core2.BranchPoint;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Location;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class XTAWriter extends AbstractVisitor {
   protected Map<Element, String> names = new HashMap();
   protected Writer writer;
   protected int level;
   protected boolean featuresSMC;
   static Pattern endsWithComment = Pattern.compile("(?s:.*)//.*");

   public XTAWriter(OutputStream stream) {
      this.writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
      this.level = 0;
   }

   private void newline(int delta) throws IOException {
      this.level += delta;
      this.writer.write(10);

      for(int i = 0; i < this.level; ++i) {
         this.writer.write("    ");
      }

   }

   public UGIWriter createUGIWriter(OutputStream stream) {
      return new UGIWriter(stream, this.names);
   }

   protected boolean isEmpty(String str) {
      return str == null || str.length() == 0;
   }

   protected boolean hasFlag(Element element, String property) {
      Object value = element.getPropertyValue(property);
      return value != null && (Boolean)value;
   }

   protected boolean writeNonEmptyProperty(Element element, String name, String format) throws IOException {
      String value = (String)element.getPropertyValue(name);
      if (!this.isEmpty(value)) {
         if (endsWithComment.matcher(value).matches()) {
            value = value + "\n";
         }

         this.writer.write(MessageFormat.format(format, value));
         return true;
      } else {
         return false;
      }
   }

   protected void writeNonEmptyProperty(Element element, String name) throws IOException {
      this.writeNonEmptyProperty(element, name, "{0}");
   }

   public void visitDocument(Document document) throws Exception {
      this.featuresSMC = false;
      document.accept(new AbstractVisitor() {
         protected void collect(Element element) {
            String name = (String)element.getPropertyValue("name");
            if (!XTAWriter.this.isEmpty(name)) {
               XTAWriter.this.names.put(element, name);
            }

         }

         public void visitLocation(Location location) throws Exception {
            this.collect(location);
         }

         public void visitBranchPoint(BranchPoint branchPoint) throws Exception {
            XTAWriter.this.featuresSMC = true;
            this.collect(branchPoint);
         }

         public void visitTemplate(AbstractTemplate template) throws Exception {
            this.collect(template);
            super.visitTemplate(template);
         }
      });
      document.accept(new AbstractVisitor() {
         protected int lCounter;
         protected int bCounter;
         protected Map<Element, String> localNames;

         public void visitLocation(Location location) throws Exception {
            String name = (String)location.getPropertyValue("name");
            if (XTAWriter.this.isEmpty(name)) {
               do {
                  int var10002 = this.lCounter++;
                  name = "L" + var10002;
               } while(this.localNames.containsValue(name));

               this.localNames.put(location, name);
            }

         }

         public void visitBranchPoint(BranchPoint branchPoint) throws Exception {
            XTAWriter.this.featuresSMC = true;
            String name = (String)branchPoint.getPropertyValue("name");
            if (XTAWriter.this.isEmpty(name)) {
               do {
                  int var10002 = this.bCounter++;
                  name = "B" + var10002;
               } while(this.localNames.containsValue(name));

               this.localNames.put(branchPoint, name);
            }

         }

         public void visitTemplate(AbstractTemplate template) throws Exception {
            this.localNames = new HashMap();
            this.lCounter = 0;
            this.bCounter = 0;
            super.visitTemplate(template);
            XTAWriter.this.names.putAll(this.localNames);
         }
      });
      this.writeNonEmptyProperty(document, "declaration");
      this.newline(0);
      super.visitDocument(document);
      this.writer.write(document.getPropertyValue("system").toString());
      this.writer.flush();
   }

   public void visitTemplate(AbstractTemplate template) throws Exception {
      this.newline(0);
      this.writer.write("process ");
      this.writer.write((String)this.names.get(template));
      this.writer.write("(");
      this.writeNonEmptyProperty(template, "parameter");
      this.writer.write(") {");
      this.newline(0);
      this.writeNonEmptyProperty(template, "declaration");
      this.newline(0);
      this.writer.write("state");
      this.newline(1);
      template.accept(new AbstractVisitor() {
         protected boolean first = true;

         public void visitLocation(Location location) throws Exception {
            if (this.first) {
               this.first = false;
            } else {
               XTAWriter.this.writer.write(",");
               XTAWriter.this.newline(0);
            }

            if (location.isPropertyLocal("comments")) {
               String comments = location.getPropertyValue("comments").toString().trim();
               if (comments.length() > 0) {
                  XTAWriter.this.writer.write("/** ");
                  XTAWriter.this.writer.write(comments);
                  XTAWriter.this.writer.write(" */");
                  XTAWriter.this.newline(0);
               }
            }

            XTAWriter.this.writer.write((String)XTAWriter.this.names.get(location));
            if (XTAWriter.this.writeNonEmptyProperty(location, "invariant", " '{' {0}")) {
               if (XTAWriter.this.writeNonEmptyProperty(location, "exponentialrate", " ; {0}")) {
                  XTAWriter.this.featuresSMC = true;
               }

               XTAWriter.this.writer.write(" }");
            } else if (XTAWriter.this.writeNonEmptyProperty(location, "exponentialrate", " '{' ; {0} '}'")) {
               XTAWriter.this.featuresSMC = true;
            }

         }
      });
      this.writer.write(";");
      this.newline(-1);
      template.accept(new XTAWriter.StateFlagGenerator("branchpoint", "branchpoint"));
      template.accept(new XTAWriter.StateFlagGenerator("committed", "commit"));
      template.accept(new XTAWriter.StateFlagGenerator("urgent", "urgent"));
      template.accept(new XTAWriter.StateFlagGenerator("init", "init"));
      template.accept(new AbstractVisitor() {
         protected boolean first = true;

         public void visitTemplate(AbstractTemplate template) throws Exception {
            super.visitTemplate(template);
            if (!this.first) {
               XTAWriter.this.writer.write(";");
               XTAWriter.this.newline(-1);
            }

         }

         public void visitEdge(Edge edge) throws Exception {
            if (this.first) {
               this.first = false;
               XTAWriter.this.writer.write("trans");
               XTAWriter.this.newline(1);
            } else {
               XTAWriter.this.writer.write(",");
               XTAWriter.this.newline(0);
            }

            if (edge.isPropertyLocal("comments")) {
               String comments = edge.getPropertyValue("comments").toString().trim();
               if (comments.length() > 0) {
                  XTAWriter.this.writer.write("/** ");
                  XTAWriter.this.writer.write(comments);
                  XTAWriter.this.writer.write(" */");
                  XTAWriter.this.newline(0);
               }
            }

            XTAWriter.this.writer.write((String)XTAWriter.this.names.get(edge.getSource()));
            if (edge.hasFlag("controllable")) {
               XTAWriter.this.writer.write(" -> ");
            } else {
               XTAWriter.this.writer.write(" -u-> ");
            }

            XTAWriter.this.writer.write((String)XTAWriter.this.names.get(edge.getTarget()));
            XTAWriter.this.writer.write(" { ");
            XTAWriter.this.writeNonEmptyProperty(edge, "select", "select {0}; ");
            XTAWriter.this.writeNonEmptyProperty(edge, "guard", "guard {0}; ");
            XTAWriter.this.writeNonEmptyProperty(edge, "synchronisation", "sync {0}; ");
            XTAWriter.this.writeNonEmptyProperty(edge, "assignment", "assign {0}; ");
            if (XTAWriter.this.writeNonEmptyProperty(edge, "probability", "probability {0}; ")) {
               XTAWriter.this.featuresSMC = true;
            }

            XTAWriter.this.writer.write("}");
         }
      });
      this.writer.write("}");
      this.newline(0);
   }

   class StateFlagGenerator extends AbstractVisitor {
      protected boolean first = true;
      protected String property;
      protected String flag;

      StateFlagGenerator(String property, String flag) {
         this.property = property;
         this.flag = flag;
      }

      public void visitTemplate(AbstractTemplate template) throws Exception {
         super.visitTemplate(template);
         if (!this.first) {
            XTAWriter.this.writer.write(";");
            XTAWriter.this.newline(-1);
         }

      }

      public void visitLocation(Location location) throws Exception {
         if (XTAWriter.this.hasFlag(location, this.property)) {
            if (this.first) {
               this.first = false;
               XTAWriter.this.writer.write(this.flag);
               XTAWriter.this.newline(1);
            } else {
               XTAWriter.this.writer.write(",");
               XTAWriter.this.newline(0);
            }

            XTAWriter.this.writer.write((String)XTAWriter.this.names.get(location));
         }

      }

      public void visitBranchPoint(BranchPoint branchPoint) throws Exception {
         XTAWriter.this.featuresSMC = true;
         if ("branchpoint".equals(this.flag)) {
            if (this.first) {
               this.first = false;
               XTAWriter.this.writer.write(this.flag);
               XTAWriter.this.newline(1);
            } else {
               XTAWriter.this.writer.write(",");
               XTAWriter.this.newline(0);
            }

            XTAWriter.this.writer.write((String)XTAWriter.this.names.get(branchPoint));
         }

      }
   }
}
