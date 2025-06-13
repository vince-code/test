package com.uppaal.model.system;

import com.uppaal.model.system.concrete.ConcreteState;
import com.uppaal.model.system.symbolic.SymbolicState;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import java.math.BigDecimal;
import java.util.ArrayList;

public class Trace {
   private Trace.DynamicList<ConcreteState> concreteStates;
   private Trace.DynamicList<SymbolicState> symbolicStates;
   private Trace.DynamicList<SystemEdgeSelect[]> transitionsEdges;
   private Trace.DynamicList<BigDecimal> entryTimes;

   public Trace(SymbolicState initialSymbolicState) {
      this.reset((ConcreteState)null, initialSymbolicState);
   }

   public Trace(ConcreteState initialConcreteState) {
      this.reset(initialConcreteState, (SymbolicState)null);
   }

   public Trace(ConcreteState initialConcreteState, SymbolicState initialSymbolicState) {
      this.reset(initialConcreteState, initialSymbolicState);
   }

   public void reset(ConcreteState initialConcreteState, SymbolicState initialSymbolicState) {
      if (initialConcreteState != null) {
         this.concreteStates = new Trace.DynamicList();
         this.concreteStates.add(initialConcreteState);
         this.entryTimes = new Trace.DynamicList();
         this.entryTimes.add(BigDecimal.ZERO);
      } else {
         this.concreteStates = null;
         this.entryTimes = null;
      }

      if (initialSymbolicState != null) {
         this.symbolicStates = new Trace.DynamicList();
         this.symbolicStates.add(initialSymbolicState);
      } else {
         this.symbolicStates = null;
      }

      this.transitionsEdges = new Trace.DynamicList();
   }

   public void clearAfterState(int i) {
      if (this.concreteStates != null) {
         this.concreteStates.removeRange(i + 1, this.concreteStates.size());
         this.entryTimes.removeRange(i + 1, this.entryTimes.size());
      }

      if (this.symbolicStates != null) {
         this.symbolicStates.removeRange(i + 1, this.symbolicStates.size());
      }

      this.transitionsEdges.removeRange(i, this.transitionsEdges.size());
   }

   public void append(BigDecimal delay, SystemEdgeSelect[] transitionEdges, ConcreteState concreteState, SymbolicState symbolicState) {
      if (this.concreteStates != null) {
         int size = this.entryTimes.size();
         if (size == 0) {
            this.entryTimes.add(delay);
         } else {
            this.entryTimes.add(((BigDecimal)this.entryTimes.get(size - 1)).add(delay));
         }

         this.concreteStates.add(concreteState);
      }

      if (this.symbolicStates != null) {
         this.symbolicStates.add(symbolicState);
      }

      this.transitionsEdges.add(transitionEdges);
   }

   public void append(BigDecimal delay, SystemEdgeSelect[] transitionEdges, ConcreteState concreteState) {
      this.append(delay, transitionEdges, concreteState, (SymbolicState)null);
   }

   public void append(SystemEdgeSelect[] transitionEdges, SymbolicState symbolicState) {
      this.append((BigDecimal)null, transitionEdges, (ConcreteState)null, symbolicState);
   }

   public BigDecimal getEntryTime(int i) {
      if (this.entryTimes == null) {
         throw new IllegalStateException();
      } else {
         return (BigDecimal)this.entryTimes.get(i);
      }
   }

   public BigDecimal getDelay(int i) {
      if (this.entryTimes == null) {
         throw new IllegalStateException();
      } else {
         return i + 1 < this.entryTimes.size() ? ((BigDecimal)this.entryTimes.get(i + 1)).subtract((BigDecimal)this.entryTimes.get(i)) : null;
      }
   }

   public SystemEdgeSelect[] getTransitionEdges(int i) {
      return (SystemEdgeSelect[])this.transitionsEdges.get(i);
   }

   public SymbolicTransition getSymbolicTransition(int i) {
      if (this.symbolicStates == null) {
         throw new IllegalStateException();
      } else {
         return i + 1 < this.symbolicStates.size() ? new SymbolicTransition(this.getSymbolicState(i), this.getTransitionEdges(i), this.getSymbolicState(i + 1)) : null;
      }
   }

   public ConcreteState getConcreteState(int i) {
      if (this.concreteStates == null) {
         throw new IllegalStateException();
      } else {
         return (ConcreteState)this.concreteStates.get(i);
      }
   }

   public SymbolicState getSymbolicState(int i) {
      if (this.symbolicStates == null) {
         throw new IllegalStateException();
      } else {
         return (SymbolicState)this.symbolicStates.get(i);
      }
   }

   public int size() {
      return this.transitionsEdges.size() + 1;
   }

   private class DynamicList<E> extends ArrayList<E> {
      public void removeRange(int from, int to) {
         super.removeRange(from, to);
         this.trimToSize();
      }
   }
}
