package com.uppaal.engine.protocol.viewmodel;

import java.util.List;

public class ModelProblemsViewModel {
   private List<ErrorMessage> errors;
   private List<ErrorMessage> warnings;

   public List<ErrorMessage> getErrors() {
      return this.errors;
   }

   public List<ErrorMessage> getWarnings() {
      return this.warnings;
   }

   public boolean isEmpty() {
      return (this.errors == null || this.errors.isEmpty()) && (this.warnings == null || this.warnings.isEmpty());
   }
}
