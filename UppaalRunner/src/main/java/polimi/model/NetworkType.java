package polimi.model;

import polimi.logic.OpenRingPathFinder;
import polimi.logic.PathFinder;
import polimi.logic.RadialPathFinder;

public enum NetworkType {
    RADIAL(new RadialPathFinder()),
    OPEN_RING(new OpenRingPathFinder()),
    CLOSED_RING(null); // TODO: new ClosedRingPathFinder()

    private final PathFinder pathFinder;

    NetworkType(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    /**
     * Returns the appropriate PathFinder strategy for this network type.
     *
     * @return the PathFinder instance for this network type
     * @throws UnsupportedOperationException if the PathFinder is not yet implemented
     */
    public PathFinder getPathFinder() {
        if (pathFinder == null) {
            throw new UnsupportedOperationException("PathFinder for " + this + " not yet implemented");
        }
        return pathFinder;
    }
}
