package polimi.logic.networkParsing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkJson {
    @JsonProperty("Network")
    private NetworkData network;

    @JsonProperty("CB_settings")
    private CBSettings cbSettings;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class NetworkData {
    @JsonProperty("Bus")
    private ArrayWrapper bus;

    @JsonProperty("FaultBus")
    private ArrayWrapper faultBus;

    @JsonProperty("Sources")
    private List<SourceData> sources;

    @JsonProperty("Lines")
    private List<LineData> lines;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class ArrayWrapper {
    @JsonProperty("_ArrayData_")
    private List<Integer> arrayData;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class SourceData {
    @JsonProperty("Bus_ID")
    private int busId;

    @JsonProperty("Isc_Lines")
    private List<Integer> iscLines;

    @JsonProperty("Isc_Buses")
    private List<Integer> iscBuses;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class LineData {
    @JsonProperty("ID")
    private int id;

    @JsonProperty("IncidentBus")
    private int incidentBus;

    @JsonProperty("OutgoingBus")
    private int outgoingBus;

    @JsonProperty("Iioc")
    private int iioc;

    @JsonProperty("Ith")
    private int ith;

    @JsonProperty("FaultFlag")
    private int faultFlag;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class CBSettings {
    @JsonProperty("CB")
    private List<CBData> cb;

    @JsonProperty("Ekip_Link")
    private List<EkipLinkData> ekipLink;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class CBData {
    @JsonProperty("CB_ID")
    private int cbId;

    @JsonProperty("Line_ID")
    private int lineId;

    @JsonProperty("Bus_ID")
    private int busId;

    @JsonProperty("Sel_Type")
    private int selType;

    @JsonProperty("m")
    private int m;

    @JsonProperty("i1")
    private int i1;

    @JsonProperty("t1")
    private int t1;

    // Campi specifici TIME_CURRENT (null per DIRECTIONAL_ZONE)
    @JsonProperty("i2")
    private Integer i2;

    @JsonProperty("t2")
    private Integer t2;

    @JsonProperty("i2_flag")
    private Integer i2Flag;

    @JsonProperty("i3")
    private String i3; // Manteniamo String per gestire "Nan"

    @JsonProperty("i3_flag")
    private Integer i3Flag;

    // Campi specifici DIRECTIONAL_ZONE (null per TIME_CURRENT)
    @JsonProperty("i7")
    private Integer i7;

    @JsonProperty("t7FW")
    private Integer t7FW;

    @JsonProperty("t7BW")
    private Integer t7BW;

    @JsonProperty("tselFW")
    private Integer tselFW;

    @JsonProperty("tselBW")
    private Integer tselBW;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class EkipLinkData {
    @JsonProperty("CB_ID")
    private int cbId;

    @JsonProperty("Line_ID")
    private int lineId;

    @JsonProperty("Actors")
    private List<ActorData> actors;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class ActorData {
    @JsonProperty("Actor_CB")
    private int actorCb;

    @JsonProperty("Actor_CB_Dir")
    private int actorCbDir;

    @JsonProperty("CB_Dir")
    private int cbDir;
}
