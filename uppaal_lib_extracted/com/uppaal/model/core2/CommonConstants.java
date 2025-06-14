package com.uppaal.model.core2;

import java.awt.Color;
import javax.swing.UIManager;

public interface CommonConstants {
   int ARROW_LENGTH = 10;
   double ARROW_ANGLE = 0.2617993877991494D;
   Color PROCESS_OUTLINE_COL = new Color(100, 100, 100);
   Color PROCESS_FOREGROUND_COL = UIManager.getColor("EditorPane.foreground");
   Color AREA_SELECT_COL = new Color(0, 128, 0);
   Color MOUSE_OVER_COL = new Color(200, 200, 50);
   Color MOUSE_OVER_REL_COL = new Color(196, 0, 0);
   Color SELECTED_COL = Color.orange;
   Color EMPH_COL = new Color(192, 0, 0);
   Color PART_COV_COL = new Color(0, 160, 255);
   Color TRACE_COV_COL = Color.blue;
   Color COV_DARK_COL = new Color(0, 25, 66);
   Color COV_LIGHT_COL = new Color(139, 237, 255);
   LinearGradient COV_GRADIENT = new LinearGradient(COV_DARK_COL, COV_LIGHT_COL);
   Color GLOBAL_DECL_COL = new Color(49, 101, 140);
   Color GLOBAL_PROCESSASSIGN_COL = new Color(49, 101, 140);
   Color GLOBAL_SYSTEM_COL = new Color(49, 101, 140);
   Color TEMPLATE_NAME_COL = new Color(49, 101, 140);
   Color TEMPLATE_PARAMLIST_COL = new Color(49, 101, 140);
   float FONT_SIZE = 14.0F;
   int GRIDMIN = 2 * Math.round(16.996F);
   int GRIDCHANGE = GRIDMIN;
   int SNAP_PER_GRID = 4;
   int LEVEL = GRIDMIN / 2;
   Color GRIDCOLOR = Color.gray;
}
