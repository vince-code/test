
import com.uppaal.model.core2.Document;
import org.junit.Test;
import polimi.logic.generator.UppaalModelGenerator;
import polimi.logic.splitter.NetworkFaultSplitter;
import polimi.logic.verifier.UppaalClient;
import polimi.model.VerificationResult;
import polimi.logic.NetworkBuilder;
import polimi.model.Network;
import java.io.File;
import java.util.List;

public class UppaalVerificationTest {

    @Test
    public void generateSplitDocumentsNetwork() throws Exception {
        String uppaalHome = "UPPAAL-5.1.0-beta5.app/Contents/Resources/uppaal";
        String jsonPath = "example/jsonNet/Example_20bus_Network_1.json";

        File jsonFile = new File(jsonPath);
        System.out.println(jsonFile.getName());
        Network network = NetworkBuilder.build(jsonFile);

        UppaalClient uppaal = new UppaalClient();
        System.out.println("\n=== Single Verification ===\n");
        long start = System.nanoTime();
        Document singleModel = new UppaalModelGenerator(network).generateDocument(true);
        long modelGeneration = System.nanoTime();
        System.out.println("Model generation: " + ((modelGeneration-start) / 1_000_000.0) + " milliseconds");

        //VerificationResult singleVerification = uppaal.verifyWithRichResult(singleModel, singleModel.getQueryList().get(0));
        //System.out.println("\t" + singleVerification);
        long end = System.nanoTime();
        System.out.println("Uppaal Verification Time: " + ((end-modelGeneration) / 1_000_000.0) + " milliseconds");

        System.out.println("\n=== Split Verification ===\n");

        start = System.nanoTime();
        NetworkFaultSplitter networkFaultSplitter = new NetworkFaultSplitter(network);
        List<Document> splitDocuments = networkFaultSplitter.generateSplitDocuments(8, true);
        long splitModelTimestamp = System.nanoTime();

        System.out.println("Split ("+ splitDocuments.size() +") models generation: " + ((splitModelTimestamp-start) / 1_000_000.0) + " milliseconds");
        long startVerifications = System.nanoTime();
        int i = 1;
        for (Document document : splitDocuments){
            //document.save("/Users/enzo/Desktop/Tesi/splitTest/Java/generated" + i + ".xml");
            VerificationResult result = uppaal.verify(document, document.getQueryList().get(0), false);
            System.out.println("Split " + i + " " + result);
            i++;
        }
        end = System.nanoTime();
        System.out.println("Uppaal total Verification time: " + ((end-startVerifications) / 1_000_000.0) + " milliseconds");
        System.out.println("Total: " + ((end-start) / 1_000_000.0) + " milliseconds");
    }

}
