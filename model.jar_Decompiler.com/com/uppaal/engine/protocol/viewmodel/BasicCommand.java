package com.uppaal.engine.protocol.viewmodel;

public class BasicCommand {
   public String cmd;
   public String args;

   public BasicCommand(String cmd, String args) {
      this.cmd = cmd;
      this.args = args;
   }
}
