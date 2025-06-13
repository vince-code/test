package matlabComparisonTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import polimi.logic.NetworkBuilder;
import polimi.logic.splitter.FaultScenario;
import polimi.logic.splitter.NetworkFaultSplitter;
import polimi.model.Network;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FaultSplitterComparisonTest {
/*
    private static final String JSON_DIR = "example/jsonNet";
    private static final String SPLIT_REF_DIR = "example/splitReferences";

    @Getter
    public static class SplitReference {
        public int div;
        public int[][] F_branch_matrix;
        public int[][] F_bus_matrix;
        public int[] LineIDs;
        public int[] BusIDs;
    }

    @ParameterizedTest
    @MethodSource("jsonFilesProvider")
    public void faultSplitMatlabComparisonTest(File refFile) throws Exception {

        String jsonFileName = refFile.getName().replace("_split.json",".json");
        File jsonFile = new File(JSON_DIR, jsonFileName);
        Network network = NetworkBuilder.build(jsonFile);

        NetworkFaultSplitter splitter = new NetworkFaultSplitter(network);
        List<FaultScenario> faultScenarioList = splitter.splitFaults(8);

        assertTrue(jsonFile.exists(), "Json file not found: " + jsonFile.getAbsolutePath());

        ObjectMapper mapper = new ObjectMapper();
        SplitReference matlabSplit = mapper.readValue(refFile, SplitReference.class);

        assertEquals(matlabSplit.getDiv(), faultScenarioList.size(), "Mismatch in number of fault scenarios");

        for (int i = 0; i < matlabSplit.getDiv(); i++) {
            FaultScenario scenario = faultScenarioList.get(i);

            int[] lineIds = matlabSplit.getLineIDs();
            int[] busIds = matlabSplit.getBusIDs();

            Set<Integer> expectedLines = Arrays.stream(matlabSplit.F_branch_matrix[i])
                    .filter(idx -> idx > 0)
                    .mapToObj(idx -> lineIds[idx - 1])
                    .collect(Collectors.toSet());

            Set<Integer> expectedBuses = Arrays.stream(matlabSplit.F_bus_matrix[i])
                    .filter(idx -> idx > 0)
                    .mapToObj(idx -> busIds[idx - 1])
                    .collect(Collectors.toSet());

            assertEquals(expectedLines, scenario.getLineFaults(), "Line faults mismatch in scenario " + (i + 1));
            assertEquals(expectedBuses, scenario.getBusFaults(), "Bus faults mismatch in scenario " + (i + 1));
            System.out.println("Matlab Lines: " + expectedLines);
            System.out.println("Matlab buses: " + expectedBuses);
        }

    }

    static Stream<File> jsonFilesProvider() {
        File dir = new File(SPLIT_REF_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalStateException("Directory does not exist: " + SPLIT_REF_DIR);
        }

        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(f -> f.getName().endsWith(".json"));
    }

 */
}
