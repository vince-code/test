package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.InsertElementCommand;
import com.uppaal.model.core2.Node;

public class InsertAnchoredElementCommand extends InsertElementCommand {
   public InsertAnchoredElementCommand(InstanceLine anchor, AnchoredElement element) {
      super(anchor.getTemplate().getCommandManager(), anchor.getTemplate(), (Node)null, element);
      element.setAnchor(anchor);
   }

   public void setAnchor(InstanceLine anchor) {
      ((AnchoredElement)this.element).setAnchor(anchor);
   }

   protected void doCancel() {
      super.doCancel();
   }

   protected void doUndo() {
      super.doUndo();
   }
}
