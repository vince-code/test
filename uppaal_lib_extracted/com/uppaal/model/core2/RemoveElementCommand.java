package com.uppaal.model.core2;

public class RemoveElementCommand extends AbstractCommand {
   protected Node node;
   protected Node parent;

   public RemoveElementCommand(Node node) {
      this.node = node;
   }

   public void execute() {
      this.parent = (Node)this.node.getParent();
      this.node.remove();
   }

   public void undo() {
      this.parent.insert(this.node, this.node.previous);
   }

   public Element getModifiedElement() {
      return this.node.getParent() == null ? this.parent : this.node;
   }
}
