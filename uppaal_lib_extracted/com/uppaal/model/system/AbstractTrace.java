package com.uppaal.model.system;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AbstractTrace<Transition extends AbstractTransition> implements Iterable<Transition> {
   private TraceListener<Transition> tlistener = null;
   protected final ArrayList<Transition> transitions = new ArrayList();
   protected boolean isReadonlyTrace;

   public AbstractTrace() {
   }

   public AbstractTrace(List<Transition> transitionList) {
      assert this.transitions != null;

      Iterator var2 = transitionList.iterator();

      AbstractTransition t;
      do {
         if (!var2.hasNext()) {
            this.transitions.addAll(transitionList);
            return;
         }

         t = (AbstractTransition)var2.next();
      } while($assertionsDisabled || t != null);

      throw new AssertionError();
   }

   public void add(Transition st) {
      assert !this.isReadonlyTrace;

      assert st != null;

      this.transitions.add(st);
      if (this.tlistener != null) {
         this.tlistener.append(st);
      }

   }

   public Transition remove(int i) {
      assert !this.isReadonlyTrace;

      if (this.tlistener != null) {
         this.tlistener.remove((AbstractTransition)this.transitions.get(i));
      }

      return (AbstractTransition)this.transitions.remove(i);
   }

   public Transition get(int i) {
      return (AbstractTransition)this.transitions.get(i);
   }

   public boolean isEmpty() {
      return this.transitions.isEmpty();
   }

   public int size() {
      return this.transitions.size();
   }

   public void setTraceListener(TraceListener<Transition> tl) {
      this.tlistener = tl;
   }

   public Iterator<Transition> iterator() {
      return this.transitions.iterator();
   }

   public boolean isReadonlyTrace() {
      return this.isReadonlyTrace;
   }

   public void markReadonly() {
      this.isReadonlyTrace = true;
   }
}
