package polimi.model;

import lombok.Data;
import polimi.logic.PathFinder;
import polimi.logic.RadialPathFinder;

import java.util.*;

@Data
public class Network {

    private final Map<Integer, Bus> busMap;
    private final Map<Integer, Line> lineMap;
    private final Map<Integer, CircuitBreaker> cbMap;
    private final List<Source> sources;

    private final NetworkType type;
    private final PathFinder pathFinder;

    private final Map<PathKey, List<Line>> cachedPaths = new HashMap<>();

    public Network(Map<Integer, Bus> busMap, Map<Integer, Line> lineMap, Map<Integer, CircuitBreaker> cbMap, List<Source> sources) {
        this.busMap = busMap;
        this.lineMap = lineMap;
        this.cbMap = cbMap;
        this.sources = sources;
        this.type = detectNetworkType();
        this.pathFinder = this.type.getPathFinder();
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

    public boolean hasCycles(){
        return lineMap.size() >= busMap.size();
    }

    private NetworkType detectNetworkType(){
        if (hasCycles()){
            return NetworkType.CLOSED_RING;
        }
        if (sources.size() == 1){
            return NetworkType.RADIAL;
        }
        return NetworkType.OPEN_RING;
    }

    public List<Line> getCachedPath(Bus fromBus, Bus toBus) {
        PathKey pathKey = new PathKey(fromBus.getId(), toBus.getId());
        return cachedPaths.computeIfAbsent(pathKey, k -> pathFinder.findPath(fromBus, toBus));
    }

    public List<Line> getCachedPath(int fromBusId, int toBusId) {
        PathKey pathKey = new PathKey(fromBusId, toBusId);
        return cachedPaths.computeIfAbsent(pathKey, k -> pathFinder.findPath(getBus(fromBusId), getBus(toBusId)));
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
}
