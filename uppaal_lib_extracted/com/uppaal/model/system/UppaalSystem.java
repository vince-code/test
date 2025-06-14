package com.uppaal.model.system;

import com.uppaal.model.AbstractSystem;
import com.uppaal.model.SupportedMethods;
import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.AbstractVisitor;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Node;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.lsc.Condition;
import com.uppaal.model.core2.lsc.InstanceLine;
import com.uppaal.model.core2.lsc.LscTemplate;
import com.uppaal.model.core2.lsc.Message;
import com.uppaal.model.core2.lsc.Prechart;
import com.uppaal.model.core2.lsc.Update;
import com.uppaal.model.lscsystem.LscProcess;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UppaalSystem extends AbstractSystem {
   private final ArrayList<Process> processes = new ArrayList();
   private final Map<String, AbstractTemplate> templates = new HashMap();
   private GanttChart ganttchart;
   private SupportedMethods supportedMethods;

   public UppaalSystem(Document aDocument) {
      super(aDocument);
      Iterator var2 = this.document.getTemplateList().iterator();

      while(var2.hasNext()) {
         AbstractTemplate template = (AbstractTemplate)var2.next();
         this.templates.put((String)template.getPropertyValue("name"), template);
      }

   }

   protected Node findNode(String name) {
      Node node;
      for(node = this.document.getFirst(); node != null; node = node.getNext()) {
         String nodeName = (String)node.getPropertyValue("name");
         if (nodeName != null && name.equals(nodeName.trim())) {
            break;
         }
      }

      return node;
   }

   protected Template findTemplate(String name, boolean guarantee) {
      Template template = (Template)this.findNode(name);
      if (template == null && guarantee) {
         throw new RuntimeException("Template \"" + name + "\" not found.");
      } else {
         return template;
      }
   }

   protected LscTemplate findLscTemplate(String name, boolean guarantee) {
      LscTemplate template = (LscTemplate)this.findNode(name);
      if (template == null && guarantee) {
         throw new RuntimeException("LscTemplate \"" + name + "\" not found.");
      } else {
         return template;
      }
   }

   public void addProcess(String processName, String templateName, com.uppaal.model.Translator map) {
      String tName = templateName.trim();
      String pName = processName.trim();
      Template template = this.findTemplate(templateName, true);
      final Process p = new Process(pName, this.processes.size(), template, map);
      template.acceptSafe(new AbstractVisitor() {
         public void visitLocation(Location location) {
            p.addLocation(location);
         }

         public void visitEdge(Edge edge) {
            p.addEdge(edge);
         }
      });
      this.processes.add(p);
   }

   public void addProcess(String processName, String templateName, com.uppaal.model.Translator parameterMap, List<Integer> edgeMap) {
      String tName = templateName.trim();
      String pName = processName.trim();
      Template template = this.findTemplate(templateName, true);
      final Process p = new Process(processName, this.processes.size(), template, parameterMap);
      template.acceptSafe(new AbstractVisitor() {
         public void visitLocation(Location location) {
            p.addLocation(location);
         }
      });
      new ArrayList();
      template.acceptSafe(new AbstractVisitor() {
         public void visitEdge(Edge edge) {
            p.addEdge(edge);
         }
      });
      this.processes.add(p);
   }

   public void setLscProcess(String processName, String templateName, com.uppaal.model.Translator map) {
      String tName = templateName.trim();
      String pName = processName.trim();
      LscTemplate template = this.findLscTemplate(tName, true);
      final LscProcess p = new LscProcess(processName, this.processes.size(), template, map);
      template.acceptSafe(new AbstractVisitor() {
         public void visitPrechart(Prechart prechart) {
            p.setPrechart(prechart);
         }

         public void visitMessage(Message message) {
            p.addMessage(message);
         }

         public void visitCondition(Condition condition) {
            p.addCondition(condition);
         }

         public void visitUpdate(Update update) {
            p.addUpdate(update);
         }

         public void visitInstanceLine(InstanceLine instance) {
            p.addInstanceLine(instance);
         }
      });
   }

   public Process getProcess(int process) {
      return (Process)this.processes.get(process);
   }

   public SystemEdge getEdge(int process, int edge) {
      return this.getProcess(process).getEdge(edge);
   }

   public ArrayList<Process> getProcesses() {
      return this.processes;
   }

   public SystemLocation getLocation(int process, int loc) {
      return this.getProcess(process).getLocation(loc);
   }

   public int getNoOfProcesses() {
      return this.processes.size();
   }

   public int getProcessIndex(String id) {
      for(int i = 0; i < this.processes.size(); ++i) {
         if (id.equals(this.getProcess(i).getName())) {
            return i;
         }
      }

      return -1;
   }

   public SystemEdgeSelect createEdgeCon(int process, int edge, List<Integer> values) {
      return new SystemEdgeSelect(this.getProcess(process).getEdge(edge), values);
   }

   public void setGanttChart(GanttChart gc) {
      this.ganttchart = gc;
   }

   public GanttChart getGanttChart() {
      return this.ganttchart;
   }

   public void setSupportedMethods(SupportedMethods st) {
      this.supportedMethods = st;
   }

   public SupportedMethods getSupportedMethods() {
      return this.supportedMethods;
   }
}
