package polimi.model;

import lombok.Data;
import java.util.*;

@Data
public class Network {
    public final Map<Integer, Bus> busMap;
    public final Map<Integer, Line> lineMap;
    public final Map<Integer, CircuitBreaker> cbMap;
    public final List<Source> sources;

    private final Map<PathKey, List<Line>> cachedPaths = new HashMap<>();

    public Network(Map<Integer, Bus> busMap, Map<Integer, Line> lineMap, Map<Integer, CircuitBreaker> cbMap, List<Source> sources) {
        this.busMap = busMap;
        this.lineMap = lineMap;
        this.cbMap = cbMap;
        this.sources = sources;
        this.precalculateAllPaths();
    }

    public Bus getBus(int busId) {
        Bus bus = busMap.get(busId);
        if (bus == null) {
            throw new IllegalArgumentException("Bus not found: " + busId);
        }
        return bus;
    }

    public Line getLine(int lineId) {
        Line line = lineMap.get(lineId);
        if (line == null) {
            throw new IllegalArgumentException("Line not found: " + lineId);
        }
        return line;
    }

    public CircuitBreaker getCB(int cbId) {
        CircuitBreaker cb = cbMap.get(cbId);
        if (cb == null) {
            throw new IllegalArgumentException("CB not found: " + cbId);
        }
        return cb;
    }

    public List<Line> getCachedPath(Bus fromBus, Bus toBus) {
        PathKey pathKey = new PathKey(fromBus.getId(), toBus.getId());
        return cachedPaths.computeIfAbsent(pathKey, k -> findPath(fromBus, toBus));
    }

    public List<Line> getCachedPath(int fromBusId, int toBusId) {
        PathKey pathKey = new PathKey(fromBusId, toBusId);
        return cachedPaths.computeIfAbsent(pathKey, k -> findPath(getBus(fromBusId), getBus(toBusId)));
    }

    public void precalculateAllPaths() {
        for (Bus fromBus : busMap.values()) {
            for (Bus toBus : busMap.values()) {
                if (fromBus.getId() != toBus.getId()) {
                    getCachedPath(fromBus, toBus);
                }
            }
        }
    }

    private List<Line> findPath(Bus startingBus, Bus targetBus) {
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
