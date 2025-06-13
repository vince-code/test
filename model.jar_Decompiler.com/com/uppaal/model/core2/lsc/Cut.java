package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Visitor;
import java.awt.Point;
import java.util.ArrayList;

public class Cut extends LscElement {
   protected ArrayList<Simregion> simregions;

   public Cut(Element prototype) {
      super(prototype);
   }

   public void add(Simregion simregion) {
      this.simregions.add(simregion);
   }

   public Simregion get(int index) {
      return (Simregion)this.simregions.get(index);
   }

   public ArrayList<Simregion> getSimregions() {
      return this.simregions;
   }

   public void setSimregions(ArrayList<Simregion> simregions) {
      this.simregions = simregions;
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitCut(this);
   }

   public Element getPrototypeFromParent(Element parent) {
      return (Element)parent.getPropertyValue("#cut");
   }

   public String getFriendlyName() {
      return "cut";
   }

   public ArrayList<Point> getMaxSimregions(ViewWorkAround view) {
      return view.getMaxSimregions(this.simregions);
   }
}
