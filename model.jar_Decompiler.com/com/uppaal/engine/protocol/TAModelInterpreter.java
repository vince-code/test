package com.uppaal.engine.protocol;

import com.uppaal.engine.ConcreteTraceElement;
import com.uppaal.engine.protocol.viewmodel.ConcreteStateViewModel;
import com.uppaal.engine.protocol.viewmodel.ConcreteSuccessorViewModel;
import com.uppaal.engine.protocol.viewmodel.ConcreteTraceNode;
import com.uppaal.engine.protocol.viewmodel.ConcreteTraceViewModel;
import com.uppaal.engine.protocol.viewmodel.ConcreteTransitionViewModel;
import com.uppaal.engine.protocol.viewmodel.DBMConstraintViewModel;
import com.uppaal.engine.protocol.viewmodel.EdgeFieldViewModel;
import com.uppaal.engine.protocol.viewmodel.GanttBarViewModel;
import com.uppaal.engine.protocol.viewmodel.RandomTransitionViewModel;
import com.uppaal.engine.protocol.viewmodel.SymbolicStateViewModel;
import com.uppaal.engine.protocol.viewmodel.SymbolicTraceNode;
import com.uppaal.model.system.GanttAddition;
import com.uppaal.model.system.Polyhedron;
import com.uppaal.model.system.SystemEdgeSelect;
import com.uppaal.model.system.SystemLocation;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.concrete.ConcreteState;
import com.uppaal.model.system.concrete.ConcreteSuccessor;
import com.uppaal.model.system.concrete.ConcreteTrace;
import com.uppaal.model.system.concrete.ConcreteTransitionDelays;
import com.uppaal.model.system.concrete.RandomTransition;
import com.uppaal.model.system.symbolic.SymbolicState;
import com.uppaal.model.system.symbolic.SymbolicTrace;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TAModelInterpreter {
   private final UppaalSystem system;

   public TAModelInterpreter(UppaalSystem system) {
      this.system = system;
   }

   public SymbolicState interpretSymbolicState(SymbolicStateViewModel protocolState) {
      if (protocolState == null) {
         return null;
      } else {
         SystemLocation[] locations = this.translateLocations(protocolState.getLocations());
         Polyhedron zone = this.translateZone(protocolState.getDbm());
         int[] varVals = new int[protocolState.getVariableValues().size()];

         for(int i = 0; i < protocolState.getVariableValues().size(); ++i) {
            varVals[i] = (Integer)protocolState.getVariableValues().get(i);
         }

         return new SymbolicState(locations, varVals, zone);
      }
   }

   public SystemEdgeSelect[] interpretEdgeSelectList(List<EdgeFieldViewModel> transition) {
      if (transition == null) {
         return new SystemEdgeSelect[0];
      } else {
         return transition.size() == 0 ? null : (SystemEdgeSelect[])transition.stream().flatMap(this::interpretEdgeSelect).toArray((x$0) -> {
            return new SystemEdgeSelect[x$0];
         });
      }
   }

   public Stream<SystemEdgeSelect> interpretEdgeSelect(EdgeFieldViewModel edge) {
      return edge.getEdgeParts().stream().map((edgePart) -> {
         return this.system.createEdgeCon(edgePart.getProcessId(), edgePart.getEdgeId(), edgePart.getSelectValues());
      });
   }

   public ConcreteTransitionDelays[] interpretConcreteTransitions(List<ConcreteTransitionViewModel> transitions) {
      List<ConcreteTransitionDelays> transitionList = new ArrayList();
      Iterator var3 = transitions.iterator();

      while(var3.hasNext()) {
         ConcreteTransitionViewModel transition = (ConcreteTransitionViewModel)var3.next();
         SystemEdgeSelect[] edge = this.interpretEdgeSelectList(transition.edges);
         transitionList.add(new ConcreteTransitionDelays(edge, 0, 0, transition.ranges));
      }

      return (ConcreteTransitionDelays[])transitionList.toArray(new ConcreteTransitionDelays[0]);
   }

   public RandomTransition interpretRandomTransition(RandomTransitionViewModel model) {
      SystemEdgeSelect[] edges = this.interpretEdgeSelectList(model.edges);
      double delay = model.delay;
      return new RandomTransition(edges, delay);
   }

   public List<GanttAddition> interpretGantAdditions(List<GanttBarViewModel> model) {
      return (List)model.stream().map(GanttAddition::new).collect(Collectors.toList());
   }

   public ConcreteSuccessor interpretConcreteSuccessor(ConcreteSuccessorViewModel model) throws CloneNotSupportedException {
      ConcreteState state = this.interpretConcreteState(model.state);
      ConcreteTransitionDelays[] transitions = this.interpretConcreteTransitions(model.transitions);
      List<GanttAddition> ganttAdditions = this.interpretGantAdditions(model.chart);
      return new ConcreteSuccessor(state, transitions, ganttAdditions, model.maxDelay);
   }

   public ConcreteState interpretConcreteState(ConcreteStateViewModel state) {
      SystemLocation[] locations = this.translateLocations(state.locationIndices);
      return new ConcreteState(state.maxDelay, locations, state.variableValues.stream().mapToInt((i) -> {
         return i;
      }).toArray(), state.floatingVariableValues.stream().mapToDouble((i) -> {
         return i;
      }).toArray(), state.clocks.stream().mapToDouble((i) -> {
         return i;
      }).toArray());
   }

   public Polyhedron translateZone(List<DBMConstraintViewModel> dbm) {
      Polyhedron zone = new Polyhedron(this.system);
      Iterator var3 = dbm.iterator();

      while(var3.hasNext()) {
         DBMConstraintViewModel constraint = (DBMConstraintViewModel)var3.next();
         zone.add(constraint.getI(), constraint.getJ(), constraint.getConstraint() ^ 1);
      }

      zone.trim();
      return zone;
   }

   public SystemLocation[] translateLocations(List<Integer> locations) {
      SystemLocation[] locs = new SystemLocation[locations.size()];

      for(int i = 0; i < locations.size(); ++i) {
         locs[i] = this.system.getLocation(i, (Integer)locations.get(i));
      }

      return locs;
   }

   public SymbolicTrace interpretSymbolicTrace(List<SymbolicTraceNode> parsedTrace) {
      SymbolicState source = this.interpretSymbolicState(((SymbolicTraceNode)parsedTrace.get(0)).getDest());
      SymbolicTrace trace = new SymbolicTrace(source);

      for(int i = 0; i < parsedTrace.size() - 1; ++i) {
         SymbolicState dest = this.interpretSymbolicState(((SymbolicTraceNode)parsedTrace.get(i + 1)).getDest());
         SystemEdgeSelect[] edges = this.interpretEdgeSelectList(((SymbolicTraceNode)parsedTrace.get(i + 1)).getTransition());
         trace.add(new SymbolicTransition(source, edges, dest));
         source = dest;
      }

      return trace;
   }

   public ConcreteTrace interpretConcreteTrace(ConcreteTraceViewModel viewModel) {
      ConcreteState init = this.interpretConcreteState(viewModel.init);
      ConcreteState lastState = init;
      List<ConcreteTraceElement> transitions = new ArrayList();
      transitions.add(new ConcreteTraceElement(0.0D, new SystemEdgeSelect[0], (ConcreteState)null, init));

      ConcreteState state;
      for(Iterator var5 = viewModel.transitions.iterator(); var5.hasNext(); lastState = state) {
         ConcreteTraceNode node = (ConcreteTraceNode)var5.next();
         SystemEdgeSelect[] edges = this.interpretEdgeSelectList(node.getEdges());
         state = this.interpretConcreteState(node.getState());
         transitions.add(new ConcreteTraceElement(node.getDelay(), edges, lastState, state));
      }

      return new ConcreteTrace(transitions);
   }
}
