package com.uppaal.engine.protocol.viewmodel;

import java.util.List;

public class DataSetViewModel {
   private String title;
   private String type;
   private String color;
   private List<PointFieldViewModel> points;

   public String getTitle() {
      return this.title;
   }

   public String getType() {
      return this.type;
   }

   public String getColor() {
      return this.color;
   }

   public List<PointFieldViewModel> getPoints() {
      return this.points;
   }
}
