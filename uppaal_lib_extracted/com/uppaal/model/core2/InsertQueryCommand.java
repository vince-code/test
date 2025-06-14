package com.uppaal.model.core2;

public class InsertQueryCommand extends AbstractCommand {
   protected final QueryList queryList;
   protected final int index;
   private Query newQuery;

   public InsertQueryCommand(QueryList queryList, int index) {
      this.queryList = queryList;
      this.index = index;
   }

   public void execute() {
      if (this.newQuery == null) {
         this.newQuery = new Query();
      }

      this.queryList.insert(this.newQuery, this.index);
   }

   public void undo() {
      this.queryList.remove(this.index);
   }

   public Element getModifiedElement() {
      return null;
   }

   public Query getAddedQuery() {
      return this.newQuery;
   }

   public int getIndex() {
      return this.index;
   }
}
