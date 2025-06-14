package com.uppaal.model.core2;

public class QueryValue {
   private QueryValue.Status status;
   private QueryValue.Kind kind;
   private String value;

   public QueryValue(String status, String kind, String value) {
      this.setStatus(status);
      this.setKind(kind);
      this.value = value;
   }

   public QueryValue(QueryValue.Status status, QueryValue.Kind kind, String value) {
      this.status = status;
      this.kind = kind;
      this.value = value;
   }

   public QueryValue() {
   }

   public void setStatus(String result) {
      byte var3 = -1;
      switch(result.hashCode()) {
      case -1867169789:
         if (result.equals("success")) {
            var3 = 0;
         }
         break;
      case -1086574198:
         if (result.equals("failure")) {
            var3 = 3;
         }
         break;
      case -14013570:
         if (result.equals("maybe-false")) {
            var3 = 2;
         }
         break;
      case 96784904:
         if (result.equals("error")) {
            var3 = 4;
         }
         break;
      case 969812947:
         if (result.equals("maybe-true")) {
            var3 = 1;
         }
      }

      switch(var3) {
      case 0:
         this.status = QueryValue.Status.Success;
         break;
      case 1:
         this.status = QueryValue.Status.Maybe_True;
         break;
      case 2:
         this.status = QueryValue.Status.Maybe_False;
         break;
      case 3:
         this.status = QueryValue.Status.Failure;
         break;
      case 4:
      default:
         this.status = QueryValue.Status.Unchecked;
      }

   }

   public void setStatus(char result) {
      switch(result) {
      case 'E':
      default:
         this.status = QueryValue.Status.Unchecked;
         break;
      case 'F':
         this.status = QueryValue.Status.Failure;
         break;
      case 'M':
         this.status = QueryValue.Status.Maybe_True;
         break;
      case 'N':
         this.status = QueryValue.Status.Maybe_False;
         break;
      case 'T':
         this.status = QueryValue.Status.Success;
      }

   }

   public void setStatus(QueryValue.Status status) {
      this.status = status;
   }

   public QueryValue.Status getStatus() {
      return this.status;
   }

   public char getStatusLabel() {
      switch(this.status) {
      case Success:
         return 'T';
      case Maybe_True:
         return 'M';
      case Maybe_False:
         return 'N';
      case Failure:
         return 'F';
      case Unchecked:
         return 'E';
      default:
         throw new IllegalStateException("Unexpected value: " + this.status);
      }
   }

   public QueryValue.Kind getKind() {
      return this.kind;
   }

   public String getValue() {
      return this.value;
   }

   public void setKind(QueryValue.Kind kind) {
      this.kind = kind;
   }

   public void setKind(String kind) {
      if (kind.equals("quality")) {
         this.setKind(QueryValue.Kind.Quality);
      } else if (kind.equals("quantity")) {
         this.setKind(QueryValue.Kind.Quantity);
      } else if (kind.matches("(confidence[ _-]?)?interval")) {
         this.setKind(QueryValue.Kind.Interval);
      } else {
         this.setKind(QueryValue.Kind.Unknown);
      }

   }

   public void setValue(String value) {
      this.value = value;
   }

   public static enum Kind {
      Quality,
      Quantity,
      Interval,
      Unknown;
   }

   public static enum Status {
      Success,
      Maybe_True,
      Maybe_False,
      Failure,
      Unchecked;
   }
}
