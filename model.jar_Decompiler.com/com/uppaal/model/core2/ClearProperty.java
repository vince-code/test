package com.uppaal.model.core2;

public class ClearProperty extends AbstractVisitor {
   private String propertyName;

   public ClearProperty(String propertyName) {
      this.propertyName = propertyName;
   }

   public void visitElement(Element element) throws Exception {
      assert element != null;

      element.setProperty(this.propertyName, (Object)null);
      super.visitElement(element);
   }
}
