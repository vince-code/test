package com.uppaal.model.core2;

public class InsertEdgeCommand extends AbstractTransaction {
   Edge edge;
   Template template;
   Nail nail;
   AbstractLocation target;

   public InsertEdgeCommand(AbstractLocation source, int x, int y) {
      super(source.getTemplate().getCommandManager());
      this.template = (Template)source.getTemplate();
      this.edge = this.template.createEdge();
      this.edge.setSource(source);
      this.addNail(x, y);
      this.template.insert(this.edge, (Node)null);
   }

   public void addNail(int x, int y) {
      this.nail = this.edge.createNail();
      this.nail.setProperty("x", x);
      this.nail.setProperty("y", y);
      this.edge.insert(this.nail, this.edge.getLast());
   }

   public void move(int x, int y) {
      if (this.nail == null) {
         this.addNail(x, y);
      } else {
         this.nail.setProperty("x", x);
         this.nail.setProperty("y", y);
      }

   }

   public void commitLocation(int x, int y) {
      assert this.edge.getTarget() == null;

      this.target = this.template.createLocation();
      this.target.setProperty("x", x);
      this.target.setProperty("y", y);
      this.template.insert(this.target, (Node)null);
      this.edge.setTarget(this.target);
      if (this.nail != null) {
         this.nail.remove();
         this.nail = null;
      }

      super.commit();
   }

   public void commitBranchPoint(int x, int y) {
      assert this.edge.getTarget() == null;

      this.target = this.template.createBranchPoint();
      this.target.setProperty("x", x);
      this.target.setProperty("y", y);
      this.template.insert(this.target, (Node)null);
      this.edge.setTarget(this.target);
      this.edge.setProperty("controllable", false);
      if (this.nail != null) {
         this.nail.remove();
         this.nail = null;
      }

      super.commit();
   }

   public void commit(AbstractLocation target) {
      assert this.edge.getTarget() == null;

      this.edge.setTarget(target);
      if (target instanceof BranchPoint) {
         this.edge.setProperty("controllable", false);
      }

      if (this.nail != null) {
         this.nail.remove();
         this.nail = null;
      }

      super.commit();
   }

   public boolean hasNails() {
      return this.edge.getFirst() != null && this.edge.getFirst().getNext() != null;
   }

   public AbstractLocation getSource() {
      return this.edge.getSource();
   }

   public AbstractLocation getTarget() {
      return this.edge.getTarget();
   }

   public void commit() {
      assert false : "Must specify target";

   }

   protected void doCancel() {
      this.edge.remove();
      this.template = null;
      this.edge = null;
      this.nail = null;
      if (this.target != null) {
         this.target.remove();
         this.target = null;
      }

   }

   protected void doExecute() {
      if (this.target != null) {
         this.template.insert(this.target, (Node)null);
      }

      this.template.insert(this.edge, (Node)null);
   }

   protected void doUndo() {
      this.edge.remove();
      if (this.target != null) {
         this.target.remove();
      }

   }

   public Element getModifiedElement() {
      return (Element)(this.edge.getParent() == null ? this.template : this.edge);
   }
}
