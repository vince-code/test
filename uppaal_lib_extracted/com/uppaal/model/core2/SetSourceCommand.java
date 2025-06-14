package com.uppaal.model.core2;

public class SetSourceCommand extends SetEndPointCommand {
   public SetSourceCommand(CommandManager commandManager, Edge edge) {
      super(commandManager, edge);
   }

   public AbstractLocation getLocation() {
      return this.edge.getSource();
   }

   protected void setLocation(AbstractLocation location) {
      this.edge.setSource(location);
   }

   protected void insertNail(Nail nail) {
      this.edge.insert(nail, (Node)null);
   }
}
