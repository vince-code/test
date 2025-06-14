package com.uppaal.model.io2;

import com.uppaal.model.core2.AbstractLocation;
import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.BranchPoint;
import com.uppaal.model.core2.DataSet2D;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.DocumentParseException;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.EngineSettings;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Nail;
import com.uppaal.model.core2.Node;
import com.uppaal.model.core2.PlotConfiguration;
import com.uppaal.model.core2.Property;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.QueryExpected;
import com.uppaal.model.core2.QueryResource;
import com.uppaal.model.core2.QueryResult;
import com.uppaal.model.core2.QueryValue;
import com.uppaal.model.core2.Series;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.Trajectory;
import com.uppaal.model.core2.lsc.Condition;
import com.uppaal.model.core2.lsc.InstanceLine;
import com.uppaal.model.core2.lsc.LscElement;
import com.uppaal.model.core2.lsc.LscTemplate;
import com.uppaal.model.core2.lsc.Message;
import com.uppaal.model.core2.lsc.Prechart;
import com.uppaal.model.core2.lsc.Update;
import java.awt.Color;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

public class XMLReader {
   public static final ResourceBundle LOCALE = ResourceBundle.getBundle("locale.Parser");
   private static final String UNKNOWN_TAG;
   protected XMLEventReader reader;
   protected XMLEvent event;
   protected Map<String, AbstractLocation> locations = new HashMap();
   protected Map<String, Edge> edges = new HashMap();
   protected Map<String, InstanceLine> instances = new HashMap();
   private final ArrayList<Integer> yloccoords = new ArrayList();
   private final Map<Integer, Condition> yConditions = new HashMap();
   private static final Set<String> known_tags;
   private List<Problem> problems = null;
   private int counter;
   private String inst = null;

   public XMLReader(XMLResolver resolver, InputStream s) throws XMLStreamException {
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      inputFactory.setProperty("javax.xml.stream.supportDTD", true);
      inputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
      if (resolver != null) {
         inputFactory.setXMLResolver(resolver);
      }

      this.reader = inputFactory.createXMLEventReader(s, StandardCharsets.UTF_8.name());
   }

   public Document parse(Element prototype, List<Problem> problems) throws XMLStreamException {
      this.problems = problems;
      Document document = null;
      this.counter = 0;

      try {
         if (this.reader.hasNext()) {
            this.next();
            document = new Document(prototype);
            this.project(document);
         }
      } finally {
         this.event = null;
      }

      return document;
   }

   protected void next() throws XMLStreamException {
      this.event = this.reader.nextEvent();
   }

   protected boolean begin(String tag) throws XMLStreamException {
      label46:
      while(!this.event.isEndDocument()) {
         while(!this.event.isStartElement() && !this.event.isEndDocument()) {
            this.next();
         }

         if (this.event.isEndDocument()) {
            return false;
         }

         String currentTag = this.event.asStartElement().getName().getLocalPart();
         if (!known_tags.contains(currentTag)) {
            this.problems.add(new Problem("WARNING", "", UNKNOWN_TAG + " " + currentTag));

            while(true) {
               do {
                  this.next();
               } while(!this.event.isEndElement());

               if (this.event.asEndElement().getName().getLocalPart().equals(currentTag)) {
                  continue label46;
               }
            }
         }

         return !this.event.isEndDocument() && currentTag.equals(tag);
      }

      return false;
   }

   protected String readText() throws XMLStreamException {
      StringBuilder s = new StringBuilder();

      while(this.event.isCharacters()) {
         s.append(this.event.asCharacters().getData());
         this.next();
      }

      return s.toString();
   }

   protected String getAttribute(String name) {
      Attribute attribute = this.event.asStartElement().getAttributeByName(new QName(name));
      return attribute == null ? null : attribute.getValue();
   }

   protected Integer getCoordinate(String attribute) {
      try {
         String s = this.getAttribute(attribute);
         return s == null ? null : Integer.valueOf(s);
      } catch (NumberFormatException var3) {
         System.err.println("Invalid " + attribute + ": not an integer");
         return null;
      }
   }

   protected Color getColor(String attribute) {
      try {
         String s = this.getAttribute(attribute);
         return s == null ? null : Color.decode(s);
      } catch (NumberFormatException var3) {
         System.err.println("Not a color: " + attribute);
         return null;
      }
   }

   protected void project(Document document) throws XMLStreamException {
      if (!this.begin("project")) {
         this.begin("nta");
      }

      this.next();
      this.declaration(document);

      while(this.begin("template") || this.begin("lsc")) {
         this.templates(document);
         this.lscTemplates(document);
      }

      this.instantiation(document);
      this.system(document);
      this.simulator(document);
      this.queries(document);
      this.horizons(document);
   }

   protected EngineSettings options() throws XMLStreamException {
      if (this.begin("option")) {
         EngineSettings settings = new EngineSettings((Element)null);

         while(this.begin("option")) {
            String key = this.getAttribute("key");
            String value = this.getAttribute("value");
            if (key == null) {
               throw new DocumentParseException("Missing 'key' attribute of tag 'option'");
            }

            settings.setValue(key, value);
            this.next();
         }

         return settings;
      } else {
         return null;
      }
   }

   protected void declaration(Node node) throws XMLStreamException {
      if (this.begin("declaration")) {
         this.next();
         node.setProperty("declaration", this.readText());
      }

   }

   protected void name(Node node) throws XMLStreamException {
      if (this.begin("name")) {
         Integer x = this.getCoordinate("x");
         Integer y = this.getCoordinate("y");
         Color color = this.getColor("color");
         this.next();
         Property property = node.setProperty("name", this.readText());
         property.setProperty("x", x);
         property.setProperty("color", color);
         property.setProperty("y", y);
      }

   }

   protected void fixedname(Node node) throws XMLStreamException {
      if (this.begin("name")) {
         this.next();
         node.setProperty("name", this.readText());
      }

   }

   protected void parameter(AbstractTemplate node) throws XMLStreamException {
      if (this.begin("parameter")) {
         this.next();
         node.setProperty("parameter", this.readText());
      }

   }

   protected void type(AbstractTemplate node) throws XMLStreamException {
      if (this.begin("type")) {
         this.next();
         node.setProperty("type", this.readText());
      }

   }

   protected void mode(AbstractTemplate node) throws XMLStreamException {
      if (this.begin("mode")) {
         this.next();
         node.setProperty("mode", this.readText());
      }

   }

   protected void role(AbstractTemplate node) throws XMLStreamException {
      if (this.begin("role")) {
         this.next();
         node.setProperty("role", this.readText());
      }

   }

   protected void labels(Node node) throws XMLStreamException {
      while(this.begin("label")) {
         String kind = this.getAttribute("kind");
         Integer x = this.getCoordinate("x");
         Integer y = this.getCoordinate("y");
         Color color = this.getColor("color");
         this.next();
         if (kind != null) {
            Property property = node.setProperty(kind, this.readText());
            property.setProperty("x", x);
            property.setProperty("y", y);
            property.setProperty("color", color);
         }
      }

   }

   protected void label(LscElement element) throws XMLStreamException {
      if (this.begin("label")) {
         Integer x = this.getCoordinate("x");
         Integer y = this.getCoordinate("y");
         Color color = this.getColor("color");
         String kind = this.getAttribute("kind");
         this.next();
         if (kind != null) {
            Property property = element.setProperty(kind, this.readText());
            if (element instanceof Message) {
               property.setProperty("x", x);
               if (x != null) {
                  property.setProperty("x", x);
                  int sx = ((Message)element).getSource().getX();
                  int tx = ((Message)element).getTarget().getX();
                  Float f = ((float)x - (float)sx) / (float)(tx - sx);
                  property.setProperty("f", f);
               }

               property.setProperty("y", y);
               property.setProperty("color", color);
            }
         }
      }

   }

   protected void temperature(Node node) throws XMLStreamException {
      this.begin("temperature");
      this.next();
      node.setProperty("hot", this.readText().equals("hot"));
   }

   protected void committed(Location location) throws XMLStreamException {
      if (this.begin("committed")) {
         this.next();
         location.setProperty("committed", true);
      }

   }

   protected void urgent(Location location) throws XMLStreamException {
      if (this.begin("urgent")) {
         this.next();
         location.setProperty("urgent", true);
      }

   }

   protected void init(Template template) throws XMLStreamException {
      if (this.begin("init")) {
         AbstractLocation location = (AbstractLocation)this.locations.get(this.getAttribute("ref"));
         if (location != null) {
            location.setProperty("init", true);
         }

         this.next();
      }

   }

   protected void locations(Template template) throws XMLStreamException {
      Node last = template.getLast();

      while(this.begin("location")) {
         Location location = template.createLocation();
         location.setProperty("x", this.getCoordinate("x"));
         location.setProperty("y", this.getCoordinate("y"));
         location.setProperty("color", this.getColor("color"));
         this.locations.put(this.getAttribute("id"), location);
         last = template.insert(location, last);
         this.next();
         this.name(location);
         this.labels(location);
         this.urgent(location);
         this.committed(location);
      }

   }

   protected void branchpoints(Template template) throws XMLStreamException {
      Node last = template.getLast();

      while(this.begin("branchpoint")) {
         BranchPoint branchPoint = template.createBranchPoint();
         branchPoint.setProperty("x", this.getCoordinate("x"));
         branchPoint.setProperty("y", this.getCoordinate("y"));
         branchPoint.setProperty("color", this.getColor("color"));
         this.locations.put(this.getAttribute("id"), branchPoint);
         last = template.insert(branchPoint, last);
         this.next();
      }

   }

   protected AbstractLocation source() throws XMLStreamException {
      if (this.begin("source")) {
         AbstractLocation location = (AbstractLocation)this.locations.get(this.getAttribute("ref"));
         this.next();
         return location;
      } else {
         return null;
      }
   }

   protected AbstractLocation target() throws XMLStreamException {
      if (this.begin("target")) {
         AbstractLocation location = (AbstractLocation)this.locations.get(this.getAttribute("ref"));
         this.next();
         return location;
      } else {
         return null;
      }
   }

   protected void nails(Edge edge) throws XMLStreamException {
      Node last = edge.getLast();

      while(this.begin("nail")) {
         Integer x = this.getCoordinate("x");
         Integer y = this.getCoordinate("y");
         Nail nail = edge.createNail();
         nail.setProperty("x", x);
         nail.setProperty("y", y);
         last = edge.insert(nail, last);
         this.next();
      }

   }

   protected String createEdgeId() {
      String id = this.getAttribute("id");
      int var10002;
      if (id != null) {
         if (id.isBlank()) {
            id = null;
         } else if (this.edges.containsKey(id)) {
            System.err.println("Duplicate edge id=\"" + id + "\" found, assuming that previous id was auto-generated: regenerating a new id for the previous one");
            Edge prevEdge = (Edge)this.edges.remove(id);

            String prevId;
            do {
               do {
                  var10002 = this.counter++;
                  prevId = "id" + var10002;
               } while(this.edges.containsKey(prevId));
            } while(this.locations.containsKey(prevId));

            this.edges.put(prevId, prevEdge);
         }
      }

      if (id == null) {
         do {
            do {
               var10002 = this.counter++;
               id = "id" + var10002;
            } while(this.edges.containsKey(id));
         } while(this.locations.containsKey(id));
      }

      return id;
   }

   protected void edges(Template template) throws XMLStreamException {
      Node last = template.getLast();

      while(true) {
         while(this.begin("edge") || this.begin("transition")) {
            String controllable = this.getAttribute("controllable");
            String id = this.createEdgeId();
            Color color = this.getColor("color");
            this.next();
            AbstractLocation s = this.source();
            AbstractLocation t = this.target();
            if (s != null && t != null) {
               Edge edge = template.createEdge();
               this.edges.put(id, edge);
               last = template.insert(edge, last);
               if (controllable != null && controllable.equals("false")) {
                  edge.setProperty("controllable", false);
               }

               edge.setSource(s);
               edge.setTarget(t);
               edge.setProperty("color", color);
               this.labels(edge);
               this.nails(edge);
            } else {
               System.err.println("Skipping edge due to invalid source or target");

               while(this.begin("label")) {
                  this.next();
               }

               while(this.begin("nail")) {
                  this.next();
               }
            }
         }

         return;
      }
   }

   protected void templates(Document document) throws XMLStreamException {
      Object last = document.getLastTATemplate();

      while(this.begin("template")) {
         this.next();
         Template template = document.createTemplate();
         last = document.insert(template, (Node)last);
         this.name(template);
         this.parameter(template);
         this.declaration(template);
         this.locations(template);
         this.branchpoints(template);
         this.init(template);
         this.edges(template);
      }

   }

   protected void lscTemplates(Document document) throws XMLStreamException {
      Node last = document.getLast();

      while(this.begin("lsc")) {
         this.instances.clear();
         this.yloccoords.clear();
         this.yConditions.clear();
         this.next();
         LscTemplate template = document.createLscTemplate();
         last = document.insert(template, last);
         this.name(template);
         this.parameter(template);
         this.type(template);
         this.mode(template);
         this.role(template);
         this.declaration(template);
         this.yloccoord(template);
         this.instances(template);
         this.prechart(template);
         this.messages(template);
         this.conditions(template);
         this.updates(template);
      }

   }

   protected void prechart(LscTemplate template) throws XMLStreamException {
      if (this.begin("prechart")) {
         Integer x = this.getCoordinate("x");
         Color color = this.getColor("color");
         this.next();
         Prechart prechart = template.createPrechart();
         prechart.setProperty("x", x);
         if (color != null) {
            prechart.setProperty("color", color);
         }

         this.yLocation(prechart, "y");
         Iterator var5 = this.instances.values().iterator();

         while(var5.hasNext()) {
            InstanceLine instance = (InstanceLine)var5.next();
            prechart.add(instance);
         }

         template.insert(prechart, template.getLast());
      }

   }

   protected void yLocation(LscElement element, String property) throws XMLStreamException {
      Integer number = this.getYLocation();
      if (number != null) {
         element.setProperty(property, this.yloccoords.get(number));
      }

   }

   protected Integer getYLocation() throws XMLStreamException {
      if (this.begin("lsclocation")) {
         this.next();
         return Integer.valueOf(this.readText());
      } else {
         return null;
      }
   }

   protected void yloccoord(LscTemplate template) throws XMLStreamException {
      while(this.begin("yloccoord")) {
         Integer y = this.getCoordinate("y");
         this.yloccoords.add(y);
         this.next();
      }

      Collections.sort(this.yloccoords);
      int max = (Integer)this.yloccoords.get(this.yloccoords.size() - 1);
      template.setProperty("length", max - 10);
   }

   protected void instances(LscTemplate template) throws XMLStreamException {
      Node last = template.getLast();

      while(this.begin("instance")) {
         InstanceLine instance = template.createInstanceLine();
         instance.setProperty("x", this.getCoordinate("x"));
         instance.setProperty("y", 0);
         instance.setProperty("color", this.getColor("color"));
         String id = this.getAttribute("id");
         this.instances.put(id, instance);
         last = template.insert(instance, last);
         this.next();
         this.fixedname(instance);
      }

   }

   protected void messages(LscTemplate template) throws XMLStreamException {
      Node last = template.getLast();

      while(true) {
         while(this.begin("message")) {
            Color color = this.getColor("color");
            Integer x = this.getCoordinate("x");
            this.next();
            InstanceLine s = this.messageSource();
            InstanceLine t = this.messageTarget();
            if (s != null && t != null) {
               Message message = template.createMessage();
               if (color != null) {
                  message.setProperty("color", color);
               }

               if (x != null) {
                  message.setProperty("x", x);
               }

               last = template.insert(message, last);
               message.setSource(s);
               message.setTarget(t);
               this.yLocation(message, "y");
               this.label(message);
            } else {
               System.err.println("Skipping message due to invalid source or target");

               while(this.begin("location")) {
                  this.next();
               }

               while(this.begin("label")) {
                  this.next();
               }
            }
         }

         return;
      }
   }

   protected void conditions(LscTemplate template) throws XMLStreamException {
      Node last = template.getLast();

      while(true) {
         while(this.begin("condition")) {
            Color color = this.getColor("color");
            Integer x = this.getCoordinate("x");
            this.next();
            Condition condition = template.createCondition();
            ArrayList<InstanceLine> anchors = this.anchors(condition);
            if (!anchors.isEmpty()) {
               if (x != null) {
                  condition.setProperty("x", x);
               }

               Integer number = this.getYLocation();
               if (number != null) {
                  Integer y = (Integer)this.yloccoords.get(number);
                  condition.setProperty("y", y);
                  this.yConditions.put(y, condition);
               }

               if (color != null) {
                  condition.setProperty("color", color);
               }

               condition.setAnchors(anchors);
               this.temperature(condition);
               this.label(condition);
               last = template.insert(condition, last);
            } else {
               System.err.println("Skipping condition due to invalid anchor");

               while(this.begin("temperature")) {
                  this.next();
               }

               while(this.begin("label")) {
                  this.next();
               }
            }
         }

         return;
      }
   }

   protected void updates(LscTemplate template) throws XMLStreamException {
      Node last = template.getLast();

      while(true) {
         while(this.begin("update")) {
            Color color = this.getColor("color");
            Integer x = this.getCoordinate("x");
            Integer y = 0;
            this.next();
            Update update = template.createUpdate();
            InstanceLine anchor = this.anchor(update);
            if (anchor != null) {
               update.setProperty("x", x);
               Integer number = this.getYLocation();
               if (number != null) {
                  y = (Integer)this.yloccoords.get(number);
               }

               if (this.yConditions.keySet().contains(y)) {
                  update.setAnchoredToCondition((Condition)this.yConditions.get(y));
               } else {
                  update.setProperty("y", y);
               }

               update.setProperty("color", color);
               update.setAnchor(anchor);
               this.label(update);
               last = template.insert(update, last);
            } else {
               System.err.println("Skipping update due to invalid anchor");

               while(this.begin("label")) {
                  this.next();
               }
            }
         }

         return;
      }
   }

   protected ArrayList<InstanceLine> anchors(Condition element) throws XMLStreamException {
      ArrayList anchors = new ArrayList();

      while(this.begin("anchor")) {
         InstanceLine instance = (InstanceLine)this.instances.get(this.getAttribute("instanceid"));
         this.next();
         anchors.add(instance);
      }

      return anchors;
   }

   protected InstanceLine anchor(Update element) throws XMLStreamException {
      this.begin("anchor");
      InstanceLine instance = (InstanceLine)this.instances.get(this.getAttribute("instanceid"));
      this.next();
      return instance;
   }

   protected InstanceLine messageSource() throws XMLStreamException {
      if (this.begin("source")) {
         InstanceLine instance = (InstanceLine)this.instances.get(this.getAttribute("ref"));
         this.next();
         return instance;
      } else {
         return null;
      }
   }

   protected InstanceLine messageTarget() throws XMLStreamException {
      if (this.begin("target")) {
         InstanceLine instance = (InstanceLine)this.instances.get(this.getAttribute("ref"));
         this.next();
         return instance;
      } else {
         return null;
      }
   }

   protected void instantiation(Document document) throws XMLStreamException {
      if (this.begin("instantiation")) {
         this.next();
         this.inst = this.readText();
      }

   }

   protected void system(Document document) throws XMLStreamException {
      if (this.begin("system")) {
         this.next();
         if (this.inst == null) {
            document.setProperty("system", this.readText());
         } else {
            String var10002 = this.inst;
            document.setProperty("system", var10002 + "\n" + this.readText());
            this.inst = null;
         }
      }

   }

   protected void simulator(Document document) throws XMLStreamException {
      if (this.begin("simulator")) {
         String type = this.getAttribute("type");
         if (!"concrete".equals(type)) {
            throw new XMLStreamException("Expected simulator type to be 'concrete' but got '" + type + "'");
         }

         this.next();
         ArrayList configurations = new ArrayList();

         while(this.begin("plot-widget")) {
            String name = this.getAttribute("title");
            this.next();
            ArrayList expressions = new ArrayList();

            while(this.begin("expression")) {
               this.next();
               expressions.add(this.readText());
            }

            configurations.add(new PlotConfiguration(name, expressions));
         }

         document.setConcretePlots(configurations);
      }

   }

   protected void queries(Document document) throws XMLStreamException {
      if (this.begin("queries")) {
         this.next();
         EngineSettings settings = this.options();
         if (settings != null) {
            document.setSettings(settings);
         }

         Query query;
         if (this.begin("query")) {
            for(; this.begin("query"); document.getQueryList().add(query)) {
               this.next();
               String formula = "";
               String comment = "";
               List<QueryResult> results = new ArrayList();
               QueryExpected expect = null;
               if (this.begin("formula")) {
                  this.next();
                  formula = this.readText();
               }

               if (this.begin("comment")) {
                  this.next();
                  comment = this.readText();
               }

               query = new Query(formula, comment);
               settings = this.options();
               if (settings != null) {
                  query.setSettings(settings);
               }

               if (this.begin("expect")) {
                  expect = this.expectation();
               }

               while(this.begin("result")) {
                  results.add(this.result());
               }

               if (expect != null) {
                  query.setExpected(expect);
               }

               if (results.isEmpty()) {
                  query.insert(new QueryResult(), query.getLast());
               } else {
                  Iterator var8 = results.iterator();

                  while(var8.hasNext()) {
                     QueryResult result = (QueryResult)var8.next();
                     query.insert(result, query.getLast());
                  }
               }
            }
         }
      }

   }

   protected void horizons(Document document) throws XMLStreamException {
      if (this.begin("concretesimulator")) {
         this.next();

         while(this.begin("plot")) {
            String title = this.getAttribute("title");
            this.next();
            Trajectory trajectory = new Trajectory(title);

            while(this.begin("series")) {
               Series series = new Series(this.getAttribute("title"), this.getAttribute("expr"));
               trajectory.addLast(series);
               this.next();
            }

            document.getTrajectoryList().addLast(trajectory);
         }
      }

   }

   private QueryResult result() throws XMLStreamException {
      String outcome = this.getAttribute("outcome");
      String type = this.getAttribute("type");
      String value = this.getAttribute("value");
      String timestamp = this.getAttribute("timestamp");
      this.next();
      QueryResult result = new QueryResult();
      result.getValue().setStatus(outcome);
      result.getValue().setKind(type);
      result.getValue().setValue(value);
      if (timestamp != null) {
         result.setTimestamp(timestamp);
      }

      EngineSettings settings = this.options();
      if (settings != null) {
         result.setSettings(settings);
      }

      if (this.begin("trace")) {
         this.next();
      }

      if (this.begin("details")) {
         this.next();
         result.setMessage(this.readText());
      }

      if (this.begin("samples")) {
         this.next();
      }

      while(this.begin("plot")) {
         result.addPlot(this.plot());
         this.next();
      }

      List<QueryResource> resources = this.resources();
      result.setResources(resources);
      return result;
   }

   private DataSet2D plot() throws XMLStreamException {
      String title = this.getAttribute("title");
      String xlabel = this.getAttribute("xaxis");
      String ylabel = this.getAttribute("yaxis");
      this.next();
      DataSet2D dataset = new DataSet2D(title, xlabel, ylabel);

      String comments;
      String comment;
      while(this.begin("series")) {
         comments = this.getAttribute("title");
         String type = this.getAttribute("type");
         String color = this.getAttribute("color");
         if (color == null) {
            dataset.addData(comments, type);
         } else {
            dataset.addData(comments, type, Color.decode(color));
         }

         String encoding = this.getAttribute("encoding");
         if (!encoding.equals("csv")) {
            throw new RuntimeException(String.format("Error: plot encoding %s not recognized", encoding));
         }

         this.next();
         comment = this.readText();
         this.parseCsv(comment, dataset);
         this.next();
      }

      if (this.begin("comment")) {
         this.next();
         comments = this.readText();
         String[] var10 = comments.split("\\n");
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            comment = var10[var12];
            dataset.addComment(comment);
         }
      }

      return dataset;
   }

   private void parseCsv(String data, DataSet2D dataset) {
      Scanner scanner = new Scanner(data.trim());

      while(scanner.hasNext()) {
         String line = scanner.nextLine();
         String[] fields = line.split(",");
         double x = Double.parseDouble(fields[0]);
         double y = Double.parseDouble(fields[1]);
         dataset.addSample(x, y);
      }

   }

   private QueryExpected expectation() throws XMLStreamException {
      String outcome;
      String type;
      String value;
      if ((outcome = this.getAttribute("outcome")) != null && (type = this.getAttribute("type")) != null && (value = this.getAttribute("value")) != null) {
         this.next();
         List resources = this.resources();
         return new QueryExpected(new QueryValue(outcome, type, value), resources);
      } else {
         throw new DocumentParseException("'expect' tag missing attributes 'outcome', 'type', and 'value'");
      }
   }

   private List<QueryResource> resources() throws XMLStreamException {
      ArrayList resources = new ArrayList();

      while(this.begin("resource")) {
         String resType = this.getAttribute("type");
         String resValue = this.getAttribute("value");
         String unit = this.getAttribute("unit");
         resources.add(unit == null ? new QueryResource(resType, resValue) : new QueryResource(resType, resValue, unit));
         this.next();
      }

      return resources;
   }

   static {
      UNKNOWN_TAG = LOCALE.getString("unknown_tag");
      known_tags = (Set)Stream.of("nta", "imports", "declaration", "template", "instantiation", "system", "name", "parameter", "location", "init", "transition", "edge", "urgent", "committed", "branchpoint", "source", "target", "label", "nail", "project", "lsc", "type", "mode", "yloccoord", "lsclocation", "prechart", "instance", "temperature", "message", "condition", "update", "anchor", "queries", "query", "formula", "comment", "option", "expect", "result", "value", "resource", "expect", "details", "samples", "plot", "title", "series", "concretesimulator", "title", "values", "simulator", "expression", "plot-widget").collect(Collectors.toSet());
   }
}
