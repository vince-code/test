package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class ConcreteSuccessorViewModel {
   public ConcreteStateViewModel state;
   public List<ConcreteTransitionViewModel> transitions;
   @SerializedName("gantt")
   public ArrayList<GanttBarViewModel> chart;
   public double maxDelay;
}
