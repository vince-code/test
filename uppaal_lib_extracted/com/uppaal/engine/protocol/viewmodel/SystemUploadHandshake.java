package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;
import com.uppaal.model.SupportedMethods;
import com.uppaal.model.system.GanttRow;
import java.util.List;

public class SystemUploadHandshake {
   private List<ErrorMessage> warnings;
   private SupportedMethods supportedMethods;
   @SerializedName("vars")
   private List<String> variableNames;
   @SerializedName("clocks")
   private List<String> clockNames;
   @SerializedName("procs")
   private List<ProcessViewModel> processes;
   @SerializedName("gantt")
   private List<GanttRow> ganttRows;

   public List<ErrorMessage> getWarnings() {
      return this.warnings;
   }

   public SupportedMethods getSupportedMethods() {
      return this.supportedMethods;
   }

   public List<String> getVariableNames() {
      return this.variableNames;
   }

   public List<String> getClockNames() {
      return this.clockNames;
   }

   public List<ProcessViewModel> getProcesses() {
      return this.processes;
   }

   public List<GanttRow> getGanttRows() {
      return this.ganttRows;
   }
}
