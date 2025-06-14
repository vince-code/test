package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;
import com.uppaal.model.system.SystemEdgeSelect;
import java.util.ArrayList;
import java.util.List;

public class EdgeFieldViewModel {
   @SerializedName("parts")
   private List<EdgeFieldViewModel.EdgePart> edgeParts = new ArrayList();

   public EdgeFieldViewModel() {
   }

   public void addEdgePart(EdgeFieldViewModel.EdgePart part) {
      this.edgeParts.add(part);
   }

   public EdgeFieldViewModel(SystemEdgeSelect edge) {
      this.edgeParts.add(new EdgeFieldViewModel.EdgePart(edge.getProcess().getIndex(), edge.getIndex(), edge.getSelectList()));
   }

   public List<EdgeFieldViewModel.EdgePart> getEdgeParts() {
      return this.edgeParts;
   }

   public static class EdgePart {
      @SerializedName("procnum")
      private int processId;
      @SerializedName("eid")
      private int edgeId;
      @SerializedName("sel_vals")
      private List<Integer> selectValues;

      public EdgePart() {
      }

      public EdgePart(int processId, int edgeId, List<Integer> selectValues) {
         this.processId = processId;
         this.edgeId = edgeId;
         this.selectValues = selectValues;
      }

      public int getProcessId() {
         return this.processId;
      }

      public int getEdgeId() {
         return this.edgeId;
      }

      public List<Integer> getSelectValues() {
         return this.selectValues;
      }
   }
}
