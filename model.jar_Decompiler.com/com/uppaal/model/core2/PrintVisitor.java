package com.uppaal.model.core2;

import com.uppaal.model.core2.lsc.LscTemplate;
import java.util.Iterator;
import java.util.Map.Entry;

public class PrintVisitor extends AbstractVisitor {
   protected int indentation = 0;

   protected void indent() {
      for(int i = 0; i < this.indentation; ++i) {
         System.out.print("    ");
      }

   }

   protected void print(String s) {
      this.indent();
      System.out.println(s);
   }

   public void visitElement(Element e) throws Exception {
      Iterator var2 = e.getProperties().entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, Property> entry = (Entry)var2.next();
         String var10001 = (String)entry.getKey();
         this.print(var10001 + ": " + ((Property)entry.getValue()).getValue());
         ((Property)entry.getValue()).accept(this);
      }

   }

   public void visitTemplate(AbstractTemplate t) throws Exception {
      if (t instanceof LscTemplate) {
         this.print("LscTemplate:");
      } else {
         this.print("Template:");
      }

      ++this.indentation;
      super.visitTemplate(t);
      --this.indentation;
   }

   public void visitLocation(Location l) throws Exception {
      this.print("Location:");
      ++this.indentation;
      super.visitLocation(l);
      --this.indentation;
   }

   public void visitBranchPoint(BranchPoint b) throws Exception {
      this.print("BranchPoint:");
      ++this.indentation;
      super.visitBranchPoint(b);
      --this.indentation;
   }

   public void visitEdge(Edge e) throws Exception {
      Object var10001 = e.getSource().getPropertyValue("name");
      this.print("Edge " + var10001 + " -> " + e.getTarget().getPropertyValue("name"));
      ++this.indentation;
      super.visitEdge(e);
      --this.indentation;
   }

   public void visitNail(Nail n) throws Exception {
      this.print("Via");
      ++this.indentation;
      super.visitNail(n);
      --this.indentation;
   }
}
