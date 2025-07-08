package polimi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Actor {
    private CircuitBreaker circuitBreaker;
    private CurrentDirection actorDirection;
    private CurrentDirection blockingDirection;
}
