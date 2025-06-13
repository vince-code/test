package polimi.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(exclude = {"outLines", "inLines" })
@ToString(exclude = {"outLines", "inLines"})
public class Bus {
    private final int id;
    private boolean faultCandidate;
    private boolean isSource;

    private final List<Line> outLines = new ArrayList<>();
    private final List<Line> inLines = new ArrayList<>();


}
