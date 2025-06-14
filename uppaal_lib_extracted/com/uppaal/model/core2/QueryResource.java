package com.uppaal.model.core2;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryResource {
   private static final List<String> timeUnits = (List)Stream.of("s", "m", "h", "d").collect(Collectors.toList());
   private static final List<String> memoryUnits = (List)Stream.of("B", "KB", "MB", "GB", "TB", "KiB", "MiB", "GiB", "TiB").collect(Collectors.toList());
   public String type;
   public String value;
   public String unit;

   public QueryResource(String type, String value, String unit) {
      this.type = type;
      this.value = value;
      this.unit = unit;
   }

   public QueryResource(String type, String value) {
      this.type = type;
      this.value = value;
   }
}
