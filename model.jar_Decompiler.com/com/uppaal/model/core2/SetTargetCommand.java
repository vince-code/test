package com.uppaal.model.core2;

public class SetTargetCommand extends SetEndPointCommand {
   protected boolean controllable;

   public SetTargetCommand(CommandManager commandManager, Edge edge) {
      super(commandManager, edge);
      this.controllable = edge.hasFlag("controllable");
   }

   public AbstractLocation getLocation() {
      return this.edge.getTarget();
   }

   protected void setLocation(AbstractLocation location) {
      this.edge.setTarget(location);
      if (location instanceof BranchPoint) {
         this.edge.setProperty("controllable", false);
      } else {
         this.edge.setProperty("controllable", this.controllable);
      }

   }

   protected void insertNail(Nail nail) {
      this.edge.insert(nail, this.edge.getLast());
   }
}
