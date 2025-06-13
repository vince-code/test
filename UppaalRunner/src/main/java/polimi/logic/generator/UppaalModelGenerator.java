package polimi.logic.generator;

import com.uppaal.model.core2.*;
import polimi.logic.generator.builder.UppaalModelBuilder;
import polimi.model.Network;

public class UppaalModelGenerator {

    private final Network network;

    public UppaalModelGenerator(Network network) {
        this.network = network;
    }

    public Document generateDocument(boolean withQuery){
        try {
            UppaalModelBuilder modelBuilder = new UppaalModelBuilder(network)
                    .addGlobalDeclarations()
                    .addFaultGeneratorTemplate()
                    .addCircuitBreakerTemplates();

            if (withQuery) modelBuilder.addQuery();
            modelBuilder.addSystemDeclaration();

            return modelBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
