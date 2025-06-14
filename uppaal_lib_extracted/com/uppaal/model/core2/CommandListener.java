package com.uppaal.model.core2;

public interface CommandListener {
   void executed(CommandManager var1, Command var2);

   void undone(CommandManager var1, Command var2);

   void redone(CommandManager var1, Command var2);
}
