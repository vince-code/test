package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlotViewModel {
   @SerializedName("title")
   private String title;
   @SerializedName("xlabel")
   private String xlabel;
   @SerializedName("ylabel")
   private String ylabel;
   @SerializedName("data")
   private List<DataSetViewModel> dataSeries;
   @SerializedName("comments")
   private String comments;

   public String getTitle() {
      return this.title;
   }

   public String getXlabel() {
      return this.xlabel;
   }

   public String getYlabel() {
      return this.ylabel;
   }

   public List<DataSetViewModel> getDataSeries() {
      return this.dataSeries;
   }

   public String getComments() {
      return this.comments;
   }
}
