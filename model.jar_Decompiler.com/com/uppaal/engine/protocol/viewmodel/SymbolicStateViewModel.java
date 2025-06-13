package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;
import com.uppaal.model.AbstractSystemLocation;
import com.uppaal.model.system.symbolic.SymbolicState;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolicStateViewModel {
   @SerializedName("locs")
   List<Integer> locations;
   @SerializedName("dbm")
   List<DBMConstraintViewModel> dbm;
   @SerializedName("vars")
   List<Integer> variableValues;

   public List<Integer> getLocations() {
      return this.locations;
   }

   public List<Integer> getVariableValues() {
      return this.variableValues;
   }

   public List<DBMConstraintViewModel> getDbm() {
      return this.dbm;
   }

   public SymbolicStateViewModel() {
   }

   public SymbolicStateViewModel(SymbolicState state) {
      this.locations = (List)Arrays.stream(state.getLocations()).map(AbstractSystemLocation::getIndex).collect(Collectors.toList());
      this.dbm = (List)state.getPolyhedron().getRawConstraintList().stream().map((cons) -> {
         return new DBMConstraintViewModel(cons.i, cons.j, cons.bound ^ 1);
      }).collect(Collectors.toList());
      this.variableValues = (List)Arrays.stream(state.getVariableValues()).boxed().collect(Collectors.toList());
   }
}
