package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProcessViewModel {
   @SerializedName("name")
   private String procName;
   @SerializedName("templ")
   private String templateName;
   @SerializedName("args")
   private List<ParameterReferenceViewModel> parameterReferences;

   public String getProcName() {
      return this.procName;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public List<ParameterReferenceViewModel> getParameterReferences() {
      return this.parameterReferences;
   }
}
