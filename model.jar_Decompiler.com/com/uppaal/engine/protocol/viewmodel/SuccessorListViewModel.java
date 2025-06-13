package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SuccessorListViewModel {
   @SerializedName("succ")
   private List<SuccessorViewModel> successors;
   private transient ErrorMessage terminatingError;

   public List<SuccessorViewModel> getSuccessors() {
      return this.successors;
   }
}
