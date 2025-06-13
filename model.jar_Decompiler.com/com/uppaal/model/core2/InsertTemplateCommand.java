package com.uppaal.model.core2;

public class InsertTemplateCommand extends AbstractCommand {
   Node parent;
   Node element;
   Node position;
   Node sibling;

   public InsertTemplateCommand(Node parent, Node position, Node element) {
      this.parent = parent;
      this.position = position;
      this.element = element;
   }

   public void execute() {
      this.parent.insert(this.element, this.position);
      if (this.element.next != null) {
         this.sibling = this.element.next;
      } else if (this.element.previous != null) {
         this.sibling = this.element.previous;
      } else {
         this.sibling = this.parent;
      }

   }

   public void undo() {
      this.element.remove();
   }

   public Element getModifiedElement() {
      return this.element.getParent() == null ? this.sibling : this.element;
   }
}
