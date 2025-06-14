package com.uppaal.model.core2;

public class RemoveTemplateCommand extends AbstractCommand {
   protected Node node;
   protected Node parent;
   protected Node sibling;

   public RemoveTemplateCommand(Node node) {
      this.node = node;
      this.parent = (Node)node.getParent();
      if (node.next != null) {
         this.sibling = node.next;
      } else if (node.previous != null) {
         this.sibling = node.previous;
      } else {
         this.sibling = this.parent;
      }

   }

   public void execute() {
      this.node.remove();
   }

   public void undo() {
      this.parent.insert(this.node, this.node.previous);
   }

   public Element getModifiedElement() {
      return this.node.getParent() == null ? this.sibling : this.node;
   }
}
