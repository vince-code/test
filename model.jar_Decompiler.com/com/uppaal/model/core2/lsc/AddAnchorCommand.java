package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.AbstractTransaction;
import com.uppaal.model.core2.CommandManager;
import com.uppaal.model.core2.Element;
import java.util.Iterator;

public class AddAnchorCommand extends AbstractTransaction {
   protected Condition element;
   protected InstanceLine anchor;

   public AddAnchorCommand(CommandManager commandManager, Element element) {
      super(commandManager);
      this.element = (Condition)element;
   }

   public void addAnchor(InstanceLine anchor, int x) {
      if (anchor == null) {
         this.element.setProperty("x", x);
      } else {
         this.anchor = anchor;
      }

      this.element.addAnchor(anchor);
   }

   protected void doCancel() {
      this.doUndo();
   }

   protected void doExecute() {
      this.addAnchor(this.anchor, 0);
      Iterator var1 = this.element.getAnchors().iterator();

      while(var1.hasNext()) {
         InstanceLine anchor = (InstanceLine)var1.next();
         anchor.setProperty("x", anchor.getX());
      }

   }

   protected void doUndo() {
      this.element.getAnchors().remove(this.anchor);
      Iterator var1 = this.element.getAnchors().iterator();

      while(var1.hasNext()) {
         InstanceLine anchor = (InstanceLine)var1.next();
         anchor.setProperty("x", anchor.getX());
      }

   }

   public Element getModifiedElement() {
      return this.element;
   }
}
