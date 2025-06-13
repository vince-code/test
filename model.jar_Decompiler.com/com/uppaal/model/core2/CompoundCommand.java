package com.uppaal.model.core2;

public class CompoundCommand extends AbstractCommand {
   protected Command[] commands;

   public CompoundCommand(Command... commands) {
      this.commands = commands;
   }

   public void execute() {
      Command[] var1 = this.commands;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Command command = var1[var3];
         command.execute();
      }

   }

   public void undo() {
      for(int i = this.commands.length - 1; i >= 0; --i) {
         this.commands[i].undo();
      }

   }

   public Element getModifiedElement() {
      return this.commands[0].getModifiedElement();
   }

   public Command[] getCommands() {
      return this.commands;
   }
}
