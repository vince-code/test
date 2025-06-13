package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.InsertElementCommand;
import com.uppaal.model.core2.Node;

public class InsertInstanceCommand extends InsertElementCommand {
   private final ViewWorkAround view;
   private int oldLength = 0;
   private int newLength = 0;
   private int prechart = -1;

   public InsertInstanceCommand(AbstractTemplate parent, Node position, Node element, ViewWorkAround view) {
      super(parent.getCommandManager(), parent, position, element);
      this.view = view;
      this.prechart = view.getPrechartIndex();
      view.addInstanceLine(this.prechart, (InstanceLine)element);
   }

   public void move(int x, int y) {
      assert this.element != null : "Cannot move canceled element";

      this.element.setProperty("x", x);
      if (this.oldLength == 0) {
         this.oldLength = ((InstanceLine)this.element).getLength();
      }

      this.newLength = y;
      ((InstanceLine)this.element).setLength(y, this.view);
   }

   protected void doCancel() {
      if (this.oldLength != 0) {
         ((InstanceLine)this.element).setLength(this.oldLength, this.view);
      }

      this.view.removeInstanceLine(this.prechart, (InstanceLine)this.element);
      super.doCancel();
   }

   protected void doExecute() {
      this.parent.insert(this.element, this.position);
      if (this.newLength != 0) {
         ((InstanceLine)this.element).setLength(this.newLength, this.view);
      }

      this.view.addInstanceLine(this.prechart, (InstanceLine)this.element);
   }

   protected void doUndo() {
      if (this.oldLength != 0) {
         ((InstanceLine)this.element).setLength(this.oldLength, this.view);
      }

      this.view.removeInstanceLine(this.prechart, (InstanceLine)this.element);
      super.doUndo();
   }
}
