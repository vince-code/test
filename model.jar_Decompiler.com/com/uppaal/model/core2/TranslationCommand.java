package com.uppaal.model.core2;

import java.util.ArrayList;
import java.util.Iterator;

public class TranslationCommand extends AbstractTransaction {
   protected ArrayList<Element> elements;
   protected int xd;
   protected int yd;

   public TranslationCommand(CommandManager commandManager, ArrayList<Element> elements) {
      super(commandManager);
      this.elements = elements;
      this.xd = this.yd = 0;
   }

   protected void moveElements(int x, int y) {
      Iterator var3 = this.elements.iterator();

      while(var3.hasNext()) {
         Element element = (Element)var3.next();
         element.setProperty("x", element.getX() + x);
         element.setProperty("y", element.getY() + y);
      }

   }

   public void move(int x, int y) {
      this.moveElements(x, y);
      this.xd += x;
      this.yd += y;
   }

   public void mergeMove(int x, int y) {
      this.moveElements(x, y);
      this.xd += x;
      this.yd += y;
      this.mergeCommit();
   }

   protected void doCancel() {
      this.moveElements(-this.xd, -this.yd);
      this.xd = this.yd = 0;
   }

   protected void doExecute() {
      this.moveElements(this.xd, this.yd);
   }

   protected void doUndo() {
      this.moveElements(-this.xd, -this.yd);
   }

   public Element getModifiedElement() {
      return (Element)this.elements.get(0);
   }

   public boolean merge(Command next) {
      if (next instanceof TranslationCommand && this.elements == ((TranslationCommand)next).elements) {
         TranslationCommand n = (TranslationCommand)next;
         this.xd += n.xd;
         this.yd += n.yd;
         return true;
      } else {
         return false;
      }
   }
}
