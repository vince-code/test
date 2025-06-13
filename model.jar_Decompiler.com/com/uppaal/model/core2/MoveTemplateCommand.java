package com.uppaal.model.core2;

public class MoveTemplateCommand extends AbstractCommand {
   Node parent;
   Node element;
   Node oldposition;
   Node newposition;

   public MoveTemplateCommand(Node parent, Node position, Node element) {
      assert parent == position.getParent();

      this.parent = parent;
      this.oldposition = element.previous;
      this.newposition = position;
      this.element = element;
   }

   public void execute() {
      this.parent.move(this.element, this.newposition);
   }

   public void undo() {
      this.parent.move(this.element, this.oldposition);
   }

   public Element getModifiedElement() {
      return this.element;
   }
}
