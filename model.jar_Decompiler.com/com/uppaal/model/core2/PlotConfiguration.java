package com.uppaal.model.core2;

import java.util.List;

public class PlotConfiguration {
   public String title;
   public List<String> variables;

   public PlotConfiguration(String title, List<String> variables) {
      this.title = title;
      this.variables = variables;
   }
}
