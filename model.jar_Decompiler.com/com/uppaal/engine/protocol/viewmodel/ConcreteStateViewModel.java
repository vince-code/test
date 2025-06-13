package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;
import com.uppaal.model.system.concrete.ConcreteState;
import com.uppaal.model.system.concrete.Limit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConcreteStateViewModel {
   @SerializedName("locs")
   public List<Integer> locationIndices;
   @SerializedName("vars")
   public List<Integer> variableValues;
   @SerializedName("fpvars")
   public List<Double> floatingVariableValues;
   @SerializedName("clocks")
   public List<Double> clocks;
   @SerializedName("lim")
   public Limit maxDelay;

   public ConcreteStateViewModel() {
   }

   public ConcreteStateViewModel(ConcreteState state) {
      assert state != null;

      this.locationIndices = (List)Arrays.stream(state.getLocations()).map((loc) -> {
         return loc.getIndex();
      }).collect(Collectors.toList());
      this.maxDelay = state.getInvariant();
      this.variableValues = (List)Arrays.stream(state.getVars()).boxed().collect(Collectors.toList());
      this.floatingVariableValues = (List)Arrays.stream(state.getFPVars()).boxed().collect(Collectors.toList());
      this.clocks = (List)Arrays.stream(state.getClocks()).boxed().collect(Collectors.toList());
   }

   private boolean CompareLists(List<?> listOne, List<?> listTwo) {
      if (listOne.size() != listTwo.size()) {
         return false;
      } else {
         for(int i = 0; i < listOne.size(); ++i) {
            if (!listOne.get(i).equals(listTwo.get(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean equals(Object obj) {
      if (obj.getClass() != ConcreteStateViewModel.class) {
         return false;
      } else {
         ConcreteStateViewModel other = (ConcreteStateViewModel)obj;
         if (!this.CompareLists(this.locationIndices, other.locationIndices)) {
            return false;
         } else if (!this.CompareLists(this.variableValues, other.variableValues)) {
            return false;
         } else if (!this.CompareLists(this.floatingVariableValues, other.floatingVariableValues)) {
            return false;
         } else if (!this.CompareLists(this.clocks, other.clocks)) {
            return false;
         } else {
            return this.maxDelay.equals(other.maxDelay);
         }
      }
   }
}
