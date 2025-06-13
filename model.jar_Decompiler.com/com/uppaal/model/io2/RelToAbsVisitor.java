package com.uppaal.model.io2;

import com.uppaal.model.core2.AbstractLocation;
import com.uppaal.model.core2.AbstractVisitor;
import com.uppaal.model.core2.BranchPoint;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Nail;
import com.uppaal.model.core2.Property;

public class RelToAbsVisitor extends AbstractVisitor {
   private int x;
   private int y;

   private void addPosition(Element element) {
      Integer x1 = (Integer)element.getPropertyValue("x");
      Integer y1 = (Integer)element.getPropertyValue("y");
      if (x1 != null && y1 != null) {
         element.setProperty("x", x1 + this.x);
         element.setProperty("y", y1 + this.y);
      }

   }

   public void visitProperty(Property property) throws Exception {
      this.addPosition(property);
   }

   public void visitNail(Nail nail) throws Exception {
      this.addPosition(nail);
   }

   public void visitEdge(Edge edge) throws Exception {
      AbstractLocation source = edge.getSource();
      AbstractLocation target = edge.getTarget();
      Integer x1 = (Integer)source.getPropertyValue("x");
      Integer y1 = (Integer)source.getPropertyValue("y");
      Integer x2 = (Integer)target.getPropertyValue("x");
      Integer y2 = (Integer)target.getPropertyValue("y");
      if (x1 != null && y1 != null && x2 != null && y2 != null) {
         this.x = (x1 + x2) / 2;
         this.y = (y1 + y2) / 2;
         super.visitEdge(edge);
      }

   }

   public void visitLocation(Location location) throws Exception {
      Integer x1 = (Integer)location.getPropertyValue("x");
      Integer y1 = (Integer)location.getPropertyValue("y");
      if (x1 != null && y1 != null) {
         this.x = x1;
         this.y = y1;
         super.visitLocation(location);
      }

   }

   public void visitBranchPoint(BranchPoint branchPoint) throws Exception {
      Integer x1 = (Integer)branchPoint.getPropertyValue("x");
      Integer y1 = (Integer)branchPoint.getPropertyValue("y");
      if (x1 != null && y1 != null) {
         this.x = x1;
         this.y = y1;
         super.visitBranchPoint(branchPoint);
      }

   }
}
