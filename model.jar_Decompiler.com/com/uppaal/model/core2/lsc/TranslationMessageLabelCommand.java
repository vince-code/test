package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.CommandManager;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Property;
import com.uppaal.model.core2.TranslationCommand;
import java.util.ArrayList;
import java.util.Iterator;

public class TranslationMessageLabelCommand extends TranslationCommand implements LscConstants {
   private float initialPos;
   private float newPos;
   int y1;

   public TranslationMessageLabelCommand(CommandManager commandManager, ArrayList<Element> elements) {
      super(commandManager, elements);
      Property label = (Property)this.getModifiedElement();
      Message message = (Message)label.getParent();
      Float f = (Float)label.getPropertyValue("f");
      if (f == 10.0F) {
         int s = message.getSource() == null ? message.getX() : message.getSource().getX();
         int t = message.getTarget() == null ? message.getX() : message.getTarget().getX();
         f = ((float)label.getX() - (float)s) / (float)(t - s);
      }

      this.initialPos = f;
      label.setProperty("f", 10.0F);
   }

   protected void moveElements(int x, int y) {
      int ye = this.getY() + y;
      int length = ((LscTemplate)this.getModifiedElement().getTemplate()).getLength() + 10;
      if (ye >= length || ye <= 0) {
         y = 0;
      }

      Iterator var5 = this.elements.iterator();

      while(var5.hasNext()) {
         Element element = (Element)var5.next();
         element.setProperty("x", element.getX() + x);
         element.setProperty("y", element.getY() + y);
      }

      this.y1 = y;
   }

   private int getY() {
      return ((Element)this.elements.get(0)).getY() + ((Element)this.elements.get(0)).getParent().getY();
   }

   public void move(int x, int y) {
      assert !this.committed : "Cannot move a committed translation";

      this.moveElements(x, y);
      this.xd += x;
      this.yd += this.y1;
   }

   protected void doCancel() {
      this.moveElements(-this.xd, -this.yd);
      this.getModifiedElement().setProperty("f", this.initialPos);
      this.xd = this.yd = 0;
      this.initialPos = this.newPos = 0.0F;
   }

   protected void doExecute() {
      this.moveElements(this.xd, this.yd);
      this.getModifiedElement().setProperty("f", this.newPos);
   }

   protected void doUndo() {
      this.moveElements(-this.xd, -this.yd);
      this.getModifiedElement().setProperty("f", this.initialPos);
   }

   public void updateF() {
      Property label = null;
      Iterator var2 = this.elements.iterator();

      while(var2.hasNext()) {
         Element e = (Element)var2.next();
         if (e instanceof Property) {
            label = (Property)e;
            break;
         }
      }

      Message message = (Message)label.getParent();
      int s = message.getSource() == null ? message.getX() : message.getSource().getX();
      int t = message.getTarget() == null ? message.getX() : message.getTarget().getX();
      int x = (Integer)label.getPropertyValue("x");
      this.newPos = ((float)x - (float)s) / (float)(t - s);
      label.setProperty("f", this.newPos);
   }
}
