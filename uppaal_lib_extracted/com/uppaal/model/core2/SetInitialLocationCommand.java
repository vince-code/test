package com.uppaal.model.core2;

import java.util.ArrayList;
import java.util.Iterator;

public class SetInitialLocationCommand extends AbstractCommand {
   protected SetPropertyCommand newLocationProperty;
   protected SetPropertyCommand oldLocationProperty;

   public SetInitialLocationCommand(Element location, boolean isInitial) {
      this.newLocationProperty = new SetPropertyCommand(location, "init", isInitial);
      Element oldInitialLocation = this.getInitialLocation(location.getTemplate());
      if (oldInitialLocation != null && oldInitialLocation != location) {
         this.oldLocationProperty = new SetPropertyCommand(oldInitialLocation, "init", !isInitial);
      }

   }

   private Location getInitialLocation(AbstractTemplate template) {
      ArrayList<Location> oldInitialLocations = this.findInitialLocations(template);
      if (oldInitialLocations.size() == 1) {
         return (Location)oldInitialLocations.get(0);
      } else {
         Iterator var3 = oldInitialLocations.iterator();

         while(var3.hasNext()) {
            Location location = (Location)var3.next();
            location.setProperty("init", (Object)null);
         }

         return null;
      }
   }

   private ArrayList<Location> findInitialLocations(AbstractTemplate template) {
      final ArrayList<Location> oldInitialLocations = new ArrayList();
      if (template != null) {
         template.acceptSafe(new AbstractVisitor() {
            public void visitLocation(Location location) {
               if ((Boolean)location.getPropertyValue("init")) {
                  oldInitialLocations.add(location);
               }

            }
         });
      }

      return oldInitialLocations;
   }

   public void execute() {
      this.newLocationProperty.execute();
      if (this.oldLocationProperty != null) {
         this.oldLocationProperty.execute();
      }

   }

   public void undo() {
      this.newLocationProperty.undo();
      if (this.oldLocationProperty != null) {
         this.oldLocationProperty.undo();
      }

   }

   public Element getModifiedElement() {
      return this.newLocationProperty.getModifiedElement();
   }
}
