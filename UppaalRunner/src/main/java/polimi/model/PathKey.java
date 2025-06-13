package polimi.model;

import java.util.Objects;

public class PathKey {
    private final int fromBusId;
    private final int toBusId;

    public PathKey(int fromBusId, int toBusId) {
        this.fromBusId = fromBusId;
        this.toBusId = toBusId;
    }

    public int getFromBusId() {
        return fromBusId;
    }

    public int getToBusId() {
        return toBusId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathKey)) return false;
        PathKey that = (PathKey) o;
        return fromBusId == that.fromBusId && toBusId == that.toBusId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromBusId, toBusId);
    }

    @Override
    public String toString() {
        return "PathKey(" + fromBusId + " -> " + toBusId + ")";
    }
}

