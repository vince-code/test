package com.uppaal.model.core2;

import com.uppaal.model.core2.lsc.Condition;
import com.uppaal.model.core2.lsc.Cut;
import com.uppaal.model.core2.lsc.InstanceLine;
import com.uppaal.model.core2.lsc.Message;
import com.uppaal.model.core2.lsc.Prechart;
import com.uppaal.model.core2.lsc.Update;
import java.util.ArrayList;
import java.util.List;

public class XPathBuilder implements Visitor {
   ArrayList<String> result = new ArrayList();

   public String getXPath(Element e) {
      this.result.clear();

      try {
         e.accept(this);
      } catch (Exception var4) {
         var4.printStackTrace(System.err);
      }

      StringBuffer s = new StringBuffer();

      for(int i = this.result.size() - 1; i >= 0; --i) {
         s.append((String)this.result.get(i));
      }

      return s.toString();
   }

   public void visitElement(Element element) throws Exception {
      this.result.add("/");
      Element parent = element.getParent();
      if (parent != null) {
         parent.accept(this);
      }

   }

   public void visitNode(Node node) throws Exception {
      this.result.add("node");
      this.visitElement(node);
   }

   public void visitProperty(Property property) throws Exception {
      String name = property.getName();
      boolean found = false;
      String[] var4 = Document.labelKinds;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String k = var4[var6];
         if (name.equals(k)) {
            this.result.add("label[@kind=\"" + name + "\"]");
            found = true;
            break;
         }
      }

      if (!found) {
         this.result.add(name);
      }

      this.visitElement(property);
   }

   public void visitQueries(QueryList qlist) throws Exception {
      this.result.add("queries");
      this.visitElement(qlist);
   }

   public void visitQuery(Query query) throws Exception {
      this.result.add("query");
      this.visitElement(query);
   }

   public void visitSettings(EngineSettings settings) throws Exception {
   }

   public void visitResults(QueryResult result) throws Exception {
   }

   public void visitExpect(QueryExpected expect) throws Exception {
   }

   public void visitResourceList(List<QueryResource> resources) throws Exception {
   }

   public void visitQueryValue(QueryValue value) throws Exception {
   }

   public void visitConcretePlotConfigs(List<PlotConfiguration> plots) throws Exception {
   }

   public void visitDocument(Document document) throws Exception {
      this.result.add("nta");
      this.visitElement(document);
   }

   public int getChildNumber(Node node) {
      int i;
      for(i = 0; node != null; ++i) {
         node = node.getPrevious();
      }

      return i;
   }

   public int getLocationNumber(Node node) {
      int i;
      for(i = 0; node != null; node = node.getPrevious()) {
         if (node instanceof Location) {
            ++i;
         }
      }

      return i;
   }

   public int getEdgeNumber(Node node) {
      int i;
      for(i = 0; node != null; node = node.getPrevious()) {
         if (node instanceof Edge) {
            ++i;
         }
      }

      return i;
   }

   public void visitTemplate(AbstractTemplate template) throws Exception {
      ArrayList var10000 = this.result;
      int var10001 = this.getChildNumber(template);
      var10000.add("template[" + var10001 + "]");
      this.visitElement(template);
   }

   public void visitLocation(Location location) throws Exception {
      ArrayList var10000 = this.result;
      int var10001 = this.getLocationNumber(location);
      var10000.add("location[" + var10001 + "]");
      this.visitElement(location);
   }

   public void visitBranchPoint(BranchPoint branchPoint) throws Exception {
      ArrayList var10000 = this.result;
      int var10001 = this.getChildNumber(branchPoint);
      var10000.add("branchpoint[" + var10001 + "]");
      this.visitElement(branchPoint);
   }

   public void visitEdge(Edge edge) throws Exception {
      ArrayList var10000 = this.result;
      int var10001 = this.getEdgeNumber(edge);
      var10000.add("transition[" + var10001 + "]");
      this.visitElement(edge);
   }

   public void visitNail(Nail nail) throws Exception {
      ArrayList var10000 = this.result;
      int var10001 = this.getChildNumber(nail);
      var10000.add("nail[" + var10001 + "]");
      this.visitElement(nail);
   }

   public void visitInstanceLine(InstanceLine instance) throws Exception {
      ArrayList var10000 = this.result;
      int var10001 = this.getChildNumber(instance);
      var10000.add("instance[" + var10001 + "]");
      this.visitElement(instance);
   }

   public void visitPrechart(Prechart prechart) throws Exception {
      ArrayList var10000 = this.result;
      int var10001 = this.getChildNumber(prechart);
      var10000.add("prechart[" + var10001 + "]");
      this.visitElement(prechart);
   }

   public void visitMessage(Message message) throws Exception {
      ArrayList var10000 = this.result;
      int var10001 = this.getChildNumber(message);
      var10000.add("message[" + var10001 + "]");
      this.visitElement(message);
   }

   public void visitUpdate(Update update) throws Exception {
      ArrayList var10000 = this.result;
      int var10001 = this.getChildNumber(update);
      var10000.add("update[" + var10001 + "]");
      this.visitElement(update);
   }

   public void visitCondition(Condition condition) throws Exception {
      ArrayList var10000 = this.result;
      int var10001 = this.getChildNumber(condition);
      var10000.add("condition[" + var10001 + "]");
      this.visitElement(condition);
   }

   public void visitCut(Cut cut) throws Exception {
      ArrayList var10000 = this.result;
      int var10001 = this.getChildNumber(cut);
      var10000.add("cut[" + var10001 + "]");
      this.visitElement(cut);
   }
}
