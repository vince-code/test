package com.uppaal.model.io2;

import com.uppaal.model.core2.AbstractLocation;
import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.AbstractVisitor;
import com.uppaal.model.core2.BranchPoint;
import com.uppaal.model.core2.Data2D;
import com.uppaal.model.core2.DataSet2D;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.EngineSettings;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Nail;
import com.uppaal.model.core2.Property;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.QueryExpected;
import com.uppaal.model.core2.QueryResource;
import com.uppaal.model.core2.QueryResult;
import com.uppaal.model.core2.QueryValue;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.lsc.Condition;
import com.uppaal.model.core2.lsc.InstanceLine;
import com.uppaal.model.core2.lsc.LscElement;
import com.uppaal.model.core2.lsc.LscTemplate;
import com.uppaal.model.core2.lsc.Message;
import com.uppaal.model.core2.lsc.Prechart;
import com.uppaal.model.core2.lsc.Update;
import java.awt.Color;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLWriter extends AbstractVisitor {
   protected int counter;
   protected XMLStreamWriter writer;
   protected Map<AbstractLocation, String> locations = new HashMap();
   protected Map<InstanceLine, String> instances = new HashMap();
   private ArrayList<Integer> yLocCoord = new ArrayList();
   protected String init;
   int depth = 0;

   public XMLWriter(OutputStream stream) throws XMLStreamException {
      XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
      this.writer = outputFactory.createXMLStreamWriter(stream, StandardCharsets.UTF_8.name());
   }

   public XMLWriter(Writer writer) throws XMLStreamException {
      XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
      this.writer = outputFactory.createXMLStreamWriter(writer);
   }

   protected void indent() throws XMLStreamException {
      this.writer.writeCharacters("\r\n");

      for(int i = 0; i < this.depth; ++i) {
         this.writer.writeCharacters("\t");
      }

   }

   protected void writePropertyAsElement(Element element, String property, boolean optional) throws XMLStreamException {
      String value = (String)element.getPropertyValue(property);
      if (!optional || value != null && value.length() > 0) {
         this.indent();
         this.writer.writeStartElement(property);
         this.writeAttributes(element.getProperty(property));
         if (value != null) {
            this.writer.writeCharacters(value);
         }

         this.writer.writeEndElement();
      }

   }

   protected void writePropertyAsLabel(Element element, String name) throws XMLStreamException {
      Property property = element.getProperty(name);
      if (property != null) {
         String str = property.getValue().toString().trim();
         if (str.length() > 0) {
            this.indent();
            this.writer.writeStartElement("label");
            this.writer.writeAttribute("kind", name);
            this.writeAttributes(property);
            this.writer.writeCharacters(str);
            this.writer.writeEndElement();
         }
      }

   }

   protected void writeAttributes(Element element) throws XMLStreamException {
      Object x = element.getPropertyValue("x");
      Object y = element.getPropertyValue("y");
      if (element.getParent() instanceof Message) {
         Float f = (Float)element.getPropertyValue("f");
         Message message = (Message)element.getParent();
         int s = message.getSource().getX();
         int t = message.getTarget().getX();
         x = (int)((float)s + f * (float)(t - s));
      }

      if (x != null) {
         this.writer.writeAttribute("x", x.toString());
      }

      if (y != null) {
         this.writer.writeAttribute("y", y.toString());
      }

      Property color = element.getLocalProperty("color");
      if (color != null) {
         int rgb = ((Color)color.getValue()).getRGB() & 16777215;
         String hex = String.format("#%06x", rgb);
         this.writer.writeAttribute("color", hex);
      }

   }

   protected boolean hasFlag(Element element, String property) {
      Object value = element.getPropertyValue(property);
      return value != null && (Boolean)value;
   }

   protected void writeFlag(Element element, String property) throws XMLStreamException {
      if (this.hasFlag(element, property)) {
         this.indent();
         this.writer.writeEmptyElement(property);
      }

   }

   protected void writeTextField(String localName, String contents) throws XMLStreamException {
      if (contents.trim().isEmpty()) {
         this.writer.writeEmptyElement(localName);
      } else {
         this.writer.writeStartElement(localName);
         this.writer.writeCharacters(contents);
         this.writer.writeEndElement();
      }

   }

   public void visitDocument(Document document) throws Exception {
      this.writer.writeStartDocument("utf-8", "1.0");
      this.indent();
      XMLStreamWriter var10000 = this.writer;
      String var10001 = UXMLResolver.getPublicID();
      var10000.writeDTD("<!DOCTYPE nta PUBLIC '" + var10001 + "' '" + UXMLResolver.getSystemID() + "'>");
      this.indent();
      this.writer.writeStartElement("nta");
      ++this.depth;
      this.writePropertyAsElement(document, "declaration", true);
      Iterator var2 = document.getTemplateList().iterator();

      while(var2.hasNext()) {
         AbstractTemplate template = (AbstractTemplate)var2.next();
         this.visitTemplate(template);
      }

      this.writePropertyAsElement(document, "instantiation", true);
      this.writePropertyAsElement(document, "system", false);
      this.visitConcretePlotConfigs(document.getConcretePlots());
      document.getQueryList().accept(this);
      --this.depth;
      this.indent();
      this.writer.writeEndElement();
      this.indent();
      this.writer.writeEndDocument();
      this.writer.close();
   }

   public void visitTemplate(AbstractTemplate template) throws Exception {
      if (template instanceof Template) {
         this.visitTemplate((Template)template);
      } else {
         this.visitTemplate((LscTemplate)template);
      }

   }

   private void visitTemplate(Template template) throws Exception {
      this.indent();
      ++this.depth;
      this.writer.writeStartElement("template");
      this.writePropertyAsElement(template, "name", false);
      this.writePropertyAsElement(template, "parameter", true);
      this.writePropertyAsElement(template, "declaration", true);
      template.accept(new AbstractVisitor() {
         public void visitLocation(Location location) throws Exception {
            location.accept(XMLWriter.this);
         }
      });
      template.accept(new AbstractVisitor() {
         public void visitBranchPoint(BranchPoint branchPoint) throws Exception {
            branchPoint.accept(XMLWriter.this);
         }
      });
      if (this.init != null) {
         this.indent();
         this.writer.writeEmptyElement("init");
         this.writer.writeAttribute("ref", this.init);
         this.init = null;
      }

      template.accept(new AbstractVisitor() {
         public void visitEdge(Edge edge) throws Exception {
            edge.accept(XMLWriter.this);
         }
      });
      --this.depth;
      this.indent();
      this.writer.writeEndElement();
   }

   private void visitTemplate(LscTemplate template) throws Exception {
      this.indent();
      ++this.depth;
      this.writer.writeStartElement("lsc");
      this.writePropertyAsElement(template, "name", false);
      this.writePropertyAsElement(template, "parameter", true);
      this.writePropertyAsElement(template, "type", false);
      this.writePropertyAsElement(template, "mode", false);
      this.writePropertyAsElement(template, "declaration", true);
      this.yLocCoord = template.getYLocCoord();
      int size = this.yLocCoord.size();

      for(int i = 0; i < size; ++i) {
         this.indent();
         this.writer.writeEmptyElement("yloccoord");
         this.writer.writeAttribute("number", String.valueOf(i));
         this.writer.writeAttribute("y", ((Integer)this.yLocCoord.get(i)).toString());
      }

      template.accept(new AbstractVisitor() {
         public void visitInstanceLine(InstanceLine instance) throws Exception {
            instance.accept(XMLWriter.this);
         }
      });
      template.accept(new AbstractVisitor() {
         public void visitPrechart(Prechart prechart) throws Exception {
            prechart.accept(XMLWriter.this);
         }
      });
      template.accept(new AbstractVisitor() {
         public void visitMessage(Message message) throws Exception {
            message.accept(XMLWriter.this);
         }
      });
      template.accept(new AbstractVisitor() {
         public void visitCondition(Condition condition) throws Exception {
            condition.accept(XMLWriter.this);
         }
      });
      template.accept(new AbstractVisitor() {
         public void visitUpdate(Update update) throws Exception {
            update.accept(XMLWriter.this);
         }
      });
      --this.depth;
      this.indent();
      this.writer.writeEndElement();
   }

   public void visitLocation(Location location) throws Exception {
      int var10002 = this.counter++;
      String id = "id" + var10002;
      this.indent();
      ++this.depth;
      this.writer.writeStartElement("location");
      this.writer.writeAttribute("id", id);
      this.writeAttributes(location);
      this.locations.put(location, id);
      this.writePropertyAsElement(location, "name", true);
      this.writePropertyAsLabel(location, "invariant");
      this.writePropertyAsLabel(location, "exponentialrate");
      this.writePropertyAsLabel(location, "comments");
      this.writePropertyAsLabel(location, "testcodeEnter");
      this.writePropertyAsLabel(location, "testcodeExit");
      this.writeFlag(location, "urgent");
      this.writeFlag(location, "committed");
      if (this.hasFlag(location, "init")) {
         this.init = id;
      }

      --this.depth;
      this.indent();
      this.writer.writeEndElement();
   }

   public void visitBranchPoint(BranchPoint branchPoint) throws Exception {
      int var10002 = this.counter++;
      String id = "id" + var10002;
      this.indent();
      this.writer.writeEmptyElement("branchpoint");
      this.writer.writeAttribute("id", id);
      this.writeAttributes(branchPoint);
      this.locations.put(branchPoint, id);
   }

   public void visitEdge(Edge edge) throws Exception {
      int var10002 = this.counter++;
      String id = "id" + var10002;
      this.indent();
      ++this.depth;
      this.writer.writeStartElement("transition");
      this.writer.writeAttribute("id", id);
      if (!edge.hasFlag("controllable")) {
         this.writer.writeAttribute("controllable", "false");
      }

      this.writeAttributes(edge);
      this.indent();
      this.writer.writeEmptyElement("source");
      this.writer.writeAttribute("ref", (String)this.locations.get(edge.getSource()));
      this.indent();
      this.writer.writeEmptyElement("target");
      this.writer.writeAttribute("ref", (String)this.locations.get(edge.getTarget()));
      this.writePropertyAsLabel(edge, "select");
      this.writePropertyAsLabel(edge, "guard");
      this.writePropertyAsLabel(edge, "synchronisation");
      this.writePropertyAsLabel(edge, "assignment");
      this.writePropertyAsLabel(edge, "comments");
      this.writePropertyAsLabel(edge, "testcode");
      this.writePropertyAsLabel(edge, "probability");
      super.visitEdge(edge);
      --this.depth;
      this.indent();
      this.writer.writeEndElement();
   }

   public void visitNail(Nail nail) throws Exception {
      this.indent();
      this.writer.writeEmptyElement("nail");
      this.writeAttributes(nail);
   }

   public void visitInstanceLine(InstanceLine instance) throws Exception {
      int var10002 = this.counter++;
      String id = "id" + var10002;
      this.indent();
      ++this.depth;
      this.writer.writeStartElement("instance");
      this.writer.writeAttribute("id", id);
      this.writeAttributes(instance);
      this.instances.put(instance, id);
      this.writePropertyAsElement(instance, "name", true);
      --this.depth;
      this.indent();
      this.writer.writeEndElement();
   }

   public void visitPrechart(Prechart prechart) throws Exception {
      this.indent();
      ++this.depth;
      this.writer.writeStartElement("prechart");
      this.writeAttributes(prechart);
      this.location(prechart);
      --this.depth;
      this.indent();
      this.writer.writeEndElement();
   }

   private void location(LscElement element) throws Exception {
      Integer value = this.yLocCoord.indexOf(element.getY());
      this.indent();
      this.writer.writeStartElement("lsclocation");
      this.writer.writeCharacters(value.toString());
      this.writer.writeEndElement();
   }

   public void visitMessage(Message message) throws Exception {
      this.indent();
      ++this.depth;
      this.writer.writeStartElement("message");
      this.writeAttributes(message);
      this.indent();
      this.writer.writeEmptyElement("source");
      this.writer.writeAttribute("ref", (String)this.instances.get(message.getSource()));
      this.indent();
      this.writer.writeEmptyElement("target");
      this.writer.writeAttribute("ref", (String)this.instances.get(message.getTarget()));
      this.location(message);
      this.writePropertyAsLabel(message, "message");
      --this.depth;
      this.indent();
      this.writer.writeEndElement();
   }

   public void visitCondition(Condition condition) throws Exception {
      this.indent();
      ++this.depth;
      this.writer.writeStartElement("condition");
      this.writeAttributes(condition);
      ArrayList<InstanceLine> anchors = condition.getAnchors();
      Iterator var3 = anchors.iterator();

      while(var3.hasNext()) {
         InstanceLine anchor = (InstanceLine)var3.next();
         this.indent();
         this.writer.writeEmptyElement("anchor");
         this.writer.writeAttribute("instanceid", (String)this.instances.get(anchor));
      }

      this.location(condition);
      this.temperature(condition);
      this.writePropertyAsLabel(condition, "condition");
      --this.depth;
      this.indent();
      this.writer.writeEndElement();
   }

   private void temperature(Condition condition) throws Exception {
      boolean value = (Boolean)condition.getPropertyValue("hot");
      String temperature = value ? "hot" : "cold";
      this.indent();
      this.writer.writeStartElement("temperature");
      this.writer.writeCharacters(temperature);
      this.writer.writeEndElement();
   }

   public void visitUpdate(Update update) throws Exception {
      this.indent();
      this.writer.writeStartElement("update");
      this.writeAttributes(update);
      if (update.getAnchoredToCondition() != null) {
         Integer y = update.getAnchoredToCondition().getY();
         update.setProperty("y", y);
      }

      ++this.depth;
      this.indent();
      this.writer.writeEmptyElement("anchor");
      this.writer.writeAttribute("instanceid", (String)this.instances.get(update.getAnchor()));
      this.location(update);
      this.writePropertyAsLabel(update, "update");
      --this.depth;
      this.indent();
      this.writer.writeEndElement();
   }

   public void visitQuery(Query query) throws Exception {
      this.indent();
      this.writer.writeStartElement("query");
      ++this.depth;
      this.indent();
      this.writeTextField("formula", query.getFormula());
      this.indent();
      this.writeTextField("comment", query.getComment());
      if (query.getSettings() != null) {
         this.visitSettings(query.getSettings());
      }

      if (query.getExpected() != null && query.getExpected().getValue() != null) {
         this.visitExpect(query.getExpected());
      }

      if (query.getResult().getStatus() != QueryValue.Status.Unchecked) {
         this.visitResults(query.getResult());
      }

      --this.depth;
      this.indent();
      this.writer.writeEndElement();
   }

   public void visitSettings(EngineSettings settings) throws Exception {
      if (settings != null) {
         Iterator var2 = settings.getProperties().entrySet().iterator();

         while(var2.hasNext()) {
            Entry<String, Property> setting = (Entry)var2.next();
            this.indent();
            this.writer.writeEmptyElement("option");
            this.writer.writeAttribute("key", (String)setting.getKey());
            this.writer.writeAttribute("value", (String)((Property)setting.getValue()).getValue());
         }

      }
   }

   public void visitResults(QueryResult result) throws Exception {
      this.indent();
      this.writer.writeStartElement("result");
      this.visitQueryValue(result.getValue());
      this.writer.writeAttribute("timestamp", result.getTimestamp());
      ++this.depth;
      if (result.getSettings() != null) {
         this.visitSettings(result.getSettings());
      }

      if (result.getMessage() != null && !result.getMessage().isEmpty()) {
         this.indent();
         this.writeTextField("details", result.getMessage());
      }

      Iterator var2 = result.getPlots().iterator();

      while(var2.hasNext()) {
         DataSet2D plotData = (DataSet2D)var2.next();
         this.visitPlot(plotData);
      }

      this.visitResourceList(result.getResources());
      --this.depth;
      this.indent();
      this.writer.writeEndElement();
   }

   private void visitPlot(DataSet2D plot) throws Exception {
      this.indent();
      this.writer.writeStartElement("plot");
      this.writer.writeAttribute("title", plot.getTitle());
      this.writer.writeAttribute("xaxis", plot.getXLabel());
      this.writer.writeAttribute("yaxis", plot.getYLabel());
      ++this.depth;
      Iterator var2 = plot.iterator();

      while(var2.hasNext()) {
         Data2D series = (Data2D)var2.next();
         this.indent();
         this.writer.writeStartElement("series");
         this.writer.writeAttribute("title", series.getTitle());
         this.writer.writeAttribute("type", series.getType());
         XMLStreamWriter var10000 = this.writer;
         String var10002 = Integer.toHexString(series.getColor().getRGB());
         var10000.writeAttribute("color", "0x" + var10002.substring(2));
         this.writer.writeAttribute("encoding", "csv");
         StringBuilder sb = new StringBuilder();
         series.forEach((point) -> {
            sb.append(point.x).append(',').append(point.y).append('\n');
         });
         this.writer.writeCharacters(sb.toString().trim());
         this.indent();
         this.writer.writeEndElement();
      }

      StringBuilder sb = new StringBuilder();
      Iterator var6 = plot.getComments().iterator();

      while(var6.hasNext()) {
         String comment = (String)var6.next();
         sb.append(comment).append('\n');
      }

      this.indent();
      this.writeTextField("comment", sb.substring(0, sb.length() - 1));
      --this.depth;
      this.indent();
      this.writer.writeEndElement();
   }

   public void visitExpect(QueryExpected expect) throws Exception {
      if (expect.getValue() != null) {
         this.indent();
         this.writer.writeStartElement("expect");
         ++this.depth;
         this.visitQueryValue(expect.getValue());
         this.visitResourceList(expect.getResources());
         --this.depth;
         this.indent();
         this.writer.writeEndElement();
      }
   }

   public void visitResourceList(List<QueryResource> resources) throws Exception {
      Iterator var2 = resources.iterator();

      while(var2.hasNext()) {
         QueryResource resource = (QueryResource)var2.next();
         this.indent();
         this.writer.writeEmptyElement("resource");
         this.writer.writeAttribute("type", resource.type);
         this.writer.writeAttribute("value", resource.value);
         if (resource.unit != null && !resource.unit.trim().isEmpty()) {
            this.writer.writeAttribute("unit", resource.unit);
         }
      }

   }

   public void visitQueryValue(QueryValue value) throws Exception {
      this.writer.writeAttribute("outcome", value.getStatus().toString().toLowerCase());
      this.writer.writeAttribute("type", value.getKind().toString().toLowerCase());
      if (value.getValue() != null && !value.getValue().trim().isEmpty()) {
         this.writer.writeAttribute("value", value.getValue().trim());
      }

   }
}
