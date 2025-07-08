package polimi.logic.modelGenerator.builder.template.model;

import lombok.Data;

@Data
public class Label {
    private final LabelType type;
    private final String value;
    private final Position position;
}
