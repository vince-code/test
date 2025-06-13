package com.uppaal.model;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.AbstractVisitor;
import com.uppaal.model.core2.Command;
import com.uppaal.model.core2.CompoundCommand;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Property;
import com.uppaal.model.core2.SetPropertyCommand;
import com.uppaal.model.core2.lsc.Condition;
import com.uppaal.model.core2.lsc.Update;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertSyntaxVisitor extends AbstractVisitor {
   private final ArrayList<Command> commands = new ArrayList();

   private void set(Element element, String property, String value) {
      this.commands.add(new SetPropertyCommand(element, property, value));
   }

   private String fixDeclarations(String s) {
      s = s.replaceAll(":=", "=");
      s = s.replaceAll("const\\s+(\\w+)\\s*((\\[[^\\]]*\\])*)\\s*", "const int $1$2 = ");
      int i = 0;

      while((i = s.indexOf("const int", i)) > -1) {
         for(int cnt = 0; i < s.length() && s.charAt(i) != ';'; ++i) {
            switch(s.charAt(i)) {
            case ',':
               if (cnt == 0) {
                  ++i;
                  StringBuffer b = new StringBuffer();
                  Matcher m = Pattern.compile("(\\s*(\\w+)+)\\s*((\\[[^\\]]*\\])*)\\s*").matcher(s);
                  m.find(i);
                  m.appendReplacement(b, "$1$3 = ");
                  m.appendTail(b);
                  s = b.toString();
               }
               break;
            case '[':
            case '{':
               ++cnt;
               break;
            case ']':
            case '}':
               --cnt;
            }
         }
      }

      return s;
   }

   public void visitDocument(Document document) throws Exception {
      Property declaration = document.getLocalProperty("declaration");
      if (declaration != null) {
         String value = (String)declaration.getValue();
         this.set(document, "declaration", this.fixDeclarations(value));
      }

      super.visitDocument(document);
      Property system = document.getLocalProperty("system");
      if (system != null) {
         String value = (String)system.getValue();
         this.set(document, "system", this.fixDeclarations(value));
      }

      document.getCommandManager().execute((Command)(new CompoundCommand((Command[])this.commands.toArray())));
   }

   public void visitTemplate(AbstractTemplate template) throws Exception {
      Property declaration = template.getLocalProperty("declaration");
      if (declaration != null) {
         String value = (String)declaration.getValue();
         this.set(template, "declaration", this.fixDeclarations(value));
      }

      Property parameter = template.getLocalProperty("parameter");
      if (parameter != null) {
         String value = (String)parameter.getValue();

         String s;
         do {
            s = value;
            value = value.replaceFirst("((\\w+(\\s*\\[[^\\]]*\\]\\s*|\\s+))+)(\\w+(\\[[^\\]]*\\])*)\\s*,\\s*", "$1$4; $1");
         } while(!s.equals(value));

         value = value.replaceAll("(int(\\[[^\\]]*\\])?)", "$1&");
         value = value.replaceAll("(^|\\W)const\\s+", "$1const int ");
         value = value.replaceAll("(^|\\W)clock\\s+", "$1clock &");
         value = value.replaceAll("(^|\\W)chan\\s+", "$1chan &");
         value = value.replaceAll(";", ",");
         this.set(template, "parameter", value);
      }

      super.visitTemplate(template);
   }

   public void visitLocation(Location location) throws Exception {
      Property invariant = location.getLocalProperty("invariant");
      if (invariant != null) {
         String value = (String)invariant.getValue();
         this.set(location, "invariant", value.replaceAll("\\s?,\\s?", " and "));
      }

   }

   public void visitEdge(Edge edge) throws Exception {
      Property guard = edge.getLocalProperty("guard");
      if (guard != null) {
         String value = (String)guard.getValue();
         this.set(edge, "guard", value.replaceAll("\\s?,\\s?", " and "));
      }

      Property assignment = edge.getLocalProperty("assignment");
      if (assignment != null) {
         String value = (String)assignment.getValue();
         this.set(edge, "assignment", value.replaceAll(":=", "="));
      }

   }

   public void visitCondition(Condition condition) {
      Property label = condition.getLocalProperty("condition");
      if (label != null) {
         String value = (String)label.getValue();
         this.set(condition, "condition", value.replaceAll("\\s?,\\s?", " and "));
      }

   }

   public void visitUpdate(Update update) {
      Property label = update.getLocalProperty("update");
      if (label != null) {
         String value = (String)label.getValue();
         this.set(update, "update", value.replaceAll(":=", "="));
      }

   }
}
