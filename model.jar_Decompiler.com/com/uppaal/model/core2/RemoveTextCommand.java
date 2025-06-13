package com.uppaal.model.core2;

public class RemoveTextCommand extends MarkSelectionCommand {
   protected Object oldvalue = null;
   protected int oldlength = 0;
   protected String newvalue = null;

   public RemoveTextCommand(Property property, int position, int length, boolean allowCollapse) {
      super(property, position, length, allowCollapse);
   }

   public void execute() {
      this.oldvalue = this.property.getValue();
      String value = (String)this.oldvalue;

      assert value != null;

      this.oldlength = value.length();
      if (this.newvalue == null) {
         assert this.position + this.length <= this.oldlength;

         String var10001 = value.substring(0, this.position);
         this.newvalue = var10001 + value.substring(this.position + this.length);
      }

      this.property.setValue(this.newvalue);
      super.execute();
   }

   public boolean merge(Command next) {
      if (this.commands != null) {
         return super.merge(next);
      } else if (next instanceof RemoveTextCommand && this.getModifiedElement() == next.getModifiedElement()) {
         RemoveTextCommand cmd = (RemoveTextCommand)next;

         assert cmd.position + cmd.length <= this.oldlength - this.length;

         if (cmd.position + cmd.length == this.position) {
            this.length += cmd.length;
            this.position = cmd.position;
            this.newvalue = null;
            return true;
         } else if (cmd.position == this.position) {
            this.length += cmd.length;
            this.newvalue = null;
            return true;
         } else {
            return super.merge(next);
         }
      } else {
         return super.merge(next);
      }
   }

   public void undo() {
      super.undo();
      this.property.setValue(this.oldvalue);
   }

   public Element getModifiedElement() {
      return this.property;
   }
}
