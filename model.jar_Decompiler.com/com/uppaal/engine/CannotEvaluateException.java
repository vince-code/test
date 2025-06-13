package com.uppaal.engine;

import com.uppaal.engine.protocol.viewmodel.ErrorMessage;

public class CannotEvaluateException extends RuntimeException {
   public int startLine;
   public int endLine;
   public String errorContext;
   public String errorPath;
   public int startColumn;
   public int endColumn;

   public CannotEvaluateException(int startLine, int endLine, String errorContext, String errorMessage, String errorPath, int startColumn, int endColumn) {
      super(errorMessage);
      this.startLine = startLine;
      this.endLine = endLine;
      this.errorContext = errorContext;
      this.errorPath = errorPath;
      this.startColumn = startColumn;
      this.endColumn = endColumn;
   }

   public CannotEvaluateException(ErrorMessage error) {
      this(error.begln, error.endln, error.ctx, error.msg, error.path, error.begcol, error.endcol);
   }

   public String toString() {
      return this.getMessage();
   }
}
