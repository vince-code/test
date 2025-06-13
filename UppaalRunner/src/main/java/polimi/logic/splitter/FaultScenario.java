package polimi.logic.splitter;

import lombok.Getter;
import lombok.ToString;
import polimi.model.Network;

import java.util.Set;

@Getter
@ToString
public class FaultScenario {
    private final Set<Integer> lineFaults;
    private final Set<Integer> busFaults;


    public FaultScenario(Set<Integer> busFaults, Set<Integer> lineFaults) {
        this.busFaults = busFaults;
        this.lineFaults = lineFaults;
    }

    public void applyTo(Network network) {

        network.getBusMap().values().forEach(b -> b.setFaultCandidate(false));
        network.getLineMap().values().forEach(l -> l.setFaultCandidate(false));

        busFaults.forEach(id -> network.getBus(id).setFaultCandidate(true));
        lineFaults.forEach(id -> network.getLine(id).setFaultCandidate(true));
    }

}
