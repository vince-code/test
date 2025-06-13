package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.EventListener;
import com.uppaal.model.core2.Visitor;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

public class Condition extends AnchoredElement {
   ArrayList<InstanceLine> anchors = new ArrayList();

   public Condition(Element prototype) {
      super(prototype);
   }

   public InstanceLine getAnchor() {
      return this.anchors.size() > 0 ? (InstanceLine)this.anchors.get(0) : null;
   }

   public void setAnchor(InstanceLine anchor) {
      InstanceLine old = this.getAnchor();
      if (this.anchors.isEmpty()) {
         this.anchors.add(anchor);
      } else {
         this.anchors.set(0, anchor);
      }

      this.fireAnchorChanged(old);
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitCondition(this);
   }

   public Element getPrototypeFromParent(Element parent) {
      return (Element)parent.getPropertyValue("#condition");
   }

   public String getFriendlyName() {
      return "condition";
   }

   public int getWidth() {
      InstanceLine anchor0 = this.anchors.isEmpty() ? null : (InstanceLine)this.anchors.get(0);
      int min = anchor0 == null ? this.getX() : anchor0.getX();
      int max = min;
      Iterator var4 = this.anchors.iterator();

      while(var4.hasNext()) {
         InstanceLine anchor = (InstanceLine)var4.next();
         int x = anchor == null ? this.getX() : anchor.getX();
         if (x > max) {
            max = x;
         }

         if (x < min) {
            min = x;
         }
      }

      return Math.max(max - min + 20, this.getWidth((Graphics2D)null));
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
                  l.anchorChanged(this, oldAnchor, this.getAnchor());
               }
            }

            element = ((Element)element).prototype;
         } while(element != null);
      }

   }

   public ArrayList<InstanceLine> getAnchors() {
      return this.anchors;
   }

   public void addAnchor(InstanceLine anchor) {
      if (!this.anchors.contains(anchor)) {
         if (anchor != null) {
            int d = 0;
            ArrayList<Integer> array = new ArrayList();

            for(int i = 0; i < this.anchors.size(); ++i) {
               if (this.anchors.get(i) == null) {
                  array.add(i + d);
                  --d;
               }
            }

            Iterator var6 = array.iterator();

            while(var6.hasNext()) {
               int i = (Integer)var6.next();
               this.anchors.remove(i);
            }
         }

         this.anchors.add(anchor);
      }
   }

   public void removeAnchor(InstanceLine anchor) {
      this.anchors.remove(anchor);
   }

   public boolean isHot() {
      return (Boolean)this.getPropertyValue("hot");
   }

   public void setAnchors(ArrayList<InstanceLine> anchors) {
      this.anchors = anchors;
   }

   public Element getLabel() {
      return this.getProperty("condition");
   }

   public String getLabelValue() {
      return (String)this.getPropertyValue("condition");
   }
}
