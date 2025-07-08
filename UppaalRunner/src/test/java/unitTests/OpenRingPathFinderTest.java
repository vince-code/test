package unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import polimi.logic.networkParsing.NetworkBuilder;
import polimi.logic.OpenRingPathFinder;
import polimi.logic.PathFinder;
import polimi.model.*;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Open Ring Network Test - Example_Open_Ring_4_DZ_TC_no_CB19")
public class OpenRingPathFinderTest {

    private Network network;
    private PathFinder openRingPathFinder;

    @BeforeEach
    public void setUp() throws Exception {
        // Load the open ring network from JSON
        File jsonFile = new File("example/jsonNet/Example_Open_Ring_4_DZ_TC_no_CB19.json");
        network = NetworkBuilder.build(jsonFile);
        openRingPathFinder = new OpenRingPathFinder();
    }

    @Test
    @DisplayName("Network should be correctly identified as OPEN_RING type")
    public void testNetworkTypeDetection() {
        assertEquals(NetworkType.OPEN_RING, network.getType(),
                "Network should be detected as OPEN_RING type");
    }

    @Test
    @DisplayName("Network should have multiple sources")
    public void testMultipleSources() {
        List<Source> sources = network.getSources();
        assertEquals(5, sources.size(), "Open ring network should have 5 sources");

        // Verify source bus IDs match the JSON
        Set<Integer> sourceBusIds = sources.stream()
                .map(source -> source.getSourceBus().getId())
                .collect(Collectors.toSet());

        Set<Integer> expectedSourceIds = Set.of(8, 14, 15, 16, 21);
        assertEquals(expectedSourceIds, sourceBusIds, "Source bus IDs should match JSON configuration");
    }

    @Test
    @DisplayName("Network should not have cycles (open ring)")
    public void testNoCycles() {
        assertFalse(network.hasCycles(), "Open ring network should not have cycles");
    }

    @Test
    @DisplayName("Network should use OpenRingPathFinder strategy")
    public void testPathFinderStrategy() {
        PathFinder pathFinder = network.getPathFinder();
        assertInstanceOf(OpenRingPathFinder.class, pathFinder,
                "Network should use OpenRingPathFinder strategy");
    }

    @Test
    @DisplayName("Test bidirectional path finding in open ring")
    public void testBidirectionalPathFinding() {
        // Test case: find path from bus 21 to bus 8
        // In a radial network this would fail, but in open ring it should succeed
        Bus bus21 = network.getBus(21);
        Bus bus8 = network.getBus(8);

        List<Line> path = network.getCachedPath(bus21, bus8);
        assertNotNull(path, "Should find a path from bus 21 to bus 8 in open ring network");
        assertFalse(path.isEmpty(), "Path should not be empty");

        // Verify the path is valid (first line connects to source bus, last line connects to target bus)
        Line firstLine = path.get(0);
        Line lastLine = path.get(path.size() - 1);

        assertTrue(firstLine.getFromBus().equals(bus21) || firstLine.getToBus().equals(bus21),
                "First line should connect to source bus 21");
        assertTrue(lastLine.getFromBus().equals(bus8) || lastLine.getToBus().equals(bus8),
                "Last line should connect to target bus 8");
    }

    @Test
    @DisplayName("Test reverse direction path finding")
    public void testReverseDirectionPath() {
        // Test finding path in reverse direction: bus 8 to bus 21
        Bus bus8 = network.getBus(8);
        Bus bus21 = network.getBus(21);

        List<Line> forwardPath = network.getCachedPath(bus8, bus21);
        List<Line> reversePath = network.getCachedPath(bus21, bus8);

        assertNotNull(forwardPath, "Should find forward path");
        assertNotNull(reversePath, "Should find reverse path");

        // Both paths should exist but may be different due to bidirectional nature
        assertFalse(forwardPath.isEmpty(), "Forward path should not be empty");
        assertFalse(reversePath.isEmpty(), "Reverse path should not be empty");
    }

    @Test
    @DisplayName("Test path finding between non-source buses")
    public void testPathBetweenNonSourceBuses() {
        // Test path finding between two non-source buses
        Bus bus5 = network.getBus(5);  // Non-source bus
        Bus bus13 = network.getBus(13); // Non-source bus

        List<Line> path = network.getCachedPath(bus5, bus13);
        assertNotNull(path, "Should find path between non-source buses");
        assertFalse(path.isEmpty(), "Path between non-source buses should not be empty");
    }

    @Test
    @DisplayName("Test path caching works correctly")
    public void testPathCaching() {
        Bus bus8 = network.getBus(8);
        Bus bus21 = network.getBus(21);

        // Get path twice - should return same instance due to caching
        List<Line> path1 = network.getCachedPath(bus8, bus21);
        List<Line> path2 = network.getCachedPath(bus8, bus21);

        assertSame(path1, path2, "Cached paths should return same instance");
    }

    @Test
    @DisplayName("Test path finding to same bus returns empty path")
    public void testPathToSameBus() {
        Bus bus8 = network.getBus(8);

        List<Line> path = openRingPathFinder.findPath(bus8, bus8);
        assertNotNull(path, "Path to same bus should not be null");
        assertTrue(path.isEmpty(), "Path to same bus should be empty");
    }

    @Test
    @DisplayName("Test specific open ring connections")
    public void testSpecificOpenRingConnections() {
        // Test the ring structure based on the JSON
        // The ring should go through buses: 5 -> 8, 5 -> 18 -> 19 -> 20 -> 21
        // And also: 18 -> 14, 19 -> 15, 17 -> 16

        // Test connection from bus 5 to bus 21 (across the open ring)
        Bus bus5 = network.getBus(5);
        Bus bus21 = network.getBus(21);

        List<Line> path = network.getCachedPath(bus5, bus21);
        assertNotNull(path, "Should find path from bus 5 to bus 21 across open ring");

        // Verify the path goes through the ring (should include lines 18, 19, 20, 21)
        Set<Integer> pathLineIds = path.stream()
                .map(Line::getId)
                .collect(Collectors.toSet());

        // Should include some of the ring lines
        assertTrue(pathLineIds.size() > 1, "Path should traverse multiple lines in the ring");
    }

    @Test
    @DisplayName("Test fault candidate buses are correctly set")
    public void testFaultCandidateBuses() {
        // Based on JSON FaultBus array: [1,5,6,8,13,14,15,16,17,18,19,20,21]
        Set<Integer> expectedFaultBuses = Set.of(1, 5, 6, 8, 13, 14, 15, 16, 17, 18, 19, 20, 21);

        Set<Integer> actualFaultBuses = network.getBusMap().values().stream()
                .filter(Bus::isFaultCandidate)
                .map(Bus::getId)
                .collect(Collectors.toSet());

        assertEquals(expectedFaultBuses, actualFaultBuses,
                "Fault candidate buses should match JSON configuration");
    }

    @Test
    @DisplayName("Test fault candidate lines are correctly set")
    public void testFaultCandidateLines() {
        // Based on JSON, lines with FaultFlag: 1 are: 2, 3, 5, 9, 12
        Set<Integer> expectedFaultLines = Set.of(2, 3, 5, 9, 12);

        Set<Integer> actualFaultLines = network.getLineMap().values().stream()
                .filter(Line::isFaultCandidate)
                .map(Line::getId)
                .collect(Collectors.toSet());

        assertEquals(expectedFaultLines, actualFaultLines,
                "Fault candidate lines should match JSON configuration");
    }

    @Test
    @DisplayName("Test all paths are precalculated")
    public void testAllPathsPrecalculated() {
        // Verify that precalculateAllPaths() has cached paths between all bus pairs
        int totalBuses = network.getBusMap().size();
        int expectedCachedPaths = totalBuses * (totalBuses - 1); // n*(n-1) for all pairs except self

        // This is an indirect test - we verify that we can get paths between all buses
        for (Bus fromBus : network.getBusMap().values()) {
            for (Bus toBus : network.getBusMap().values()) {
                if (!fromBus.equals(toBus)) {
                    List<Line> path = network.getCachedPath(fromBus, toBus);
                    assertNotNull(path,
                            String.format("Path from bus %d to bus %d should exist",
                                    fromBus.getId(), toBus.getId()));
                }
            }
        }
    }
}
