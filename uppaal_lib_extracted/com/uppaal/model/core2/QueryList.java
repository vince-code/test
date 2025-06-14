package com.uppaal.model.core2;

import java.util.ArrayList;
import java.util.Iterator;

public class QueryList extends Node implements Iterable<Query> {
   private final ArrayList<QueryListListener> queryListListeners;

   public QueryList(Element prototype) {
      super(prototype);
      this.queryListListeners = new ArrayList();
      this.setCommandManager(new CommandManager());
   }

   public QueryList() {
      this((Element)null);
   }

   public void addQueryListListener(QueryListListener l) {
      this.queryListListeners.add(l);
   }

   public void removeQueryListListener(QueryListListener l) {
      this.queryListListeners.remove(l);
   }

   private void fireQueryAdded(Query query, int index) {
      Iterator var3 = this.queryListListeners.iterator();

      while(var3.hasNext()) {
         QueryListListener l = (QueryListListener)var3.next();
         l.queryAdded(query, index);
      }

   }

   private void fireQueryRemoved(Query query, int index) {
      Iterator var3 = this.queryListListeners.iterator();

      while(var3.hasNext()) {
         QueryListListener l = (QueryListListener)var3.next();
         l.queryRemoved(query, index);
      }

   }

   public boolean isEmpty() {
      return this.getFirst() == null;
   }

   public void add(Query query) {
      this.insert(query, this.getLast());
      this.fireQueryAdded(query, this.size() - 1);
   }

   public void removeAll() {
      Iterator var1 = this.iterator();

      while(var1.hasNext()) {
         Query query = (Query)var1.next();
         this.remove(query);
      }

   }

   public Query get(int index) {
      Query query = (Query)this.getFirst();

      for(int i = 0; i < index; ++i) {
         query = (Query)query.getNext();
         if (query == null) {
            break;
         }
      }

      return query;
   }

   public void insert(Query q, int index) {
      Query old = this.get(index);
      if (old == null) {
         this.insert(q, this.getLast());
      } else {
         this.insert(q, old.getPrevious());
      }

      this.fireQueryAdded(q, index);
   }

   public void remove(Query query) {
      int index = this.indexOf(query);
      query.remove();
      this.fireQueryRemoved(query, index);
   }

   public void remove(int index) {
      Query q = this.get(index);
      q.remove();
      this.fireQueryRemoved(q, index);
   }

   public int indexOf(Query query) {
      int i = 0;

      for(Iterator var3 = this.iterator(); var3.hasNext(); ++i) {
         Query q = (Query)var3.next();
         if (q == query) {
            return i;
         }
      }

      return -1;
   }

   public int size() {
      Node n = this.getFirst();

      int i;
      for(i = 0; n != null; ++i) {
         n = n.getNext();
      }

      return i;
   }

   public Iterator<Query> iterator() {
      return new Iterator<Query>() {
         private Node next = QueryList.this.getFirst();

         public boolean hasNext() {
            return this.next != null;
         }

         public Query next() {
            Node n = this.next;
            this.next = this.next.getNext();
            return (Query)n;
         }

         public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
         }
      };
   }

   public int getVersion() {
      return this.getCommandManager().getVersion();
   }

   public void accept(Visitor visitor) throws Exception {
      visitor.visitQueries(this);
   }

   public String getFriendlyName() {
      return "queries";
   }

   public boolean isBlank() {
      if (this.getFirst() == null) {
         return true;
      } else {
         Iterator var1 = this.iterator();

         Query q;
         do {
            if (!var1.hasNext()) {
               return true;
            }

            q = (Query)var1.next();
         } while(q.getComment().isBlank() && q.getFormula().isBlank());

         return false;
      }
   }
}
