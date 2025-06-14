package com.uppaal.model.core2;

import com.uppaal.model.core2.lsc.AnchoredElement;
import com.uppaal.model.core2.lsc.InstanceLine;
import com.uppaal.model.core2.lsc.Message;

public class AbstractEventListener implements EventListener {
   public void propertyChanged(Property source, String name, Object old, Object value) {
   }

   public void afterInsertion(Node parent, Node node) {
   }

   public void afterMove(Node parent, Node child) {
   }

   public void beforeRemoval(Node parent, Node node) {
   }

   public void afterRemoval(Node parent, Node node) {
   }

   public void edgeSourceChanged(Edge source, AbstractLocation oldSource, AbstractLocation newSource) {
   }

   public void edgeTargetChanged(Edge source, AbstractLocation oldTarget, AbstractLocation newTarget) {
   }

   public void messageSourceChanged(Message source, InstanceLine oldSource, InstanceLine newSource) {
   }

   public void messageTargetChanged(Message source, InstanceLine oldTarget, InstanceLine newTarget) {
   }

   public void anchorChanged(AnchoredElement element, InstanceLine oldAnchor, InstanceLine newAnchor) {
   }
}
