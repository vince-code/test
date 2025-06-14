package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Node;
import java.awt.Point;
import java.util.ArrayList;

public interface ViewWorkAround {
   int getInstanceLength(int var1);

   void populateInstance(AbstractTemplate var1);

   void populatePrechart(AbstractTemplate var1);

   void populateCondition(AbstractTemplate var1);

   int getPrechartIndex();

   Prechart getPrechart();

   void addInstanceLine(int var1, InstanceLine var2);

   void removeInstanceLine(int var1, InstanceLine var2);

   ArrayList<Condition> getConditionsOf(Node var1);

   Update getUpdateOf(Node var1);

   Update getAnchoredToConditionUpdate(Condition var1, InstanceLine var2);

   void setAnchorToUpdate(InstanceLine var1, Condition var2);

   ArrayList<Point> getMaxSimregions(ArrayList<Simregion> var1);
}
