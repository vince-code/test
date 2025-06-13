package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.AbstractTransaction;
import com.uppaal.model.core2.CommandManager;
import com.uppaal.model.core2.Element;

public class ChangeAnchorCommand extends AbstractTransaction implements LscConstants {
   protected InstanceLine oldAnchor;
   protected InstanceLine newAnchor;
   protected AnchoredElement element;
   ViewWorkAround view;

   public ChangeAnchorCommand(CommandManager commandManager, Element element) {
      super(commandManager);
      this.element = (AnchoredElement)element;
      this.oldAnchor = this.element.getAnchor();
   }

   public void changeAnchor(int x) {
      this.setAnchor((InstanceLine)null);
      this.element.setProperty("x", x);
   }

   public void setFinalAnchor(InstanceLine anchor, ViewWorkAround view) {
      this.view = view;
      this.setAnchor(anchor);
      this.newAnchor = anchor;
   }

   private void setAnchor(InstanceLine anchor) {
      this.element.setAnchor(anchor);
      if (this.view != null) {
         if (this.element instanceof Condition) {
            this.view.setAnchorToUpdate(anchor, (Condition)this.element);
         }

      }
   }

   protected void doCancel() {
      this.setAnchor(this.oldAnchor);
      this.element.getAnchor().setProperty("x", this.element.getAnchor().getX());
   }

   protected void doExecute() {
      this.setAnchor(this.newAnchor);
      this.element.getAnchor().setProperty("x", this.element.getAnchor().getX());
   }

   protected void doUndo() {
      this.doCancel();
   }

   public Element getModifiedElement() {
      return this.element;
   }
}
