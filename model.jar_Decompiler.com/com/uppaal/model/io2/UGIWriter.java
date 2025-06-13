package com.uppaal.model.io2;

import com.uppaal.model.core2.AbstractLocation;
import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.AbstractVisitor;
import com.uppaal.model.core2.BranchPoint;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Nail;
import com.uppaal.model.core2.Property;
import java.awt.Color;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UGIWriter extends AbstractVisitor {
   protected Writer writer;
   protected Map<Element, String> names;
   protected Set<EdgeId> edges;

   UGIWriter(OutputStream stream, Map<Element, String> names) {
      this.writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
      this.names = names;
   }

   protected void writeCoords(int x, int y) throws Exception {
      this.writer.write(40);
      this.writer.write(Integer.toString(x));
      this.writer.write(44);
      this.writer.write(Integer.toString(y));
      this.writer.write(");\n");
   }

   protected void writeProperty(String ugiName, String propertyName, int x, int y, AbstractLocation location) throws Exception {
      if (location.isPropertyLocal(propertyName)) {
         Property property = location.getProperty(propertyName);
         Integer x1 = (Integer)property.getPropertyValue("x");
         Integer y1 = (Integer)property.getPropertyValue("y");
         this.writer.write(ugiName);
         this.writer.write(32);
         this.writer.write((String)this.names.get(location));
         this.writeCoords(x1 - x, y1 - y);
      }

   }

   protected void writeColor(Location location) throws Exception {
      if (location.isPropertyLocal("color")) {
         Color color = location.getColor();
         String hex = String.format("#%06x", color.getRGB() & 16777215);
         this.writer.write("lcolor ");
         this.writer.write((String)this.names.get(location));
         this.writer.write(32);
         this.writer.write(hex);
         this.writer.write(";\n");
      }

   }

   protected void writeProperty(String ugiName, String propertyName, int x, int y, Edge edge, EdgeId id) throws Exception {
      if (edge.isPropertyLocal(propertyName)) {
         Property property = edge.getProperty(propertyName);
         Integer x1 = (Integer)property.getPropertyValue("x");
         Integer y1 = (Integer)property.getPropertyValue("y");
         this.writer.write(ugiName);
         this.writer.write(32);
         this.writer.write(id.toString());
         this.writer.write(32);
         this.writeCoords(x1 - x, y1 - y);
      }

   }

   protected void writeColor(Edge edge, EdgeId id) throws Exception {
      if (edge.isPropertyLocal("color")) {
         Color color = edge.getColor();
         String hex = String.format("#%06x", color.getRGB() & 16777215);
         this.writer.write("ecolor ");
         this.writer.write(id.toString());
         this.writer.write(32);
         this.writer.write(hex);
         this.writer.write(";\n");
      }

   }

   public void visitDocument(Document document) throws Exception {
      super.visitDocument(document);
      this.writer.flush();
   }

   public void visitTemplate(AbstractTemplate template) throws Exception {
      Writer var10000 = this.writer;
      Object var10001 = this.names.get(template);
      var10000.write("process " + (String)var10001 + " graphinfo {\n");
      this.edges = new HashSet();
      super.visitTemplate(template);
      this.writer.write("}\n");
   }

   public void visitLocation(Location location) throws Exception {
      int x = (Integer)location.getPropertyValue("x");
      int y = (Integer)location.getPropertyValue("y");
      Writer var10000 = this.writer;
      Object var10001 = this.names.get(location);
      var10000.write("location " + (String)var10001 + " ");
      this.writeCoords(x, y);
      this.writeProperty("locationName", "name", x, y, location);
      this.writeProperty("invariant", "invariant", x, y, location);
      this.writeProperty("exponentialrate", "exponentialrate", x, y, location);
      this.writeColor(location);
   }

   public void visitBranchPoint(BranchPoint branchPoint) throws Exception {
      int x = (Integer)branchPoint.getPropertyValue("x");
      int y = (Integer)branchPoint.getPropertyValue("y");
      Writer var10000 = this.writer;
      Object var10001 = this.names.get(branchPoint);
      var10000.write("branchpoint " + (String)var10001 + " ");
      this.writeCoords(x, y);
   }

   public void visitEdge(Edge edge) throws Exception {
      AbstractLocation source = edge.getSource();
      AbstractLocation target = edge.getTarget();
      int x1 = (Integer)source.getPropertyValue("x");
      int y1 = (Integer)source.getPropertyValue("y");
      int x2 = (Integer)target.getPropertyValue("x");
      int y2 = (Integer)target.getPropertyValue("y");
      final int x = (x1 + x2) / 2;
      final int y = (y1 + y2) / 2;
      final EdgeId id = new EdgeId((String)this.names.get(source), (String)this.names.get(target), 1);

      while(this.edges.contains(id)) {
         id.increment();
      }

      this.edges.add(id);
      edge.accept(new AbstractVisitor() {
         protected boolean first = true;

         public void visitNail(Nail nail) throws Exception {
            UGIWriter.this.writer.write("trans ");
            UGIWriter.this.writer.write(id.toString());
            UGIWriter.this.writer.write(32);
            UGIWriter.this.writeCoords((Integer)nail.getPropertyValue("x") - x, (Integer)nail.getPropertyValue("y") - y);
         }
      });
      this.writeProperty("select", "select", x, y, edge, id);
      this.writeProperty("guard", "guard", x, y, edge, id);
      this.writeProperty("sync", "synchronisation", x, y, edge, id);
      this.writeProperty("assign", "assignment", x, y, edge, id);
      this.writeProperty("probability", "probability", x, y, edge, id);
      this.writeColor(edge, id);
   }
}
