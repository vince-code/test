package com.uppaal.model.core2;

import com.uppaal.model.core2.lsc.Condition;
import com.uppaal.model.core2.lsc.Cut;
import com.uppaal.model.core2.lsc.InstanceLine;
import com.uppaal.model.core2.lsc.Message;
import com.uppaal.model.core2.lsc.Prechart;
import com.uppaal.model.core2.lsc.Update;
import java.util.Iterator;
import java.util.List;

public class AbstractVisitor implements Visitor {
   public void visitElement(Element element) throws Exception {
      if (element.properties != null) {
         Iterator var2 = element.properties.values().iterator();

         while(var2.hasNext()) {
            Property property = (Property)var2.next();
            property.accept(this);
         }
      }

   }

   public void visitNode(Node node) throws Exception {
      this.visitElement(node);

      for(node = node.first; node != null; node = node.next) {
         node.accept(this);
      }

   }

   public void visitProperty(Property property) throws Exception {
      this.visitElement(property);
   }

   public void visitDocument(Document document) throws Exception {
      this.visitNode(document);
      this.visitConcretePlotConfigs(document.getConcretePlots());
   }

   public void visitTemplate(AbstractTemplate template) throws Exception {
      this.visitNode(template);
   }

   public void visitLocation(Location location) throws Exception {
      this.visitNode(location);
   }

   public void visitBranchPoint(BranchPoint branchPoint) throws Exception {
      this.visitNode(branchPoint);
   }

   public void visitEdge(Edge edge) throws Exception {
      this.visitNode(edge);
   }

   public void visitNail(Nail nail) throws Exception {
      this.visitNode(nail);
   }

   public void visitInstanceLine(InstanceLine instance) throws Exception {
      this.visitNode(instance);
   }

   public void visitPrechart(Prechart prechart) throws Exception {
      this.visitNode(prechart);
   }

   public void visitMessage(Message message) throws Exception {
      this.visitNode(message);
   }

   public void visitUpdate(Update update) throws Exception {
      this.visitNode(update);
   }

   public void visitCondition(Condition condition) throws Exception {
      this.visitNode(condition);
   }

   public void visitCut(Cut cut) throws Exception {
      this.visitNode(cut);
   }

   public void visitQueries(QueryList queryList) throws Exception {
      Iterator var2 = queryList.iterator();

      while(var2.hasNext()) {
         Query query = (Query)var2.next();
         query.accept(this);
      }

   }

   public void visitQuery(Query query) throws Exception {
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
}
