package polimi.logic.generator.builder.template.model;

import java.util.Set;

public interface UppaalLocation {
    Position getPosition();
    Set<Label> getLabels();
    Label getLabel(LabelType labelType);
    String getLabelValue(LabelType labelType);
    Position getLabelPosition(LabelType labelType);
    boolean isInitial();
}
