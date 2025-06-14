package com.uppaal.model.system;

import com.uppaal.model.AbstractProcess;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Template;
import java.util.ArrayList;

public class Process extends AbstractProcess {
   private final ArrayList<SystemLocation> locations = new ArrayList();
   private final ArrayList<SystemEdge> edges = new ArrayList();

   public Process(String name, int index, Template template, com.uppaal.model.Translator translator) {
      super(name, index, template, translator);
   }

   void addEdge(Edge edge) {
      String sync = (String)edge.getPropertyValue("synchronisation");
      if (sync != null && sync.length() > 0) {
         sync = this.translator.translate(sync);
      }

      this.edges.add(new SystemEdge(this, this.edges.size(), sync, edge));
   }

   void addLocation(Location location) {
      this.locations.add(new SystemLocation(this, this.locations.size(), location));
   }

   public SystemEdge getEdge(int edge) {
      return (SystemEdge)this.edges.get(edge);
   }

   public final ArrayList<SystemEdge> getEdges() {
      return this.edges;
   }

   public SystemLocation getLocation(int loc) {
      return (SystemLocation)this.locations.get(loc);
   }

   public ArrayList<SystemLocation> getLocations() {
      return this.locations;
   }
}
