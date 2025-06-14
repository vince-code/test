package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.AbstractTransaction;
import com.uppaal.model.core2.CommandManager;
import com.uppaal.model.core2.Element;

public class SetLengthCommand extends AbstractTransaction implements LscConstants {
   protected int oldLength = 0;
   protected InstanceLine element;
   protected int newLength = 0;
   private ViewWorkAround view;

   public SetLengthCommand(CommandManager commandManager, Element element, ViewWorkAround view) {
      super(commandManager);
      this.element = (InstanceLine)element;
      this.oldLength = ((InstanceLine)element).getLength();
      this.view = view;
   }

   public void resize(int y) {
      this.element.setLength(y - 2, this.view);
      this.newLength = y;
   }

   protected void doCancel() {
      this.element.setLength(this.oldLength, this.view);
      this.newLength = this.oldLength;
   }

   protected void doExecute() {
      this.element.setLength(this.newLength, this.view);
   }

   protected void doUndo() {
      this.element.setLength(this.oldLength, this.view);
   }

   public Element getModifiedElement() {
      return this.element;
   }
}
