package com.uppaal.model.core2;

import java.awt.Color;

public class LinearGradient {
   int r;
   int g;
   int b;
   int dr;
   int dg;
   int db;

   public LinearGradient(Color start, Color end) {
      this.r = start.getRed();
      this.g = start.getGreen();
      this.b = start.getBlue();
      this.dr = end.getRed() - this.r;
      this.dg = end.getGreen() - this.g;
      this.db = end.getBlue() - this.b;
   }

   public Color interpolate(float value) {
      float r = (float)this.r + (float)this.dr * value;
      float g = (float)this.g + (float)this.dg * value;
      float b = (float)this.b + (float)this.db * value;
      return new Color((int)r, (int)g, (int)b);
   }
}
