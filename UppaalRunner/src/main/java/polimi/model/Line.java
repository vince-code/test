package polimi.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(exclude = {"fromBus", "toBus", "circuitBreaker"})
@ToString(exclude = {"fromBus", "toBus", "circuitBreaker"})
public class Line {
    private final int id;
    private final Bus fromBus;
    private final Bus toBus;
    private final int iioc;
    private final int ith;
    private boolean faultCandidate;

    private CircuitBreaker circuitBreaker;
}
