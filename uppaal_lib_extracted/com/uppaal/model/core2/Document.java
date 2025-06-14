package com.uppaal.model.core2;

import com.uppaal.model.core2.lsc.Condition;
import com.uppaal.model.core2.lsc.InstanceLine;
import com.uppaal.model.core2.lsc.LscTemplate;
import com.uppaal.model.core2.lsc.Message;
import com.uppaal.model.core2.lsc.Prechart;
import com.uppaal.model.core2.lsc.Update;
import com.uppaal.model.io2.BoundCalc;
import com.uppaal.model.io2.CachedOutputStream;
import com.uppaal.model.io2.FullXMLWriter;
import com.uppaal.model.io2.XTAWriter;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Document extends Node {
   public static final String SETTINGS = "settings";
   public static final String SETTINGS_CHANGED = "#settingsChanged";
   public static final String CONCRETE_SIMULATOR_PLOTS = "concrete-sim-plots";
   private static final Pattern node = Pattern.compile("(\\w+)(\\[((\\d+)|@kind=\"(\\w+)\")\\])?");
   public static final String[] labelKinds = new String[]{"invariant", "exponentialrate", "comments", "select", "guard", "synchronisation", "assignment", "probability", "message", "condition", "update", "name"};

   public Document() {
      super((Element)null);
   }

   public Document(Element prototype) {
      super(prototype);
      this.insert(new QueryList(this), this.getLast());
      this.setHorizonList(new TrajectoryList(this));
      this.setCommandManager(new CommandManager());
   }

   public static Document load(String location) throws IOException {
      new DocumentPrototype();
      return DocumentPrototype.load(location);
   }

   public EngineSettings getSettings() {
      return (EngineSettings)this.getPropertyValue("settings");
   }

   public final void setSettings(EngineSettings settings) {
      this.setProperty("settings", settings);
   }

   public void setHorizonList(TrajectoryList list) {
      Optional<TrajectoryList> old = this.getFirstInstance(TrajectoryList.class);
      if (old.isPresent()) {
         this.insert(list, (Node)old.get());
         ((TrajectoryList)old.get()).remove();
      } else {
         this.insert(list, this.getLast());
      }

   }

   public String getVersion() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.getCommandManager().getVersion());
      sb.append(',');
      this.addSubVersion(sb, this.getLocalProperty("declaration"));

      for(Node n = this.getFirst(); n != null; n = n.getNext()) {
         if (n instanceof AbstractTemplate) {
            this.addSubVersion(sb, n);
            this.addSubVersion(sb, n.getLocalProperty("declaration"));
         }
      }

      this.addSubVersion(sb, this.getLocalProperty("system"));
      return sb.toString();
   }

   private void addSubVersion(StringBuilder sb, Element element) {
      if (element != null && element.getCommandManager() != null) {
         sb.append(element.getCommandManager().getVersion());
         sb.append(',');
      } else {
         sb.append("1,");
      }

   }

   public QueryList getQueryList() {
      return (QueryList)this.getFirstInstance(QueryList.class).orElse((Object)null);
   }

   public TrajectoryList getTrajectoryList() {
      return (TrajectoryList)this.getFirstInstance(TrajectoryList.class).orElse((Object)null);
   }

   public Template createTemplate() {
      return new Template((Element)this.getPropertyValue("#template"));
   }

   public Template addTemplate() {
      Template t = new Template((Element)this.getPropertyValue("#template"));
      this.insert(t, this.getLastTATemplate());
      return t;
   }

   public LscTemplate createLscTemplate() {
      return new LscTemplate((Element)this.getPropertyValue("#lscTemplate"));
   }

   public AbstractTemplate getTemplates() {
      return (AbstractTemplate)this.first;
   }

   public List<AbstractTemplate> getTemplateList() {
      Node child = this.getFirst();

      ArrayList templates;
      for(templates = new ArrayList(); child != null; child = child.getNext()) {
         if (child instanceof AbstractTemplate) {
            templates.add((AbstractTemplate)child);
         }
      }

      return templates;
   }

   public AbstractTemplate findTemplate(String name) {
      if (name == null) {
         return null;
      } else {
         AbstractTemplate t;
         Node n;
         for(t = this.getTemplates(); t != null && !name.equals(t.getPropertyValue("name")); t = n instanceof AbstractTemplate ? (AbstractTemplate)n : null) {
            n = t.getNext();
         }

         return t;
      }
   }

   public AbstractTemplate getLastTATemplate() {
      Node child = this.getFirst();

      Node lastTemplate;
      for(lastTemplate = null; child != null; child = child.getNext()) {
         if (child instanceof Template) {
            lastTemplate = child;
         }
      }

      return lastTemplate == null ? null : (AbstractTemplate)lastTemplate;
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitDocument(this);
   }

   public Document getDocument() {
      return this;
   }

   private Node getChild(Element element, int index, Class<?> c) {
      if (!(element instanceof Node)) {
         return null;
      } else {
         Node child;
         for(child = ((Node)element).getFirst(); child != null && !c.isInstance(child); child = child.getNext()) {
         }

         while(child != null && index > 1) {
            do {
               child = child.getNext();
            } while(child != null && !c.isInstance(child));

            --index;
         }

         return child;
      }
   }

   public Element resolveXPath(String path) {
      Scanner scanner = new Scanner(path);
      Object current = this;

      while(scanner.hasNext() && current != null) {
         scanner.findInLine(node);
         MatchResult result = scanner.match();
         String token = result.group(1);
         String s = result.group(4);
         int index = s == null ? 1 : Integer.parseInt(s);
         String kind = result.group(5);
         byte var10 = -1;
         switch(token.hashCode()) {
         case -2050877650:
            if (token.equals("yloccoord")) {
               var10 = 21;
            }
            break;
         case -2028505734:
            if (token.equals("declaration")) {
               var10 = 2;
            }
            break;
         case -1727702954:
            if (token.equals("lscTemplate")) {
               var10 = 17;
            }
            break;
         case -1724158635:
            if (token.equals("transition")) {
               var10 = 11;
            }
            break;
         case -1413299531:
            if (token.equals("anchor")) {
               var10 = 27;
            }
            break;
         case -1321546630:
            if (token.equals("template")) {
               var10 = 3;
            }
            break;
         case -1291365605:
            if (token.equals("prechart")) {
               var10 = 23;
            }
            break;
         case -976263890:
            if (token.equals("branchpoint")) {
               var10 = 9;
            }
            break;
         case -896505829:
            if (token.equals("source")) {
               var10 = 14;
            }
            break;
         case -887328209:
            if (token.equals("system")) {
               var10 = 5;
            }
            break;
         case -880905839:
            if (token.equals("target")) {
               var10 = 15;
            }
            break;
         case -861311717:
            if (token.equals("condition")) {
               var10 = 25;
            }
            break;
         case -838846263:
            if (token.equals("update")) {
               var10 = 26;
            }
            break;
         case -836906175:
            if (token.equals("urgent")) {
               var10 = 13;
            }
            break;
         case -309310695:
            if (token.equals("project")) {
               var10 = 0;
            }
            break;
         case 109403:
            if (token.equals("nta")) {
               var10 = 1;
            }
            break;
         case 3237136:
            if (token.equals("init")) {
               var10 = 10;
            }
            break;
         case 3357091:
            if (token.equals("mode")) {
               var10 = 19;
            }
            break;
         case 3373590:
            if (token.equals("nail")) {
               var10 = 16;
            }
            break;
         case 3373707:
            if (token.equals("name")) {
               var10 = 6;
            }
            break;
         case 3506294:
            if (token.equals("role")) {
               var10 = 20;
            }
            break;
         case 3575610:
            if (token.equals("type")) {
               var10 = 18;
            }
            break;
         case 102727412:
            if (token.equals("label")) {
               var10 = 12;
            }
            break;
         case 321701236:
            if (token.equals("temperature")) {
               var10 = 28;
            }
            break;
         case 555127957:
            if (token.equals("instance")) {
               var10 = 22;
            }
            break;
         case 936545613:
            if (token.equals("instantiation")) {
               var10 = 4;
            }
            break;
         case 954925063:
            if (token.equals("message")) {
               var10 = 24;
            }
            break;
         case 1430243185:
            if (token.equals("lsclocation")) {
               var10 = 29;
            }
            break;
         case 1901043637:
            if (token.equals("location")) {
               var10 = 8;
            }
            break;
         case 1954460585:
            if (token.equals("parameter")) {
               var10 = 7;
            }
         }

         switch(var10) {
         case 0:
            current = this;
            break;
         case 1:
            current = this;
            break;
         case 2:
            current = ((Element)current).getProperty(token);
            break;
         case 3:
            current = this.getChild((Element)current, index, Template.class);
            break;
         case 4:
            current = ((Element)current).getProperty(token);
            break;
         case 5:
            current = ((Element)current).getProperty(token);
            break;
         case 6:
            current = ((Element)current).getProperty(token);
            break;
         case 7:
            current = ((Element)current).getProperty(token);
            break;
         case 8:
            current = this.getChild((Element)current, index, Location.class);
            break;
         case 9:
            current = this.getChild((Element)current, index, BranchPoint.class);
         case 10:
         case 14:
         case 15:
         case 21:
         case 27:
            break;
         case 11:
            current = this.getChild((Element)current, index, Edge.class);
            break;
         case 12:
            String[] kinds = new String[]{"invariant", "exponentialrate", "comments", "select", "guard", "synchronisation", "assignment", "probability", "message", "condition", "update"};
            if (kind != null) {
               current = ((Element)current).getLocalProperty(kind);
               break;
            }

            Element child = null;
            String[] var13 = kinds;
            int var14 = kinds.length;
            int var15 = 0;

            for(; var15 < var14; ++var15) {
               String k = var13[var15];
               if (((Element)current).isPropertyLocal(k)) {
                  if (index == 1) {
                     child = ((Element)current).getProperty(k);
                  }

                  --index;
               }
            }

            current = child;
            break;
         case 13:
            current = ((Element)current).getProperty(token);
            break;
         case 16:
            current = this.getChild((Element)current, index, Nail.class);
            break;
         case 17:
            current = this.getChild((Element)current, index, LscTemplate.class);
            break;
         case 18:
            current = ((Element)current).getProperty(token);
            break;
         case 19:
            current = ((Element)current).getProperty(token);
            break;
         case 20:
            current = ((Element)current).getProperty(token);
            break;
         case 22:
            current = this.getChild((Element)current, index, InstanceLine.class);
            break;
         case 23:
            current = this.getChild((Element)current, index, Prechart.class);
            break;
         case 24:
            current = this.getChild((Element)current, index, Message.class);
            break;
         case 25:
            current = this.getChild((Element)current, index, Condition.class);
            break;
         case 26:
            current = this.getChild((Element)current, index, Update.class);
            break;
         case 28:
            current = ((Element)current).getProperty(token);
            break;
         case 29:
            current = ((Element)current).getProperty(token);
            break;
         default:
            return null;
         }
      }

      return (Element)current;
   }

   public void save(File file) throws IOException {
      OutputStream out = new CachedOutputStream(file);
      CachedOutputStream ugi = null;

      try {
         String s = file.getName().toLowerCase();
         if (s.endsWith(".xml")) {
            this.saveXML(out);
         } else {
            if (!s.endsWith(".xta")) {
               throw new IOException("The file extension was not recognised (Only .xml and .xta are supported).");
            }

            File ugiFile = new File(file.getAbsoluteFile().getParentFile(), s.replaceFirst(".xta$", ".ugi"));
            ugi = new CachedOutputStream(ugiFile);
            this.saveXTA(out, ugi);
         }
      } catch (IOException var10) {
         throw var10;
      } catch (Exception var11) {
         throw new IOException(var11.getMessage(), var11);
      } finally {
         out.close();
         if (ugi != null) {
            ugi.close();
         }

      }

   }

   public void saveXML(OutputStream out) throws IOException {
      try {
         this.accept(new FullXMLWriter(out));
      } catch (IOException var3) {
         throw var3;
      } catch (Exception var4) {
         var4.printStackTrace(System.err);
         throw new IOException(var4.getMessage());
      }
   }

   public void saveXTA(OutputStream xta, OutputStream ugi) throws IOException {
      try {
         XTAWriter writer = new XTAWriter(xta);
         this.accept(writer);
         if (ugi != null) {
            this.accept(writer.createUGIWriter(ugi));
         }

      } catch (IOException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new IOException(var5.getMessage(), var5);
      }
   }

   public static boolean merge(AbstractTemplate source, AbstractTemplate target) {
      ArrayList<Command> cmd = new ArrayList();
      if (target instanceof Template) {
         if (source instanceof Template) {
            BoundCalc measurer = new BoundCalc();
            Rectangle2D tb = measurer.getBounds(target);
            Rectangle2D sb = measurer.getBounds(source);
            if (sb != null && tb != null) {
               final int dx = (int)Math.round(tb.getMaxX() - sb.getMinX());
               final int dy = (int)Math.round(tb.getMinY() - sb.getMinY());
               source.acceptSafe(new AbstractVisitor() {
                  public void visitElement(Element element) throws Exception {
                     super.visitElement(element);
                     if (element.isPropertyLocal("x")) {
                        element.setProperty("x", element.getX() + dx);
                     }

                     if (element.isPropertyLocal("y")) {
                        element.setProperty("y", element.getY() + dy);
                     }

                  }
               });
            }

            boolean initExists = false;

            Node child;
            for(child = target.getFirst(); child != null; child = child.getNext()) {
               if (child.hasFlag("init")) {
                  initExists = true;
               }
            }

            for(child = source.getFirst(); child != null; child = source.getFirst()) {
               child.remove();
               child.importInto(target);
               if (initExists && child.hasFlag("init")) {
                  child.setProperty("init", (Object)null);
               }

               cmd.add(new InsertElementCommand(target.getCommandManager(), target, target.getLast(), child));
            }

            if (cmd.isEmpty()) {
               return false;
            }

            Command[] commands = (Command[])cmd.toArray(new Command[cmd.size()]);
            target.getCommandManager().execute(commands);
            return true;
         }
      } else if (target instanceof LscTemplate && source instanceof LscTemplate) {
         return false;
      }

      return false;
   }

   public void save(String path) throws IOException {
      this.save(new File(path));
   }

   public String getXPathTag() {
      return "/nta";
   }

   public String getFriendlyName() {
      return "Project";
   }

   public List<PlotConfiguration> getConcretePlots() {
      return (List)this.getPropertyValue("concrete-sim-plots");
   }

   public void setConcretePlots(List<PlotConfiguration> plots) {
      this.getCommandManager().execute((Command)(new SetPropertyCommand(this, "concrete-sim-plots", plots)));
   }
}
