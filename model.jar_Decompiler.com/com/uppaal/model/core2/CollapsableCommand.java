package com.uppaal.model.core2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class CollapsableCommand extends AbstractCommand {
   private final boolean allowCollapse;
   protected final Property property;
   protected List<Command> commands;

   public CollapsableCommand(boolean allowCollapse, Property property) {
      this.allowCollapse = allowCollapse;
      this.property = property;
   }

   public void execute() {
      if (this.commands != null) {
         Iterator var1 = this.commands.iterator();

         while(var1.hasNext()) {
            Command command = (Command)var1.next();
            command.execute();
         }
      }

   }

   public void undo() {
      if (this.commands != null) {
         for(int i = this.commands.size() - 1; i >= 0; --i) {
            ((Command)this.commands.get(i)).undo();
         }
      }

   }

   public boolean merge(Command next) {
      if (next instanceof CollapsableCommand) {
         CollapsableCommand other = (CollapsableCommand)next;
         if (other.allowCollapse && this.getModifiedElement() == other.getModifiedElement()) {
            if (this.commands == null) {
               this.commands = new ArrayList();
            }

            this.commands.add(other);
            return true;
         }
      }

      return this.commands != null ? ((Command)this.commands.get(this.commands.size() - 1)).merge(next) : false;
   }

   public Optional<Command> getLast() {
      return this.commands != null ? Optional.of((Command)this.commands.get(this.commands.size() - 1)) : Optional.empty();
   }

   public Element getModifiedElement() {
      return this.property;
   }
}
