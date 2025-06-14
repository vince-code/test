package com.uppaal.model.core2;

public class InsertTextCommand extends MarkSelectionCommand {
   protected String text;
   protected Object oldvalue;
   protected int oldlength;
   protected String newvalue;

   public InsertTextCommand(Property property, String text, int position, boolean allowCollapse) {
      super(property, position, text.length(), allowCollapse);
      this.text = text;
      this.oldvalue = null;
      this.oldlength = 0;
      this.newvalue = null;
   }

   public void execute() {
      this.oldvalue = this.property.getValue();
      String value = (String)this.oldvalue;
      if (value == null) {
         this.oldlength = 0;
         this.property.setValue(this.text);
      } else {
         if (this.newvalue == null) {
            this.oldlength = value.length();

            assert this.position <= this.oldlength;

            String var10001 = value.substring(0, this.position);
            this.newvalue = var10001 + this.text + value.substring(this.position);
         }

         this.property.setValue(this.newvalue);
      }

      super.execute();
   }

   public boolean merge(Command next) {
      if (this.commands != null) {
         return super.merge(next);
      } else if (next instanceof InsertTextCommand && this.getModifiedElement() == next.getModifiedElement()) {
         InsertTextCommand cmd = (InsertTextCommand)next;

         assert cmd.position <= this.oldlength + this.text.length();

         if (cmd.position == this.position) {
            this.text = cmd.text + this.text;
            this.length = this.text.length();
            this.newvalue = null;
            return true;
         } else if (cmd.position == this.position + this.text.length()) {
            this.text = this.text + cmd.text;
            this.length = this.text.length();
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
}
