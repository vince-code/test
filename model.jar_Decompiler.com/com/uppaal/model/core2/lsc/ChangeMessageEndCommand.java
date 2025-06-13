package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.AbstractTransaction;
import com.uppaal.model.core2.CommandManager;
import com.uppaal.model.core2.Element;

public class ChangeMessageEndCommand extends AbstractTransaction implements LscConstants {
   protected InstanceLine oldEnd;
   protected Message element;
   protected InstanceLine newEnd;
   protected boolean changeSource = false;
   protected int oldX;
   protected int newX;
   protected int oldY;
   protected int newY;

   public ChangeMessageEndCommand(CommandManager commandManager, Element element, boolean source) {
      super(commandManager);
      this.element = (Message)element;
      this.changeSource = source;
      if (this.changeSource) {
         this.oldEnd = ((Message)element).getSource();
      } else {
         this.oldEnd = ((Message)element).getTarget();
      }

      this.oldX = element.getX();
      this.oldY = element.getY();
   }

   public void changeEnd(int x) {
      this.setEnd((InstanceLine)null);
      this.element.setProperty("x", x);
   }

   public void setFinalEnd(InstanceLine end) {
      this.setEnd(end);
      this.newX = this.element.getX();
      this.newY = this.element.getY();
      this.newEnd = end;
   }

   private void setEnd(InstanceLine end) {
      if (this.changeSource) {
         this.element.setSource(end);
      } else {
         this.element.setTarget(end);
      }

   }

   protected void doCancel() {
      this.setEnd(this.oldEnd);
      this.element.setProperty("x", this.oldX);
      this.element.setProperty("y", this.oldY);
      this.element.getTarget().setProperty("x", this.element.getTarget().getX());
      this.element.getSource().setProperty("x", this.element.getSource().getX());
   }

   protected void doExecute() {
      this.setEnd(this.newEnd);
      this.element.setProperty("x", this.newX);
      this.element.setProperty("y", this.newY);
      this.element.getTarget().setProperty("x", this.element.getTarget().getX());
      this.element.getSource().setProperty("x", this.element.getSource().getX());
   }

   protected void doUndo() {
      this.doCancel();
   }

   public Element getModifiedElement() {
      return this.element;
   }
}
