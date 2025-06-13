package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.AbstractTransaction;
import com.uppaal.model.core2.CommandManager;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Node;
import java.util.Iterator;

public class RemoveAnchorCommand extends AbstractTransaction {
   protected InstanceLine oldAnchor;
   protected Condition element;
   private ViewWorkAround view;
   private Update update;

   public RemoveAnchorCommand(CommandManager commandManager, Element element, InstanceLine anchor) {
      super(commandManager);
      this.element = (Condition)element;
      this.oldAnchor = anchor;
   }

   public void removeAnchor(ViewWorkAround view) {
      this.view = view;
      this.element.getAnchors().remove(this.oldAnchor);
      Iterator var2 = this.element.getAnchors().iterator();

      while(var2.hasNext()) {
         InstanceLine anchor = (InstanceLine)var2.next();
         anchor.setProperty("x", anchor.getX());
      }

      Update newUpdate = view.getAnchoredToConditionUpdate(this.element, this.oldAnchor);
      if (newUpdate != null) {
         this.update = null;
      }

   }

   protected void doCancel() {
      this.doUndo();
   }

   protected void doExecute() {
      this.removeAnchor(this.view);
      Iterator var1 = this.element.getAnchors().iterator();

      while(var1.hasNext()) {
         InstanceLine anchor = (InstanceLine)var1.next();
         anchor.setProperty("x", anchor.getX());
      }

   }

   protected void doUndo() {
      this.element.addAnchor(this.oldAnchor);
      Iterator var1 = this.element.getAnchors().iterator();

      while(var1.hasNext()) {
         InstanceLine anchor = (InstanceLine)var1.next();
         anchor.setProperty("x", anchor.getX());
      }

      if (this.update != null) {
         ((Node)this.element.getParent()).insert(this.update, this.update.previous);
      }

   }

   public Element getModifiedElement() {
      return this.element;
   }
}
