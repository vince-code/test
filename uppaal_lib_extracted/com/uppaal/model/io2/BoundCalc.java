package com.uppaal.model.io2;

import com.uppaal.model.Translator;
import com.uppaal.model.core2.AbstractVisitor;
import com.uppaal.model.core2.BranchPoint;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Node;
import com.uppaal.model.core2.Property;
import com.uppaal.model.system.EmptyTranslator;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BoundCalc extends AbstractVisitor {
   private Rectangle2D bounds;
   private boolean empty;
   private Translator translator;
   static final double lHeight = 14.0D;
   static final Graphics context;
   public static final FontMetrics plainMetrics;
   public static final FontMetrics boldMetrics;

   public static double plainWidth(String s) {
      return Math.ceil(plainMetrics.getStringBounds(s, context).getWidth());
   }

   public static double boldWidth(String s) {
      return Math.ceil(boldMetrics.getStringBounds(s, context).getWidth());
   }

   public BoundCalc() {
      this(new EmptyTranslator());
   }

   public BoundCalc(Translator aTranslator) {
      this.bounds = new Double();
      this.empty = true;
      this.translator = aTranslator;
      this.bounds.setRect(0.0D, 0.0D, 0.0D, 0.0D);
      this.empty = true;
   }

   private void draw(Property property, FontMetrics metrics) throws IOException {
      if (property != null) {
         Object value = property.getValue();
         if (value instanceof String) {
            String line = this.translator.translate((String)value);
            if (line.length() > 0) {
               int l = 0;
               int lastpos = 0;
               int x = (Integer)property.getPropertyValue("x");
               int y = (Integer)property.getPropertyValue("y");
               if (this.empty) {
                  this.bounds.setRect((double)x, (double)y, 8.0D, 8.0D);
                  this.empty = false;
               } else {
                  this.bounds.add((double)x, (double)y);
                  this.bounds.add((double)(x + 8), (double)(y + 8));
               }

               Rectangle2D b;
               for(int pos = line.indexOf(10, lastpos); pos >= 0; ++l) {
                  if (pos > lastpos) {
                     b = metrics.getStringBounds(line, lastpos, pos, (Graphics)null);
                     this.bounds.add((double)x + b.getMaxX(), (double)y + (double)l * 14.0D + b.getMaxY());
                  }

                  lastpos = pos + 1;
                  pos = line.indexOf(10, lastpos);
               }

               if (line.length() > lastpos) {
                  b = metrics.getStringBounds(line, lastpos, line.length(), (Graphics)null);
                  this.bounds.add((double)x + b.getMaxX(), (double)y + (double)l * 14.0D + b.getMaxY());
               }
            }

         }
      }
   }

   protected void draw(Element element, double margin) {
      int x = (Integer)element.getPropertyValue("x");
      int y = (Integer)element.getPropertyValue("y");
      if (this.empty) {
         this.bounds.setRect((double)x - margin, (double)y - margin, 2.0D * margin, 2.0D * margin);
         this.empty = false;
      } else {
         this.bounds.add((double)x - margin, (double)y - margin);
         this.bounds.add((double)x + margin, (double)y + margin);
      }

   }

   private void draw(Edge edge) {
      for(Node node = edge.getFirst(); node != null; node = node.getNext()) {
         this.draw(node, 5.0D);
      }

   }

   public void visitLocation(Location location) throws Exception {
      this.draw(location, 12.0D);
      this.draw(location.getProperty("name"), boldMetrics);
      this.draw(location.getProperty("invariant"), boldMetrics);
      this.draw(location.getProperty("exponentialrate"), boldMetrics);
      this.draw(location.getProperty("comments"), plainMetrics);
   }

   public void visitBranchPoint(BranchPoint branchPoint) throws Exception {
      this.draw(branchPoint, 5.0D);
   }

   public void visitEdge(Edge edge) throws Exception {
      this.draw(edge);
      this.draw(edge.getProperty("select"), plainMetrics);
      this.draw(edge.getProperty("guard"), plainMetrics);
      this.draw(edge.getProperty("synchronisation"), plainMetrics);
      this.draw(edge.getProperty("assignment"), plainMetrics);
      this.draw(edge.getProperty("probability"), plainMetrics);
      this.draw(edge.getProperty("comments"), plainMetrics);
   }

   public Rectangle2D getBounds(Element e) {
      this.bounds.setRect(0.0D, 0.0D, 0.0D, 0.0D);
      this.empty = true;
      e.acceptSafe(this);
      return this.empty ? null : new Double(this.bounds.getMinX(), this.bounds.getMinY(), this.bounds.getWidth(), this.bounds.getHeight());
   }

   static {
      BufferedImage i = new BufferedImage(1, 1, 1);
      context = i.createGraphics();
      plainMetrics = context.getFontMetrics(new Font("Helvetica", 0, 14));
      boldMetrics = context.getFontMetrics(new Font("Helvetica", 1, 14));
   }
}
