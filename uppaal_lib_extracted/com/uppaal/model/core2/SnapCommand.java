package com.uppaal.model.core2;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SnapCommand extends AbstractCommand {
   private final AbstractTemplate template;
   private final List<Position> positions = new LinkedList();

   private void swap() {
      Iterator var1 = this.positions.iterator();

      while(var1.hasNext()) {
         Position position = (Position)var1.next();
         position.swap();
      }

   }

   public SnapCommand(AbstractTemplate template, double diameter) {
      this.template = template;
      template.acceptSafe(new SnapVisitor(diameter, this.positions));
   }

   public void execute() {
      this.swap();
   }

   public void undo() {
      this.swap();
   }

   public Element getModifiedElement() {
      return this.template;
   }
}
