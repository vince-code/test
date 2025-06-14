package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.AbstractCommand;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Node;
import java.util.ArrayList;
import java.util.Iterator;

public class RemoveLscElementCommand extends AbstractCommand {
   protected Node node;
   protected Node parent;
   private ViewWorkAround view;
   private Prechart prechart;
   private ArrayList<Condition> conditions;
   private Update update = null;

   public RemoveLscElementCommand(Node node, ViewWorkAround view) {
      this.node = node;
      this.view = view;
      this.prechart = view.getPrechart();
      this.conditions = view.getConditionsOf(node);
      if (node instanceof Condition) {
         this.update = view.getUpdateOf(node);
      }

   }

   public void execute() {
      if (this.prechart != null) {
         this.addAnchoredElements(false);
      }

      if (this.conditions != null && this.conditions.size() > 0) {
         this.addAnchoredToCondition(false);
      }

      if (this.update != null) {
         this.update.remove();
      }

      this.parent = (Node)this.node.getParent();
      this.node.remove();
   }

   private void addAnchoredElements(boolean b) {
      if (this.node instanceof InstanceLine) {
         if (b) {
            this.prechart.add((InstanceLine)this.node);
         } else {
            this.prechart.remove((InstanceLine)this.node);
         }

         this.view.populatePrechart(this.node.getTemplate());
      }
   }

   private void addAnchoredToCondition(boolean b) {
      Iterator var2 = this.conditions.iterator();

      while(var2.hasNext()) {
         Condition condition = (Condition)var2.next();
         if (b) {
            condition.addAnchor((InstanceLine)this.node);
         } else {
            condition.removeAnchor((InstanceLine)this.node);
         }
      }

      this.view.populateCondition(this.node.getTemplate());
   }

   public void undo() {
      this.parent.insert(this.node, this.node.previous);
      if (this.prechart != null) {
         this.addAnchoredElements(true);
      }

      if (this.conditions != null && this.conditions.size() > 0) {
         this.addAnchoredToCondition(true);
      }

      if (this.update != null) {
         this.parent.insert(this.update, this.update.previous);
      }

   }

   public Element getModifiedElement() {
      return this.node.getParent() == null ? this.parent : this.node;
   }
}
