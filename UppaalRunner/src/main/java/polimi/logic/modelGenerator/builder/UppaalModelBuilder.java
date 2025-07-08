package polimi.logic.modelGenerator.builder;

import com.uppaal.model.core2.*;
import polimi.logic.modelGenerator.builder.declarations.GlobalDeclarationsBuilder;
import polimi.logic.modelGenerator.builder.template.CBTemplateBuilder;
import polimi.logic.modelGenerator.builder.template.FaultGenTemplateBuilder;
import polimi.model.CircuitBreaker;
import polimi.model.Network;

public class UppaalModelBuilder {
    private final Network network;
    private final Document document;

    public UppaalModelBuilder(Network network) {
        this.network = network;
        this.document = new Document(new DocumentPrototype());
    }

    public UppaalModelBuilder addGlobalDeclarations() {
        GlobalDeclarationsBuilder declarationsBuilder = new GlobalDeclarationsBuilder(network);
        document.setProperty("declaration", declarationsBuilder.build());
        return this;
    }

    public UppaalModelBuilder addFaultGeneratorTemplate() throws ModelException {
        FaultGenTemplateBuilder templateBuilder = new FaultGenTemplateBuilder();
        Template fgTemplate = templateBuilder.build(network, document);
        document.insert(fgTemplate, document.getLastTATemplate());
        return this;
    }

    public UppaalModelBuilder addCircuitBreakerTemplates() throws ModelException {
        for (CircuitBreaker cb : network.getCbMap().values()) {
            CBTemplateBuilder templateBuilder = new CBTemplateBuilder(cb);
            Template cbTemplate = templateBuilder.build(network, document);
            document.insert(cbTemplate, document.getLastTATemplate());
        }
        return this;
    }

    public UppaalModelBuilder addSystemDeclaration() {
        document.setProperty("system", SystemDeclarationsBuilder.build(network));
        return this;
    }

    public UppaalModelBuilder addQuery() {
        Query query = QueryBuilder.generateQuery(network);
        document.getQueryList().add(query);
        return this;
    }

    public Document build() {
        return document;
    }
}
