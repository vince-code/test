package com.uppaal.model.core2;

import java.awt.Color;

public class ModelColors {
   public Color locationFillColor;
   public Color locationLabelColor;
   public Color invariantColor;
   public Color exponentialRateColor;
   public Color commentsColor;
   public Color edgeColor;
   public Color updateColor;
   public Color syncColor;
   public Color guardColor;
   public Color selectColor;
   public Color probabilityWeightColor;
   public static ModelColors current;
   private static ModelColors light;
   private static ModelColors dark;

   public static ModelColors getCurrentColors() {
      if (current == null) {
         current = getLightColors();
      }

      return current;
   }

   public static ModelColors getLightColors() {
      if (light == null) {
         light = new ModelColors();
         light.locationFillColor = new Color(165, 175, 205);
         light.edgeColor = Color.BLACK;
         light.locationLabelColor = new Color(140, 56, 99);
         light.invariantColor = new Color(167, 66, 168);
         light.exponentialRateColor = new Color(173, 39, 80);
         light.commentsColor = new Color(150, 150, 150);
         light.updateColor = new Color(66, 66, 168);
         light.syncColor = new Color(66, 160, 168);
         light.guardColor = new Color(66, 168, 72);
         light.selectColor = new Color(163, 168, 66);
         light.probabilityWeightColor = new Color(168, 122, 66);
      }

      return light;
   }

   public static ModelColors getDarkColors() {
      if (dark == null) {
         dark = new ModelColors();
         dark.locationFillColor = new Color(111, 122, 154);
         dark.edgeColor = new Color(15263976);
         dark.locationLabelColor = new Color(255, 112, 186);
         dark.invariantColor = new Color(253, 83, 255);
         dark.exponentialRateColor = new Color(189, 104, 125);
         dark.commentsColor = new Color(150, 150, 150);
         dark.updateColor = new Color(8421631);
         dark.syncColor = new Color(6615295);
         dark.guardColor = new Color(113, 232, 120);
         dark.selectColor = new Color(221, 227, 105);
         dark.probabilityWeightColor = new Color(232, 169, 93);
      }

      return dark;
   }
}
