package polimi.logic.generator.builder.template.model;

import lombok.Getter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum FaultGeneratorLocation implements UppaalLocation {
    NO_FAULT(
            true,
            new Position(-306, -127),
            Set.of(new Label(LabelType.name, "No_Fault", new Position(-357, -161)))
    ),
    FAULT_SIGNAL(
            false,
            new Position(263, -127),
            Set.of(new Label(LabelType.name, "Fault_Signal", new Position(253, -161)))
    ),
    RESET_READY(
            false,
            new Position(-306, 221),
            Set.of(new Label(LabelType.name, "Reset_Ready", new Position(-349, 238)))
    ),
    CHECK_FAULT(
            false,
            new Position(272, 221),
            Set.of(new Label(LabelType.name, "Check_Fault", new Position(238, 238)))
    ),
    FAULT(
            false,
            new Position(476, 25),
            Set.of(new Label(LabelType.name, "Fault", new Position(466, -9)))
    );

    private final boolean initial;
    private final Position position;
    private final Set<Label> labels;
    private final Map<LabelType, Label> labelMap;


    FaultGeneratorLocation(boolean isInitial, Position position, Set<Label> labels) {
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

