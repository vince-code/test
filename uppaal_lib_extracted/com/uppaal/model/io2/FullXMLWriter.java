package com.uppaal.model.io2;

import com.uppaal.model.core2.PlotConfiguration;
import com.uppaal.model.core2.QueryList;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLStreamException;

public class FullXMLWriter extends XMLWriter {
   public FullXMLWriter(OutputStream stream) throws XMLStreamException {
      super(stream);
   }

   public void visitQueries(QueryList queries) throws Exception {
      this.indent();
      this.writer.writeStartElement("queries");
      ++this.depth;
      this.visitSettings(queries.getDocument().getSettings());
      super.visitQueries(queries);
      --this.depth;
      this.indent();
      this.writer.writeEndElement();
   }

   public void visitConcretePlotConfigs(List<PlotConfiguration> plots) throws Exception {
      if (plots != null && !plots.isEmpty()) {
         this.indent();
         this.writer.writeStartElement("simulator");
         this.writer.writeAttribute("type", "concrete");
         ++this.depth;
         Iterator var2 = plots.iterator();

         while(var2.hasNext()) {
            PlotConfiguration plot = (PlotConfiguration)var2.next();
            this.indent();
            this.writer.writeStartElement("plot-widget");
            this.writer.writeAttribute("title", plot.title);
            ++this.depth;
            Iterator var4 = plot.variables.iterator();

            while(var4.hasNext()) {
               String variable = (String)var4.next();
               this.indent();
               this.writer.writeStartElement("expression");
               this.writer.writeCharacters(variable);
               this.writer.writeEndElement();
            }

            --this.depth;
            this.indent();
            this.writer.writeEndElement();
         }

         --this.depth;
         this.indent();
         this.writer.writeEndElement();
      }
   }
}
