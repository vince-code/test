package com.uppaal.model.io2;

import com.uppaal.engine.protocol.viewmodel.ErrorMessage;

public class Problem {
   private final String type;
   private final String xpath;
   private final int fline;
   private final int fcolumn;
   private final int lline;
   private final int lcolumn;
   private final String msg;
   private final String context;
   private static final String ERROR = "While reading problem report";

   public String getType() {
      return this.type;
   }

   public String getMessage() {
      return this.msg;
   }

   public String getXPath() {
      return this.xpath;
   }

   public int getFirstLine() {
      return this.fline;
   }

   public int getFirstColumn() {
      return this.fcolumn;
   }

   public int getLastLine() {
      return this.lline;
   }

   public int getLastColumn() {
      return this.lcolumn;
   }

   public String getContext() {
      return this.context;
   }

   public Problem(String type, String path, int fline, int fcolumn, int lline, int lcolumn, String msg, String context) {
      this.type = type;
      this.xpath = path;
      this.fline = fline;
      this.fcolumn = fcolumn;
      this.lline = lline;
      this.lcolumn = lcolumn;
      this.msg = msg;
      this.context = context;
   }

   public Problem(String type, ErrorMessage error) {
      this(type, error.path, error.begln, error.begcol, error.endln, error.endcol, error.msg, error.ctx);
   }

   public Problem(String type, String path, int fline, int fcolumn, int lline, int lcolumn, String msg) {
      this(type, path, fline, fcolumn, lline, lcolumn, msg, (String)null);
   }

   public Problem(String type, String path, String msg) {
      this(type, path, 0, 0, 0, 0, msg, (String)null);
   }

   public String toString() {
      return this.type + "\n" + this.xpath + "\n" + this.fline + " " + this.fcolumn + " " + this.lline + " " + this.lcolumn + this.msg + this.context + "\n";
   }
}
