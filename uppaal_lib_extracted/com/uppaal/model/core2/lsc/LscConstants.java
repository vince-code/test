package com.uppaal.model.core2.lsc;

import com.uppaal.model.core2.CommonConstants;
import java.awt.Color;

public interface LscConstants extends CommonConstants {
   int INSTANCE_HEIGHT = 20;
   int INSTANCE_FOOT_HEIGHT = 4;
   int INSTANCE_FOOT_WIDTH = 50;
   int INSTANCE_MIN_WIDTH = 60;
   int INSTANCE_LENGTH = 210;
   double ARROW_HEIGHT = 3.0D;
   double ARROW_BASE = 6.0D;
   int PRECHART_MIN_HEIGHT = 30;
   int PRECHART_TOP_DISTANCE = 20;
   int PRECHART_SIDE_DISTANCE = 20;
   int LOC_DISTANCE = 20;
   Color INSTANCE_FILL_COL = new Color(220, 220, 220);
   Color INSTANCE_LINE_COL = Color.black;
   Color MESSAGE_COL = Color.black;
   Color MESSAGE_LABEL_COL = new Color(66, 66, 168);
   Color PRECHART_COL = Color.black;
   Color CONDITION_COLD_COL = new Color(66, 66, 168);
   Color CONDITION_HOT_COL = Color.red;
   Color UPDATE_COL = new Color(66, 168, 72);
   int CONDITION_UPDATE_HEIGHT = 20;
   int ANCHOR_RADIUS = 4;
   int ANCHOR_PRECHART_RADIUS = 6;
   int TOUCH_DISTANCE = 3;
   int TOUCH_INSTANCE_DISTANCE = 4;
   int FOOT_TOUCH_DISTANCE = 10;
   int TOP_DISTANCE = 10;
   int CONDITION_SIDES = 20;
   int CUT_SIDE_DISTANCE = 40;
   int CUT_Y_DISTANCE = 40;
}
