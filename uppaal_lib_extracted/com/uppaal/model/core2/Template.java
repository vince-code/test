package com.uppaal.model.core2;

import java.util.HashMap;
import java.util.Map;

public class Template extends AbstractTemplate {
   public Template(Element prototype) {
      super(prototype);
   }

   public Location createLocation() {
      return new Location((Element)this.getPropertyValue("#location"));
   }

   public Location addLocation() {
      Location l = this.createLocation();
      this.insert(l, (Node)null);
      return l;
   }

   public BranchPoint createBranchPoint() {
      return new BranchPoint((Element)this.getPropertyValue("#branchpoint"));
   }

   public BranchPoint addBranchPoint() {
      BranchPoint bp = new BranchPoint((Element)this.getPropertyValue("#branchpoint"));
      this.insert(bp, (Node)null);
      return bp;
   }

   public Edge createEdge() {
      return new Edge((Element)this.getPropertyValue("#edge"));
   }

   public Edge addEdge(AbstractLocation source, AbstractLocation target) throws ModelException {
      if (source.parent != this) {
         throw new ModelException("Source must belong to the same template");
      } else if (target.parent != this) {
         throw new ModelException("Target must belong to the same template");
      } else if (source instanceof BranchPoint && target instanceof BranchPoint) {
         throw new ModelException("Both Source and Target cannot be branchpoints");
      } else {
         Edge e = this.createEdge();
         e.setSource(source);
         e.setTarget(target);
         this.insert(e, (Node)null);
         return e;
      }
   }

   public Element getPrototypeFromParent(Element parent) {
      return (Element)parent.getPropertyValue("#template");
   }

   public String getFriendlyName() {
      return (String)this.getPropertyValue("name");
   }

   public Object clone() throws CloneNotSupportedException {
      Template template = (Template)super.clone();
      final Map<Node, Node> mapping = new HashMap();
      Node p = this.first;

      for(Node q = template.first; p != null; q = q.getNext()) {
         assert q != null;

         mapping.put(p, q);
         p = p.getNext();
      }

      template.acceptSafe(new AbstractVisitor() {
         public void visitEdge(Edge edge) {
            edge.source = (AbstractLocation)mapping.get(edge.source);
            edge.target = (AbstractLocation)mapping.get(edge.target);
         }
      });
      return template;
   }
}
