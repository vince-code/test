package com.uppaal.model.core2;

public class BranchPoint extends AbstractLocation {
   public BranchPoint(Element prototype) {
      super(prototype);
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitBranchPoint(this);
   }

   public Element getPrototypeFromParent(Element parent) {
      return (Element)parent.getPropertyValue("#branchpoint");
   }

   public String getFriendlyName() {
      return this.getName();
   }

   public String getName() {
      String name = (String)this.getPropertyValue("name");
      return name != null && !name.trim().isEmpty() ? name : "∘";
   }
}
