package com.uppaal.model;

import com.uppaal.model.core2.AbstractLocation;
import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.AbstractVisitor;
import com.uppaal.model.core2.BranchPoint;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Nail;
import com.uppaal.model.core2.Node;
import com.uppaal.model.core2.Property;

public class LayoutVisitor extends AbstractVisitor {
   private final int offsetx = 40;
   private final int offsety = 80;
   private final int inc = 150;
   private int x;
   private int y;

   private void assurePosition(Element element, int x, int y) {
      if (element.getLocalProperty("x") == null || element.getLocalProperty("y") == null) {
         element.setProperty("x", x);
         element.setProperty("y", y);
      }

   }

   private void assurePositionAndContinue(Node node, int x, int y) throws Exception {
      this.assurePosition(node, x, y);
      this.visitNode(node);
   }

   private void assurePositionRelativeToParent(Element element, int dx, int dy) {
      Element parent = element.getParent();
      int x_ = parent.getX() + dx;
      int y_ = parent.getY() + dy;
      this.assurePosition(element, x_, y_);
   }

   private void setRelativeToEdge(Element element, int dx, int dy) {
      Edge edge = (Edge)element.getParent();
      AbstractLocation source = edge.getSource();
      AbstractLocation target = edge.getTarget();
      int x1 = source.getX();
      int y1 = source.getY();
      int x2 = target.getX();
      int y2 = target.getY();
      this.assurePosition(element, (x1 + x2) / 2 + dx, (y1 + y2) / 2 + dy);
   }

   public void visitTemplate(AbstractTemplate element) throws Exception {
      this.x = 0;
      this.y = 0;
      this.visitNode(element);
   }

   public void visitProperty(Property element) {
      if (element.getName().equals("name")) {
         Element parent = element.getParent();
         if (parent instanceof Location) {
            this.assurePositionRelativeToParent(element, -10, -30);
         }
      } else if (element.getName().equals("invariant")) {
         this.assurePositionRelativeToParent(element, -10, 15);
      } else if (element.getName().equals("exponentialrate")) {
         this.assurePositionRelativeToParent(element, -10, 20);
      } else if (element.getName().equals("select")) {
         this.setRelativeToEdge(element, -60, -45);
      } else if (element.getName().equals("guard")) {
         this.setRelativeToEdge(element, -60, -30);
      } else if (element.getName().equals("synchronisation")) {
         this.setRelativeToEdge(element, -60, -15);
      } else if (element.getName().equals("assignment")) {
         this.setRelativeToEdge(element, -60, 0);
      } else if (element.getName().equals("probability")) {
         this.setRelativeToEdge(element, -60, 15);
      } else if (element.getName().equals("comments")) {
         if (element.getParent() instanceof Edge) {
            this.setRelativeToEdge(element, -60, 30);
         } else {
            this.assurePositionRelativeToParent(element, -10, 35);
         }
      }

   }

   public void visitLocation(Location element) throws Exception {
      this.assurePositionAndContinue(element, 40 + 150 * this.x, 80 + 150 * this.y);
      if (this.x == 0) {
         this.x = this.y + 1;
         this.y = 0;
      } else if (this.y < this.x) {
         ++this.y;
      } else {
         --this.x;
      }

   }

   public void visitBranchPoint(BranchPoint element) throws Exception {
      this.assurePositionAndContinue(element, 40 + 150 * this.x, 80 + 150 * this.y);
      if (this.x == 0) {
         this.x = this.y + 1;
         this.y = 0;
      } else if (this.y < this.x) {
         ++this.y;
      } else {
         --this.x;
      }

   }

   private void addNail(Edge edge, int x, int y) {
      Nail nail = edge.createNail();
      nail.setProperty("x", x);
      nail.setProperty("y", y);
      edge.insert(nail, edge.getLast());
   }

   public void visitEdge(Edge element) throws Exception {
      if (element.getNails() == null) {
         AbstractLocation source = element.getSource();
         AbstractLocation target = element.getTarget();
         if (source == target) {
            int x = source.getX();
            int y = source.getY();
            this.addNail(element, x - 30, y - 30);
            this.addNail(element, x + 30, y - 30);
         }
      }

      this.visitNode(element);
   }
}
