package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Node;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LscElement extends Node implements LscConstants {
   public LscElement(Element prototype) {
      super(prototype);
   }

   public String getFriendlyName() {
      assert false;

      return null;
   }

   public String getTemperature() {
      return (Boolean)this.getPropertyValue("hot") ? "hot" : "cold";
   }

   public boolean isHot() {
      return (Boolean)this.getPropertyValue("hot");
   }

   public static int getWidth(String string, Font font, Graphics2D g) {
      if (g == null) {
         BufferedImage bufferedImage = new BufferedImage(2, 2, 7);
         g = bufferedImage.createGraphics();
      }

      g.setFont(font);
      return (int)Math.ceil(g.getFontMetrics().getStringBounds(string, g).getWidth());
   }

   public static Color lighter(Color color, float white) {
      int r = color.getRed();
      int g = color.getGreen();
      int b = color.getBlue();
      return new Color((int)((float)r * (1.0F - white) + 255.0F * white), (int)((float)g * (1.0F - white) + 255.0F * white), (int)((float)b * (1.0F - white) + 255.0F * white));
   }

   public int getWidth(Graphics2D g) {
      String s = this.getLabelValue();
      Font font = (Font)this.getPropertyValue("font");
      return getWidth(s, font, g);
   }

   public String getLabelValue() {
      return "";
   }

   public Color getColor(Element element) {
      if (this.getPropertyValue("color") != null) {
         return (Color)this.getPropertyValue("color");
      } else {
         return ((LscElement)element).isHot() ? CONDITION_HOT_COL : CONDITION_COLD_COL;
      }
   }

   public ArrayList<InstanceLine> getAnchors() {
      return null;
   }

   public Element getLabel() {
      Element label = this.getProperty("condition");
      return label != null ? label : this.getProperty("update");
   }
}
