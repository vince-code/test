package polimi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"line", "bus", "ekipLinkActors"})
@ToString(exclude = {"line", "bus", "ekipLinkActors"})
public class CircuitBreaker {
    private int id;
    private Line line;
    private Bus bus;
    private SelectivityType selType;
    private int m;
    private int i1, t1;
    private Integer i2, t2;
    private Boolean i2Flag;
    private Integer i3;
    private Boolean i3Flag;
    private Integer i7;
    private Integer t7FW;
    private Integer t7BW;
    private Boolean tselFW;
    private Boolean tselBW;
    private List<Actor> ekipLinkActors;
}
