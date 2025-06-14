package com.uppaal.model.core2;

public class MarkSelectionCommand extends CollapsableCommand {
   protected int position;
   protected int length;

   public MarkSelectionCommand(Property property, int position, int length, boolean allowCollapse) {
      super(allowCollapse, property);

      assert position >= 0;

      assert length >= 0;

      this.position = position;
      this.length = length;
   }

   public int getBeginIndex() {
      return this.position;
   }

   public int getEndIndex() {
      return this.position + this.length;
   }
}
