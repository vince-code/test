package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.EventListener;
import com.uppaal.model.core2.Visitor;
import java.util.ArrayList;

public class Update extends AnchoredElement {
   InstanceLine anchor;
   public Condition anchoredToCondition;

   public Update(Element prototype) {
      super(prototype);
   }

   public InstanceLine getAnchor() {
      return this.anchor;
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitUpdate(this);
   }

   public Element getPrototypeFromParent(Element parent) {
      return (Element)parent.getPropertyValue("#update");
   }

   public String getFriendlyName() {
      return "update";
   }

   public void setAnchor(InstanceLine anchor) {
      InstanceLine old = this.anchor;
      this.anchor = anchor;
      this.fireAnchorChanged(old);
   }

   protected void fireAnchorChanged(InstanceLine oldAnchor) {
      if (this.getDocument() != null) {
         Object element = this;

         do {
            if (((Element)element).listeners != null) {
               EventListener[] var3 = (EventListener[])((Element)element).listeners.getListeners(EventListener.class);
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  EventListener l = var3[var5];
                  l.anchorChanged(this, oldAnchor, this.anchor);
               }
            }

            element = ((Element)element).prototype;
         } while(element != null);
      }

   }

   public ArrayList<InstanceLine> getAnchors() {
      ArrayList<InstanceLine> array = new ArrayList();
      array.add(this.getAnchor());
      return array;
   }

   public void setAnchoredToCondition(Condition condition) {
      this.anchoredToCondition = condition;
   }

   public Condition getAnchoredToCondition() {
      return this.anchoredToCondition;
   }

   public Element getLabel() {
      return this.getProperty("update");
   }

   public String getLabelValue() {
      return (String)this.getPropertyValue("update");
   }
}
