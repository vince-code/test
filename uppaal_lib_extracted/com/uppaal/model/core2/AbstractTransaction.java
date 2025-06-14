package com.uppaal.model.core2;

public abstract class AbstractTransaction extends AbstractCommand {
   protected boolean committed = false;
   protected CommandManager commandManager;

   public AbstractTransaction(CommandManager commandManager) {
      this.commandManager = commandManager;
   }

   public void commit() {
      this.commandManager.execute((Command)this);
   }

   public void mergeCommit() {
      this.commandManager.mergeExecute(this);
   }

   public void cancel() {
      assert !this.committed : "Cannot cancel committed transaction";

      this.doCancel();
   }

   protected abstract void doCancel();

   protected abstract void doExecute();

   protected abstract void doUndo();

   public void execute() {
      if (!this.committed) {
         this.committed = true;
      } else {
         this.doExecute();
      }

   }

   public void undo() {
      assert this.committed : "Cannot undo uncommitted transaction";

      this.doUndo();
   }

   public void move(int xd, int yd) {
   }

   public void mergeMove(int xd, int yd) {
   }
}
