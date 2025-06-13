package com.uppaal.model.system;

import com.uppaal.engine.protocol.viewmodel.GanttBarViewModel;

public class GanttAddition {
   private final int rowId;
   private final int value;

   public GanttAddition(GanttBarViewModel bar) {
      this.rowId = bar.id;
      this.value = bar.value;
   }

   public int getRowId() {
      return this.rowId;
   }

   public int getValue() {
      return this.value;
   }
}
