package com.uppaal.model.core2;

import com.uppaal.model.core2.lsc.AnchoredElement;
import com.uppaal.model.core2.lsc.InstanceLine;
import com.uppaal.model.core2.lsc.Message;

public interface EventListener extends java.util.EventListener {
   void propertyChanged(Property var1, String var2, Object var3, Object var4);

   void afterInsertion(Node var1, Node var2);

   void beforeRemoval(Node var1, Node var2);

   void afterRemoval(Node var1, Node var2);

   void afterMove(Node var1, Node var2);

   void edgeSourceChanged(Edge var1, AbstractLocation var2, AbstractLocation var3);

   void edgeTargetChanged(Edge var1, AbstractLocation var2, AbstractLocation var3);

   void messageSourceChanged(Message var1, InstanceLine var2, InstanceLine var3);

   void messageTargetChanged(Message var1, InstanceLine var2, InstanceLine var3);

   void anchorChanged(AnchoredElement var1, InstanceLine var2, InstanceLine var3);
}
