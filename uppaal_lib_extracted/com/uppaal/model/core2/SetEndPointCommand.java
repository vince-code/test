package com.uppaal.model.core2;

public abstract class SetEndPointCommand extends AbstractTransaction {
   protected Edge edge;
   protected AbstractLocation location;
   protected Nail nail;

   public abstract AbstractLocation getLocation();

   protected abstract void setLocation(AbstractLocation var1);

   protected abstract void insertNail(Nail var1);

   public SetEndPointCommand(CommandManager commandManager, Edge edge) {
      super(commandManager);
      this.edge = edge;
      this.location = this.getLocation();
   }

   public Nail convertToNail(int x, int y) {
      assert this.nail == null : "Can only convert end point to nail once.";

      assert !this.committed : "Cannot modify a committed command.";

      this.nail = this.edge.createNail();
      this.nail.setProperty("x", x);
      this.nail.setProperty("y", y);
      this.insertNail(this.nail);
      return this.nail;
   }

   public void moveNailTo(int x, int y) {
      assert this.nail != null : "Must convert to nail first.";

      assert !this.committed : "Cannot modify a committed command.";

      this.nail.setProperty("x", x);
      this.nail.setProperty("y", y);
   }

   public void setEndPoint(AbstractLocation location) {
      assert !this.committed : "Cannot modify a committed command.";

      if (this.nail != null) {
         this.nail.remove();
         this.nail = null;
      }

      this.setLocation(location);
   }

   protected void doCancel() {
      if (this.nail != null) {
         this.nail.remove();
         this.nail = null;
      }

      this.setLocation(this.location);
   }

   protected void doExecute() {
      this.swap();
      if (this.nail != null) {
         this.insertNail(this.nail);
      }

   }

   protected void doUndo() {
      this.swap();
      if (this.nail != null) {
         this.nail.remove();
      }

   }

   protected void swap() {
      AbstractLocation tmp = this.getLocation();
      this.setLocation(this.location);
      this.location = tmp;
   }

   public Element getModifiedElement() {
      return this.edge;
   }
}
