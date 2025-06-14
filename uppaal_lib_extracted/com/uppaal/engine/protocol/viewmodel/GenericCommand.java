package com.uppaal.engine.protocol.viewmodel;

import com.google.gson.annotations.SerializedName;

public class GenericCommand<T> {
   @SerializedName("cmd")
   public String command;
   @SerializedName("args")
   public T args;

   public GenericCommand() {
   }

   public GenericCommand(String command, T args) {
      this.command = command;
      this.args = args;
   }
}
