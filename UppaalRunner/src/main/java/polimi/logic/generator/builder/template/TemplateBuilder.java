package polimi.logic.generator.builder.template;

import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.ModelException;
import com.uppaal.model.core2.Template;
import polimi.logic.generator.builder.template.model.Label;
import polimi.logic.generator.builder.template.model.LabelType;
import polimi.logic.generator.builder.template.model.UppaalLocation;
import polimi.model.Network;

public abstract class TemplateBuilder {

    abstract Template build(Network network, Document document) throws ModelException;

    static Location createLocation(Template template, UppaalLocation loc) throws ModelException {
        Location location = template.addLocation();
        location.setProperty(LabelType.init.name(), loc.isInitial());
        location.setXY(loc.getPosition().getX(), loc.getPosition().getY());

        for (Label label : loc.getLabels()) {
            location.setProperty(label.getType().name(), label.getValue())
                    .setXY(label.getPosition().getX(), label.getPosition().getY());
        }

        return location;
    }
}
