package polimi.logic;

import polimi.model.Bus;
import polimi.model.Line;

import java.util.List;

public interface PathFinder {
    List<Line> findPath(Bus startingBus, Bus targetBus);
}
