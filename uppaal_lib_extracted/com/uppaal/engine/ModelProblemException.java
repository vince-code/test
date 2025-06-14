package com.uppaal.engine;

import com.uppaal.engine.protocol.viewmodel.ErrorMessage;
import com.uppaal.engine.protocol.viewmodel.ModelProblemsViewModel;
import java.util.List;

public class ModelProblemException extends Exception {
   private final List<ErrorMessage> errors;
   private final List<ErrorMessage> warnings;

   public List<ErrorMessage> getErrors() {
      return this.errors;
   }

   public List<ErrorMessage> getWarnings() {
      return this.warnings;
   }

   public boolean isEmpty() {
      return (this.errors == null || this.errors.isEmpty()) && (this.warnings == null || this.warnings.isEmpty());
   }

   public ModelProblemException(ModelProblemsViewModel errorInfo) {
      this.errors = errorInfo.getErrors();
      this.warnings = errorInfo.getWarnings();
   }
}
