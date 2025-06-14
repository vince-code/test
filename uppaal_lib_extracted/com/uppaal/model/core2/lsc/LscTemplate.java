package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.AbstractVisitor;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LscTemplate extends AbstractTemplate implements LscConstants {
   public LscTemplate(Element prototype) {
      super(prototype);
   }

   public void setLength(int y, ViewWorkAround view) {
      if (y >= 210) {
         this.setProperty("length", view.getInstanceLength(y));
         view.populateInstance(this);
      }
   }

   public void setLengthTo(int y, ViewWorkAround view) {
      this.setLength(y - 10, view);
   }

   public int getLength() {
      return (Integer)this.getPropertyValue("length");
   }

   public InstanceLine createInstanceLine() {
      return new InstanceLine((Element)this.getPropertyValue("#instance"));
   }

   public Prechart createPrechart() {
      return new Prechart((Element)this.getPropertyValue("#prechart"));
   }

   public Message createMessage() {
      return new Message((Element)this.getPropertyValue("#message"));
   }

   public Cut createCut() {
      return new Cut((Element)this.getPropertyValue("#cut"));
   }

   public Condition createCondition() {
      return new Condition((Element)this.getPropertyValue("#condition"));
   }

   public Update createUpdate() {
      return new Update((Element)this.getPropertyValue("#update"));
   }

   public Element getPrototypeFromParent(Element parent) {
      return (Element)parent.getPropertyValue("#lscTemplate");
   }

   public String getFriendlyName() {
      return "LSC";
   }

   public Object clone() throws CloneNotSupportedException {
      LscTemplate template = (LscTemplate)super.clone();
      final Map<Node, Node> mapping = new HashMap();
      Node p = this.first;

      for(Node q = template.first; p != null; q = q.getNext()) {
         assert q != null;

         mapping.put(p, q);
         p = p.getNext();
      }

      template.acceptSafe(new AbstractVisitor() {
         public void visitMessage(Message message) {
            message.source = (InstanceLine)mapping.get(message.source);
            message.target = (InstanceLine)mapping.get(message.target);
         }

         public void visitPrechart(Prechart prechart) {
            int size = prechart.instances.size();
            ArrayList<InstanceLine> copy = prechart.instances;
            prechart.instances = new ArrayList(size);

            for(int i = 0; i < size; ++i) {
               prechart.instances.add((InstanceLine)mapping.get(copy.get(i)));
            }

         }

         public void visitCondition(Condition condition) {
            int size = condition.anchors.size();
            ArrayList<InstanceLine> copy = condition.anchors;
            condition.anchors = new ArrayList(size);

            for(int i = 0; i < size; ++i) {
               condition.anchors.add((InstanceLine)mapping.get(copy.get(i)));
            }

         }

         public void visitUpdate(Update update) {
            update.anchor = (InstanceLine)mapping.get(update.anchor);
         }
      });
      return template;
   }

   public ArrayList<Integer> getYLocCoord() {
      ArrayList<Integer> yLocCoord = new ArrayList();

      for(Node node = this.getFirst(); node != null; node = node.getNext()) {
         Integer y = node.getY();
         if (!yLocCoord.contains(y)) {
            yLocCoord.add(y);
         }
      }

      Collections.sort(yLocCoord);
      yLocCoord.add(this.getLength() + 10);
      return yLocCoord;
   }

   public Object getType() {
      return this.getPropertyValue("type");
   }

   public Object getMode() {
      return this.getPropertyValue("mode");
   }
}
