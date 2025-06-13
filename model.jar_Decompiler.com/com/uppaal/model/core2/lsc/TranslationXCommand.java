package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.CommandManager;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.TranslationCommand;
import java.util.ArrayList;
import java.util.Iterator;

public class TranslationXCommand extends TranslationCommand {
   public TranslationXCommand(CommandManager commandManager, ArrayList<Element> elements) {
      super(commandManager, elements);
   }

   private void moveElements(int x) {
      Iterator var2 = this.elements.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         element.setProperty("x", element.getX() + x);
      }

   }

   public void move(int x) {
      assert !this.committed : "Cannot move a committed translation";

      this.moveElements(x);
      this.xd += x;
   }
}
