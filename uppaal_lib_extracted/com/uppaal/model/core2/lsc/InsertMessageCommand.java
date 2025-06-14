package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.AbstractTransaction;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Node;

public class InsertMessageCommand extends AbstractTransaction {
   LscTemplate template;
   Message message;
   InstanceLine source;
   InstanceLine target;

   public InsertMessageCommand(InstanceLine source, Message message) {
      super(source.getTemplate().getCommandManager());
      this.template = (LscTemplate)source.getTemplate();
      this.message = message;
      message.setSource(source);
      this.template.insert(message, (Node)null);
   }

   public InstanceLine getSource() {
      return this.message.getSource();
   }

   public void setTarget(InstanceLine target) {
      this.message.setTarget(target);
   }

   public Element getElement() {
      return this.message;
   }

   protected void doCancel() {
      this.message.remove();
      this.message = null;
      this.template = null;
      this.source = null;
   }

   protected void doExecute() {
      this.template.insert(this.message, (Node)null);
   }

   protected void doUndo() {
      this.message.remove();
   }

   public Element getModifiedElement() {
      return (Element)(this.message.getParent() == null ? this.template : this.message);
   }

   public void move(int x) {
      this.message.setProperty("x", x);
   }

   public void moveY(int y) {
      this.message.setProperty("y", y);
   }
}
