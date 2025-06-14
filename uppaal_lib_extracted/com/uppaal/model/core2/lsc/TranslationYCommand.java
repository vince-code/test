package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.CommandManager;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.TranslationCommand;
import java.util.ArrayList;
import java.util.Iterator;

public class TranslationYCommand extends TranslationCommand implements LscConstants {
   protected int oldLength = 0;
   protected int newLength = 0;
   protected ViewWorkAround view;
   protected boolean resize = false;

   public TranslationYCommand(CommandManager commandManager, ArrayList<Element> elements) {
      super(commandManager, elements);
      this.oldLength = ((LscTemplate)this.getModifiedElement().getTemplate()).getLength();
   }

   private void moveElements(int y) {
      Iterator var2 = this.elements.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         if (!(element.getParent() instanceof Message)) {
            element.setProperty("y", element.getY() + y);
         }
      }

   }

   public void move(int y) {
      assert !this.committed : "Cannot move a committed translation";

      if (this.getYMin() + y >= 30) {
         this.moveElements(y);
         this.yd += y;
      }

   }

   private int getYMin() {
      int min = this.getModifiedElement().getY();
      if (this.getModifiedElement().getParent() instanceof Message) {
         min = this.getModifiedElement().getParent().getY();
      }

      Iterator var3 = this.elements.iterator();

      while(var3.hasNext()) {
         Element element = (Element)var3.next();
         int y = element.getY();
         if (element.getParent() instanceof Message) {
            y = element.getParent().getY();
         }

         if (y < min) {
            min = y;
         }
      }

      return min;
   }

   public void resizeIfNecessary(ViewWorkAround view) {
      int ymax = this.GetYMax();
      int length = ((LscTemplate)this.getModifiedElement().getTemplate()).getLength();
      if (ymax > 10 + length - 20) {
         this.view = view;
         this.newLength = ymax + 20;
         this.resize = true;
         ((LscTemplate)this.getModifiedElement().getTemplate()).setLength(this.newLength - 10, view);
      }
   }

   private int GetYMax() {
      int y = 0;
      Iterator var2 = this.elements.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         int ye = element.getY();
         if (element.getParent() instanceof Message) {
            ye = element.getParent().getY();
         }

         if (ye > y) {
            y = ye;
         }
      }

      return y;
   }

   protected void doCancel() {
      this.moveElements(-this.yd);
      if (this.resize) {
         ((LscTemplate)this.getModifiedElement().getTemplate()).setLength(this.oldLength, this.view);
      }

      this.yd = 0;
   }

   protected void doExecute() {
      this.moveElements(this.yd);
      if (this.resize) {
         ((LscTemplate)this.getModifiedElement().getTemplate()).setLength(this.newLength, this.view);
      }

   }

   protected void doUndo() {
      this.moveElements(-this.yd);
      if (this.resize) {
         ((LscTemplate)this.getModifiedElement().getTemplate()).setLength(this.oldLength, this.view);
      }

   }
}
