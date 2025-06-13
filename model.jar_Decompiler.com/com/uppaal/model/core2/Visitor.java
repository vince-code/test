package com.uppaal.model.core2;

import com.uppaal.model.core2.lsc.Condition;
import com.uppaal.model.core2.lsc.Cut;
import com.uppaal.model.core2.lsc.InstanceLine;
import com.uppaal.model.core2.lsc.Message;
import com.uppaal.model.core2.lsc.Prechart;
import com.uppaal.model.core2.lsc.Update;
import java.util.List;

public interface Visitor {
   void visitElement(Element var1) throws Exception;

   void visitNode(Node var1) throws Exception;

   void visitProperty(Property var1) throws Exception;

   void visitDocument(Document var1) throws Exception;

   void visitTemplate(AbstractTemplate var1) throws Exception;

   void visitLocation(Location var1) throws Exception;

   void visitBranchPoint(BranchPoint var1) throws Exception;

   void visitEdge(Edge var1) throws Exception;

   void visitNail(Nail var1) throws Exception;

   void visitInstanceLine(InstanceLine var1) throws Exception;

   void visitPrechart(Prechart var1) throws Exception;

   void visitMessage(Message var1) throws Exception;

   void visitUpdate(Update var1) throws Exception;

   void visitCondition(Condition var1) throws Exception;

   void visitCut(Cut var1) throws Exception;

   void visitQueries(QueryList var1) throws Exception;

   void visitQuery(Query var1) throws Exception;

   void visitSettings(EngineSettings var1) throws Exception;

   void visitResults(QueryResult var1) throws Exception;

   void visitExpect(QueryExpected var1) throws Exception;

   void visitResourceList(List<QueryResource> var1) throws Exception;

   void visitQueryValue(QueryValue var1) throws Exception;

   void visitConcretePlotConfigs(List<PlotConfiguration> var1) throws Exception;
}
