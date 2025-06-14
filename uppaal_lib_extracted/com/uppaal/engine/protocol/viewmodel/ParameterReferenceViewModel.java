package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;

public class ParameterReferenceViewModel {
   @SerializedName("par")
   private String formalParameter;
   @SerializedName("v")
   private String argument;

   public String getFormalParameter() {
      return this.formalParameter;
   }

   public String getArgument() {
      return this.argument;
   }
}
