package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.Element;
import java.util.ArrayList;

public abstract class AnchoredElement extends LscElement {
   public AnchoredElement(Element prototype) {
      super(prototype);
   }

   public abstract void setAnchor(InstanceLine var1);

   public abstract InstanceLine getAnchor();

   protected abstract void fireAnchorChanged(InstanceLine var1);

   public abstract ArrayList<InstanceLine> getAnchors();

   public abstract Element getLabel();
}
