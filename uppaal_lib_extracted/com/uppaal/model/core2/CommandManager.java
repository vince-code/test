package com.uppaal.model.core2;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandManager {
   private final AtomicInteger counter = new AtomicInteger();
   private int version;
   private final LinkedList<Command> undoList;
   private final LinkedList<Command> redoList;
   private final LinkedList<CommandListener> cmdListeners;

   public CommandManager() {
      this.version = this.counter.incrementAndGet();
      this.undoList = new LinkedList();
      this.redoList = new LinkedList();
      this.cmdListeners = new LinkedList();
   }

   public CommandManager(CommandManager orig) {
      this.version = this.counter.incrementAndGet();
      this.undoList = new LinkedList();
      this.redoList = new LinkedList();
      this.cmdListeners = new LinkedList();
      this.version = orig.version;
   }

   public int getVersion() {
      return this.version;
   }

   public void execute(Command command) {
      command.setVersion(this.version);
      this.version = this.counter.incrementAndGet();
      this.redoList.clear();
      command.execute();
      this.undoList.add(command);
      Iterator var2 = this.cmdListeners.iterator();

      while(var2.hasNext()) {
         CommandListener l = (CommandListener)var2.next();
         l.executed(this, command);
      }

   }

   public void mergeExecute(Command command) {
      command.setVersion(this.version);
      this.version = this.counter.incrementAndGet();
      this.redoList.clear();
      command.execute();
      if (this.undoList.isEmpty() || !((Command)this.undoList.getLast()).merge(command)) {
         this.undoList.add(command);
      }

      Iterator var2 = this.cmdListeners.iterator();

      while(var2.hasNext()) {
         CommandListener l = (CommandListener)var2.next();
         l.executed(this, command);
      }

   }

   public void execute(Command... commands) {
      this.execute((Command)(new CompoundCommand(commands)));
   }

   public Command getLastCommand() {
      return this.undoList.isEmpty() ? null : (Command)this.undoList.getLast();
   }

   public void undo() throws CannotUndoException {
      if (this.undoList.isEmpty()) {
         throw new CannotUndoException();
      } else {
         Command command = (Command)this.undoList.removeLast();
         int oldVersion = this.version;
         this.version = command.getVersion();
         command.setVersion(oldVersion);
         command.undo();
         this.redoList.add(command);
         Iterator var3 = this.cmdListeners.iterator();

         while(var3.hasNext()) {
            CommandListener l = (CommandListener)var3.next();
            l.undone(this, command);
         }

      }
   }

   public boolean canUndo() {
      return !this.undoList.isEmpty();
   }

   public void redo() throws CannotRedoException {
      if (this.redoList.isEmpty()) {
         throw new CannotRedoException();
      } else {
         Command command = (Command)this.redoList.removeLast();
         int oldVersion = this.version;
         this.version = command.getVersion();
         command.setVersion(oldVersion);
         command.execute();
         this.undoList.add(command);
         Iterator var3 = this.cmdListeners.iterator();

         while(var3.hasNext()) {
            CommandListener l = (CommandListener)var3.next();
            l.redone(this, command);
         }

      }
   }

   public boolean canRedo() {
      return !this.redoList.isEmpty();
   }

   public void addCommandListener(CommandListener listener) {
      if (listener != null) {
         this.cmdListeners.add(listener);
      }

   }

   public void removeCommandListener(CommandListener listener) {
      if (listener != null) {
         this.cmdListeners.remove(listener);
      }

   }
}
