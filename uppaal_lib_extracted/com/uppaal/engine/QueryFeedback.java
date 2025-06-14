package com.uppaal.engine;

import com.uppaal.engine.protocol.viewmodel.ProgressInfoViewModel;
import com.uppaal.model.core2.QueryResult;
import com.uppaal.model.system.concrete.ConcreteTrace;
import com.uppaal.model.system.symbolic.SymbolicTrace;

public interface QueryFeedback {
   void setProgressAvail(boolean var1);

   void setProgress(int var1, long var2, long var4, long var6, long var8, long var10, long var12, long var14, long var16, long var18);

   default void setProgress(ProgressInfoViewModel info) {
      this.setProgress(info.getLoad(), info.getVm(), info.getRss(), info.getCache(), info.getAvail(), info.getSwap(), info.getSwapfree(), info.getUser(), info.getSys(), info.getTimestamp());
   }

   void setSystemInfo(long var1, long var3, long var5);

   void setLength(int var1);

   void setCurrent(int var1);

   void setTrace(char var1, String var2, SymbolicTrace var3, QueryResult var4);

   void setTrace(char var1, String var2, ConcreteTrace var3, QueryResult var4);

   void setFeedback(String var1);

   void appendText(String var1);

   void setResultText(String var1);
}
