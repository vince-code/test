package com.uppaal.model.core2;

public class InsertElementCommand extends AbstractTransaction {
   protected Node parent;
   protected Node element;
   protected Node position;
   Node sibling;

   public InsertElementCommand(CommandManager commandManager, Node parent, Node position, Node element) {
      super(commandManager);

      assert position == null || parent == position.getParent();

      this.parent = parent;
      this.position = position;
      this.element = element;
      parent.insert(element, position);
      if (element.next != null) {
         this.sibling = element.next;
      } else {
         this.sibling = element.previous;
      }

   }

   public void move(int x, int y) {
      assert !this.committed : "Cannot move committed element";

      assert this.element != null : "Cannot move canceled element";

      this.element.setProperty("x", x);
      this.element.setProperty("y", y);
   }

   public Element getElement() {
      return this.element;
   }

   protected void doCancel() {
      this.element.remove();
      this.element = null;
   }

   protected void doExecute() {
      this.parent.insert(this.element, this.position);
   }

   protected void doUndo() {
      this.element.remove();
   }

   public Element getModifiedElement() {
      if (this.element.getParent() == null) {
         return this.sibling != null ? this.sibling : this.parent;
      } else {
         return this.element;
      }
   }
}
