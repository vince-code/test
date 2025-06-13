package com.uppaal.engine.protocol.viewmodel;

import com.uppaal.model.system.SystemEdgeSelect;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConcreteSuccessorQuery {
   ConcreteStateViewModel state;
   double delay;
   List<EdgeFieldViewModel> edges;
   double interval;

   public ConcreteSuccessorQuery(ConcreteStateViewModel state, double delay, SystemEdgeSelect[] edges, double interval) {
      this.state = state;
      this.delay = delay;
      this.edges = toEdgeFieldViewModel(edges);
      this.interval = interval;
   }

   private static List<EdgeFieldViewModel> toEdgeFieldViewModel(SystemEdgeSelect[] edges) {
      List<EdgeFieldViewModel> viewModelList = new ArrayList();
      SystemEdgeSelect[] var2 = edges;
      int var3 = edges.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SystemEdgeSelect edgeSelect = var2[var4];
         boolean exists = false;
         int procID = edgeSelect.getProcess().getIndex();
         Iterator var8 = viewModelList.iterator();

         while(var8.hasNext()) {
            EdgeFieldViewModel viewModel = (EdgeFieldViewModel)var8.next();
            int viewModelProcID = ((EdgeFieldViewModel.EdgePart)viewModel.getEdgeParts().get(0)).getProcessId();
            if (viewModelProcID == procID) {
               viewModel.addEdgePart(new EdgeFieldViewModel.EdgePart(procID, edgeSelect.getIndex(), edgeSelect.getSelectList()));
               exists = true;
               break;
            }
         }

         if (!exists) {
            viewModelList.add(new EdgeFieldViewModel(edgeSelect));
         }
      }

      return viewModelList;
   }
}
