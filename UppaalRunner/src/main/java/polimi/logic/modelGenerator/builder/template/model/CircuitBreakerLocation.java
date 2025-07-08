package polimi.logic.modelGenerator.builder.template.model;

import lombok.Getter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum CircuitBreakerLocation implements UppaalLocation {
    CLOSED(
            true,
            new Position(-364, -68),
            Set.of(new Label(LabelType.name, "Closed", new Position(-382, -102)))
    ),
    STANDBY(
            false,
            new Position(-119, -200),
            Set.of(
                    new Label(LabelType.name, "Standby", new Position(-144, -234)),
                    new Label(LabelType.invariant, "x<=t", new Position(-127, -190)))
    ),
    OPEN(
            false,
            new Position(119, -68),
            Set.of(new Label(LabelType.name, "Open", new Position(109, -102)))
    );

    private final boolean initial;
    private final Position position;
    private final Set<Label> labels;
    private final Map<LabelType, Label> labelMap;

    CircuitBreakerLocation(boolean isInitial,Position position, Set<Label> labels) {
        this.initial = isInitial;
        this.position = position;
        this.labels = labels;
        this.labelMap = labels.stream().collect(Collectors.toMap(Label::getType, l -> l));
    }

    @Override
    public Label getLabel(LabelType type) {
        return labelMap.get(type);
    }

    @Override
    public String getLabelValue(LabelType type) {
        Label label = getLabel(type);
        return label != null ? label.getValue() : null;
    }

    @Override
    public Position getLabelPosition(LabelType type) {
        Label label = getLabel(type);
        return label != null ? label.getPosition() : null;
    }
}

