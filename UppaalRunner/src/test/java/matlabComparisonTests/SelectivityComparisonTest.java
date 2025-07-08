package matlabComparisonTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import polimi.logic.Context;
import polimi.logic.networkParsing.NetworkBuilder;
import polimi.logic.SelectivityChecker;
import polimi.model.Network;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelectivityComparisonTest {
    private static final String JSON_DIR = "example/jsonNet";
    private static final String RESULT_DIR = "example/results";

    @Getter
    public static class MatlabResult {
        private double executionTime;
        private List<Integer> misconfiguredCBs;
    }

    @ParameterizedTest
    @MethodSource("jsonFilesProvider")
    public void compareMatlabResult(File jsonFile) throws Exception {
        String matlabResultFileName = jsonFile.getName().replace(".json", "_result.json");
        File matlabResultFile = new File(RESULT_DIR, matlabResultFileName);

        ObjectMapper mapper = new ObjectMapper();

        MatlabResult matlabResult = mapper.readValue(matlabResultFile, MatlabResult.class);
        List<Integer> expectedMisconfiguredCBs = matlabResult.misconfiguredCBs;

        Network network = NetworkBuilder.build(jsonFile);

        Context.initializeUppaalEnginesPool("UPPAAL-5.1.0-beta5.app/Contents/Resources/uppaal",9);
        Context.getInstance().setNetwork(network);
        List<Integer> actualMisconfiguredCBs = SelectivityChecker.extractMisconfiguredCBs(network,7);

        assertEquals(expectedMisconfiguredCBs, actualMisconfiguredCBs, "Incorrect Result");
    }

    static Stream<File> jsonFilesProvider() {
        File dir = new File(JSON_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalStateException("Directory does not exist: " + JSON_DIR);
        }

        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(f -> f.getName().endsWith(".json"));
    }
}
