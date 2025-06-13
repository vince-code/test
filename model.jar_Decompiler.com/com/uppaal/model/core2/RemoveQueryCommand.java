package com.uppaal.model.core2;

public class RemoveQueryCommand extends AbstractCommand {
   protected final Query query;
   protected final QueryList queryList;
   protected final int index;

   public RemoveQueryCommand(QueryList queryListModel, int index) {
      this.queryList = queryListModel;
      this.index = index;
      this.query = queryListModel.get(index);
   }

   public RemoveQueryCommand(QueryList queryList, Query query) {
      this.queryList = queryList;
      this.query = query;
      this.index = queryList.indexOf(query);
   }

   public void execute() {
      this.queryList.remove(this.index);
   }

   public void undo() {
      this.queryList.insert(this.query, this.index);
   }

   public Element getModifiedElement() {
      return null;
   }

   public Query getQuery() {
      return this.query;
   }

   public int getIndex() {
      return this.index;
   }
}
