package com.uppaal.model.core2;

import java.util.List;

class SnapVisitor extends AbstractVisitor {
   private final List<Position> positions;
   private final double diameter;

   SnapVisitor(double diameter, List<Position> positions) {
      this.diameter = diameter;
      this.positions = positions;
   }

   private int snap(double value) {
      return (int)Math.round((double)Math.round(value / this.diameter) * this.diameter);
   }

   private void snap(Element element) {
      Integer x = (Integer)element.getPropertyValue("x");
      Integer y = (Integer)element.getPropertyValue("y");
      if (x != null && y != null) {
         this.positions.add(new Position(element, this.snap((double)x), this.snap((double)y)));
      }

   }

   public void visitProperty(Property property) throws Exception {
      this.snap(property);
   }

   public void visitLocation(Location location) throws Exception {
      this.snap(location);
      super.visitLocation(location);
   }

   public void visitBranchPoint(BranchPoint branchPoint) throws Exception {
      this.snap(branchPoint);
      super.visitBranchPoint(branchPoint);
   }

   public void visitNail(Nail nail) throws Exception {
      this.snap(nail);
   }
}
