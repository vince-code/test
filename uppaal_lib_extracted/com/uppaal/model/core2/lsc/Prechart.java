package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Visitor;
import java.util.ArrayList;

public class Prechart extends LscElement {
   protected ArrayList<InstanceLine> instances = new ArrayList();

   public Prechart(Element prototype) {
      super(prototype);
   }

   public void add(InstanceLine instance) {
      if (!this.instances.contains(instance)) {
         this.instances.add(instance);
      }

   }

   public void remove(InstanceLine instance) {
      this.instances.remove(instance);
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitPrechart(this);
   }

   public Element getPrototypeFromParent(Element parent) {
      return (Element)parent.getPropertyValue("#prechart");
   }

   public String getFriendlyName() {
      return "prechart";
   }

   public ArrayList<InstanceLine> getInstances() {
      return this.instances;
   }
}
