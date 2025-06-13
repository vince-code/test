package polimi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"line", "bus"})
@ToString(exclude = {"line", "bus"})
public class CircuitBreaker {
    private int id;
    private Line line;
    private Bus bus;
    private int selType;
    private int m;
    private int i1, t1, i2, t2;
    private boolean i2Flag;
    private Integer i3;
    private boolean i3Flag;
}
