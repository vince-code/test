package polimi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class VerificationResult {
    private boolean isSatisfied;
    private String fault;
    private List<Integer> misconfiguredCBs;
    private String trace;

    public int[] getMisconfiguredCBsArray() {
        if (misconfiguredCBs == null) {
            return new int[0];
        }
        int[] array = new int[misconfiguredCBs.size()];
        for (int i = 0; i < misconfiguredCBs.size(); i++) {
            array[i] = misconfiguredCBs.get(i);
        }
        return array;
    }

    public String toString(){
        final String GREEN = "\u001B[32m";
        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";

        final String OK = "Property is satisfied.";
        final String KO = "Property is not_satisfied";

        String color = isSatisfied ? GREEN : RED;
        String result = isSatisfied ? OK : KO;
        StringBuilder resultBuilder = new StringBuilder("\tResult: " + color + result + RESET);
        if (isSatisfied){
            resultBuilder.append("\n").append("\t\tFault: ").append(fault);
            resultBuilder.append("\n").append("\t\tMisconfigured CBs: ").append(misconfiguredCBs);
            resultBuilder.append("\n").append("\t\tTrace:\n").append(trace);
        }
        return(resultBuilder.toString());
    }
}
