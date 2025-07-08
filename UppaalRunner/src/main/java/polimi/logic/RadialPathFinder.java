package polimi.logic;

import polimi.model.Bus;
import polimi.model.Line;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RadialPathFinder implements PathFinder{

    @Override
    public List<Line> findPath(Bus startingBus, Bus targetBus) {
        return findPath(startingBus, targetBus, new HashSet<>());
    }

    private List<Line> findPath(Bus currentBus, Bus targetBus, Set<Bus> visited) {
        visited.add(currentBus);
        if (currentBus.equals(targetBus)) return new ArrayList<>();

        for (Line line : currentBus.getOutLines()) {
            Bus nextBus = line.getToBus();
            if (!visited.contains(nextBus)) {
                List<Line> subPath = findPath(nextBus, targetBus, visited);
                if (subPath != null) {
                    subPath.add(0, line);
                    return subPath;
                }
            }
        }
        return null;
    }
}
