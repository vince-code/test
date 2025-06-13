package polimi.logic.verifier;

import com.uppaal.engine.QueryFeedback;
import com.uppaal.model.core2.QueryResult;
import com.uppaal.model.system.concrete.ConcreteTrace;
import com.uppaal.model.system.symbolic.SymbolicTrace;

public class DefaultQueryFeedback implements QueryFeedback {
     private SymbolicTrace strace = null;
     private ConcreteTrace ctrace = null;

    public SymbolicTrace getSymbolicTrace() {
        return strace;
    }

    public void setProgressAvail(boolean var1) {
    }

    public void setProgress(int var1, long var2, long var4, long var6, long var8, long var10, long var12, long var14, long var16, long var18) {
    }

    public void setSystemInfo(long var1, long var3, long var5) {
    }

    public void setLength(int var1) {
    }

    public void setCurrent(int var1) {
    }

    public void setTrace(char var1, String var2, SymbolicTrace symbolicTrace, QueryResult var4) {
        strace = symbolicTrace;
    }

    public void setTrace(char var1, String var2, ConcreteTrace concreteTrace, QueryResult var4) {
        ctrace = concreteTrace;
    }

    public void setFeedback(String var1) {
        if (var1 != null && var1.length() > 0) {
            System.out.println("Feedback: " + var1);
        }
    }

    public void appendText(String var1) {
        if (var1 != null && var1.length() > 0) {
            System.out.println("Append: " + var1);
        }

    }

    public void setResultText(String var1) {
        if (var1 != null && var1.length() > 0) {
            System.out.println("Result: " + var1);
        }

    }
}
