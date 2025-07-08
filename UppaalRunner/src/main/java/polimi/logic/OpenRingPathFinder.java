package polimi.logic;

import polimi.model.Bus;
import polimi.model.Line;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OpenRingPathFinder implements PathFinder{

    @Override
    public List<Line> findPath(Bus startingBus, Bus targetBus) {
        return findPath(startingBus, targetBus, new HashSet<>());
    }

    private List<Line> findPath(Bus currentBus, Bus targetBus, Set<Bus> visited) {
        visited.add(currentBus);

        // Base case: reached target
        if (currentBus.equals(targetBus)) {
            return new ArrayList<>();
        }

        // In open ring networks, we need to explore paths in both directions
        List<Line> allAccessibleLines = new ArrayList<>();

        // Add outgoing lines (normal direction: fromBus -> toBus)
        allAccessibleLines.addAll(currentBus.getOutLines());

        // Add incoming lines (reverse direction: toBus -> fromBus)
        // These represent the ability to traverse lines in the opposite direction
        allAccessibleLines.addAll(currentBus.getInLines());

        // Explore all accessible lines
        for (Line line : allAccessibleLines) {
            Bus nextBus = getNextBus(line, currentBus);

            // Avoid cycles
            if (!visited.contains(nextBus)) {
                List<Line> subPath = findPath(nextBus, targetBus, visited);
                if (subPath != null) {
                    // Found a path: add current line at the beginning
                    subPath.add(0, line);
                    return subPath;
                }
            }
        }

        // No path found from this branch
        return null;
    }

    /**
     * Determines the next bus when traversing a line from the current bus.
     * Handles bidirectional traversal by checking if we're going in the
     * normal direction (fromBus -> toBus) or reverse direction (toBus -> fromBus).
     *
     * @param line the line to traverse
     * @param currentBus the bus we're currently at
     * @return the next bus to visit
     */
    private Bus getNextBus(Line line, Bus currentBus) {
        if (line.getFromBus().equals(currentBus)) {
            // Normal direction: fromBus -> toBus
            return line.getToBus();
        } else if (line.getToBus().equals(currentBus)) {
            // Reverse direction: toBus -> fromBus
            return line.getFromBus();
        } else {
            throw new IllegalStateException(
                    "Line " + line.getId() + " is not connected to current bus " + currentBus.getId()
            );
        }
    }
}
