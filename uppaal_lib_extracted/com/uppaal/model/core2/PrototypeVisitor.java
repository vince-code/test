package com.uppaal.model.core2;

public class PrototypeVisitor extends AbstractVisitor {
   private void visitPrototype(Element element, String property) throws Exception {
      Element prototype = (Element)element.getPropertyValue(property);
      if (prototype != null) {
         prototype.accept(this);
      }

   }

   public void visitElement(Element element) throws Exception {
      this.visitPrototype(element, "#template");
      this.visitPrototype(element, "#location");
      this.visitPrototype(element, "#branchpoint");
      this.visitPrototype(element, "#edge");
      this.visitPrototype(element, "#nail");
      this.visitPrototype(element, "#lscTemplate");
      this.visitPrototype(element, "#instance");
      this.visitPrototype(element, "#message");
      this.visitPrototype(element, "#condition");
      this.visitPrototype(element, "#update");
      this.visitPrototype(element, "#prechart");
      this.visitPrototype(element, "#cut");
      super.visitElement(element);
   }
}
