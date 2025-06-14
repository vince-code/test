package com.uppaal.model.core2;

import com.uppaal.model.core2.lsc.Message;
import java.awt.Color;
import java.awt.Font;
import java.util.Iterator;
import java.util.Map.Entry;

public class Property extends Element {
   protected Object value;

   public Property(Element parent, Element prototype, Object value) {
      super(prototype);
      this.parent = parent;
      this.value = value;
   }

   public void setValue(Object value) {
      this.setValue(value, this.getName());
   }

   void setValue(Object value, String ownName) {
      Object old = this.value;
      this.value = value;
      this.parent.firePropertyChanged(this, ownName, old, value);
   }

   public <T> T getValue() {
      T res = this.value;
      return res;
   }

   public Property setX(int x) {
      this.setProperty("x", x);
      return this;
   }

   public Property setY(int y) {
      this.setProperty("y", y);
      return this;
   }

   public Property setXY(int x, int y) {
      this.setX(x);
      this.setY(y);
      return this;
   }

   public Color getColor() {
      return (Color)this.getPropertyValue("color");
   }

   public Font getFont() {
      return (Font)this.getPropertyValue("font");
   }

   public boolean isVisible() {
      return (Boolean)this.getPropertyValue("visible");
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitProperty(this);
   }

   public String getName() {
      Iterator var1 = this.parent.getProperties().entrySet().iterator();

      Entry entry;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         entry = (Entry)var1.next();
      } while(entry.getValue() != this);

      return (String)entry.getKey();
   }

   public Element getPrototypeFromParent(Element parent) {
      return parent.prototype == null ? null : parent.prototype.getProperty(this.getName());
   }

   public int getX() {
      if (this.getParent() instanceof Message) {
         Float f = (Float)this.getPropertyValue("f");
         if (f == 10.0F) {
            return super.getX();
         } else {
            Message message = (Message)this.getParent();
            int s = message.getSource() == null ? message.getX() : message.getSource().getX();
            int t = message.getTarget() == null ? message.getX() : message.getTarget().getX();
            return (int)((float)s + f * (float)(t - s));
         }
      } else {
         return super.getX();
      }
   }

   public String getFriendlyName() {
      return (String)this.getPropertyValue("#name.tag");
   }
}
