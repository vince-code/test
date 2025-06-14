package com.uppaal.model.core2;

public class Edge extends Node {
   protected AbstractLocation source;
   protected AbstractLocation target;

   public Edge(Element prototype) {
      super(prototype);
   }

   public AbstractLocation getSource() {
      return this.source;
   }

   public AbstractLocation getTarget() {
      return this.target;
   }

   public Nail getNails() {
      return (Nail)this.first;
   }

   public void setSource(AbstractLocation source) {
      AbstractLocation old = this.source;
      this.source = source;
      this.fireSourceChanged(old);
   }

   public void setTarget(AbstractLocation target) {
      AbstractLocation old = this.target;
      this.target = target;
      this.fireTargetChanged(old);
   }

   public Nail createNail() {
      return new Nail((Element)this.getPropertyValue("#nail"));
   }

   public Edge addNail(int x, int y) {
      Nail nail = this.createNail();
      this.insert(nail, this.getLast());
      nail.setXY(x, y);
      return this;
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitEdge(this);
   }

   void fireSourceChanged(AbstractLocation oldSource) {
      if (this.getDocument() != null) {
         Object element = this;

         do {
            if (((Element)element).listeners != null) {
               EventListener[] var3 = (EventListener[])((Element)element).listeners.getListeners(EventListener.class);
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  EventListener l = var3[var5];
                  l.edgeSourceChanged(this, oldSource, this.source);
               }
            }

            element = ((Element)element).prototype;
         } while(element != null);
      }

   }

   void fireTargetChanged(AbstractLocation oldTarget) {
      if (this.getDocument() != null) {
         Object element = this;

         do {
            if (((Element)element).listeners != null) {
               EventListener[] var3 = (EventListener[])((Element)element).listeners.getListeners(EventListener.class);
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  EventListener l = var3[var5];
                  l.edgeTargetChanged(this, oldTarget, this.target);
               }
            }

            element = ((Element)element).prototype;
         } while(element != null);
      }

   }

   public Element getPrototypeFromParent(Element parent) {
      return (Element)parent.getPropertyValue("#edge");
   }

   public String getFriendlyName() {
      return this.getName();
   }

   public String getName() {
      String var10000 = this.getSource().getName();
      return var10000 + "→" + this.getTarget().getName();
   }
}
