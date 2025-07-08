package polimi.model;

import lombok.Getter;

@Getter
public enum SelectivityType {
    TIME_CURRENT(1),
    DIRECTIONAL_ZONE(3);

    final int selTypeId;

    SelectivityType(int selTypeId){
        this.selTypeId = selTypeId;
    }

    public static SelectivityType getTypeFromId(int selTypeId){
        for (SelectivityType type : SelectivityType.values()){
            if (type.getSelTypeId() == selTypeId) return type;
        }
        return null;
    }

}
