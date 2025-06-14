package com.uppaal.model.core2;

public interface Command {
   void execute();

   void undo();

   void setVersion(int var1);

   int getVersion();

   boolean merge(Command var1);

   Element getModifiedElement();
}
