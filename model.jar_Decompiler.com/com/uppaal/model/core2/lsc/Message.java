package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.EventListener;
import com.uppaal.model.core2.Visitor;
import java.util.ArrayList;

public class Message extends LscElement {
   protected InstanceLine source;
   protected InstanceLine target;

   public Message(Element prototype) {
      super(prototype);
   }

   public InstanceLine getSource() {
      return this.source;
   }

   public InstanceLine getTarget() {
      return this.target;
   }

   public void setSource(InstanceLine source) {
      InstanceLine old = this.source;
      this.source = source;
      this.fireSourceChanged(old);
   }

   void fireSourceChanged(InstanceLine oldSource) {
      if (this.getDocument() != null) {
         Object element = this;

         do {
            if (((Element)element).listeners != null) {
               EventListener[] var3 = (EventListener[])((Element)element).listeners.getListeners(EventListener.class);
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  EventListener l = var3[var5];
                  l.messageSourceChanged(this, oldSource, this.source);
               }
            }

            element = ((Element)element).prototype;
         } while(element != null);
      }

   }

   public void setTarget(InstanceLine target) {
      InstanceLine old = this.target;
      this.target = target;
      this.fireTargetChanged(old);
   }

   void fireTargetChanged(InstanceLine oldTarget) {
      if (this.getDocument() != null) {
         Object element = this;

         do {
            if (((Element)element).listeners != null) {
               EventListener[] var3 = (EventListener[])((Element)element).listeners.getListeners(EventListener.class);
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  EventListener l = var3[var5];
                  l.messageTargetChanged(this, oldTarget, this.target);
               }
            }

            element = ((Element)element).prototype;
         } while(element != null);
      }

   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitMessage(this);
   }

   public ArrayList<InstanceLine> getAnchors() {
      ArrayList<InstanceLine> array = new ArrayList();
      array.add(this.getSource());
      if (this.getSource() != this.getTarget()) {
         array.add(this.getTarget());
      }

      return array;
   }

   public Element getPrototypeFromParent(Element parent) {
      return (Element)parent.getPropertyValue("#message");
   }

   public String getFriendlyName() {
      return "message";
   }

   public String getLabelValue() {
      return (String)this.getPropertyValue("message");
   }
}
