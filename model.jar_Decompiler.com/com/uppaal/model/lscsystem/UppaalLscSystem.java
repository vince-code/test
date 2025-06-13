package com.uppaal.model.lscsystem;

import com.uppaal.model.AbstractSystem;
import com.uppaal.model.Translator;
import com.uppaal.model.core2.AbstractVisitor;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Node;
import com.uppaal.model.core2.lsc.Condition;
import com.uppaal.model.core2.lsc.InstanceLine;
import com.uppaal.model.core2.lsc.LscTemplate;
import com.uppaal.model.core2.lsc.Message;
import com.uppaal.model.core2.lsc.Prechart;
import com.uppaal.model.core2.lsc.Update;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UppaalLscSystem extends AbstractSystem {
   private Map<String, LscTemplate> templates = new HashMap();
   private ArrayList<LscProcess> processes = new ArrayList();

   public UppaalLscSystem() {
   }

   public UppaalLscSystem(Document aDocument) {
      super(aDocument);

      for(Node element = this.document.getFirst(); element != null; element = element.getNext()) {
         this.templates.put((String)element.getPropertyValue("name"), (LscTemplate)element);
      }

   }

   public void addProcess(String processName, String templateName, Translator map) {
      templateName = templateName.trim();
      processName = processName.trim();

      Node node;
      for(node = this.document.getFirst(); node != null && !templateName.equals(((String)node.getPropertyValue("name")).trim()); node = node.getNext()) {
      }

      assert node != null : "Template " + templateName + " not found.";

      final LscProcess p = new LscProcess(processName, this.processes.size(), (LscTemplate)node, map);
      node.acceptSafe(new AbstractVisitor() {
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
      this.processes.add(p);
   }

   public LscProcess getProcess(int process) {
      return (LscProcess)this.processes.get(process);
   }

   public SystemInstanceLine getInstance(int process, int i) {
      return this.getProcess(process).getInstanceLine(i);
   }

   public SystemMessage getMessage(int process, int i) {
      return this.getProcess(process).getMessage(i);
   }

   public SystemCondition getCondition(int process, int i) {
      return this.getProcess(process).getCondition(i);
   }

   public SystemUpdate getUpdate(int process, int i) {
      return this.getProcess(process).getUpdate(i);
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
}
