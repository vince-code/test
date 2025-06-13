package polimi.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(exclude = {"sourceBus", "iscLines", "iscBuses"})
@ToString(exclude = {"sourceBus", "iscLines", "iscBuses"})
public class Source {
    private final Bus sourceBus;
    private final Map<Integer, Integer> iscLines = new HashMap<>();
    private final Map<Integer, Integer> iscBuses = new HashMap<>();
}
