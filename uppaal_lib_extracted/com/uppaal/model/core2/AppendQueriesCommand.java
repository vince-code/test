package com.uppaal.model.core2;

import java.util.Iterator;

public class AppendQueriesCommand extends AbstractCommand {
   protected QueryList queryList;
   protected QueryList newQueryList;

   public AppendQueriesCommand(QueryList queryList, QueryList newQueryList) {
      this.queryList = queryList;
      this.newQueryList = newQueryList;
   }

   public void execute() {
      Iterator var1 = this.newQueryList.iterator();

      while(var1.hasNext()) {
         Query query = (Query)var1.next();
         this.queryList.add(query);
      }

   }

   public void undo() {
      Iterator var1 = this.newQueryList.iterator();

      while(var1.hasNext()) {
         Query query = (Query)var1.next();
         this.queryList.remove(query);
      }

   }

   public Element getModifiedElement() {
      return null;
   }

   public int getIntervalStart() {
      return this.queryList.size();
   }

   public int getIntervalEnd() {
      return this.queryList.size() + this.newQueryList.size() - 1;
   }
}
