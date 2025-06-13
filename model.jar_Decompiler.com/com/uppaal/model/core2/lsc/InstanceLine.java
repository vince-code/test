package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Visitor;
import java.util.ArrayList;

public class InstanceLine extends LscElement implements LscConstants {
   public InstanceLine(Element prototype) {
      super(prototype);
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitInstanceLine(this);
   }

   public Element getPrototypeFromParent(Element parent) {
      return (Element)parent.getPropertyValue("#instance");
   }

   public String getFriendlyName() {
      return "instanceline";
   }

   public Integer getLength() {
      return (Integer)this.getTemplate().getPropertyValue("length");
   }

   public void setLength(int length, ViewWorkAround view) {
      ((LscTemplate)this.getTemplate()).setLength(length, view);
   }

   public int getYFoot() {
      return this.getLength() + 10 + 2;
   }

   public boolean isAnchorOf(Element element) {
      if (!(element instanceof AnchoredElement) && !(element instanceof Message)) {
         return false;
      } else if (element instanceof AnchoredElement) {
         ArrayList<InstanceLine> anchors = ((LscElement)element).getAnchors();
         return anchors.contains(this);
      } else {
         return ((Message)element).getSource() == this || ((Message)element).getTarget() == this;
      }
   }
}
