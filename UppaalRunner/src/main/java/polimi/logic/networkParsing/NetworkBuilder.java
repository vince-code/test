package polimi.logic.networkParsing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import polimi.model.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class NetworkBuilder {

    public static Network build(File networkJsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(networkJsonFile);

        Map<Integer, Bus> busMap = new HashMap<>();
        Map<Integer, Line> lineMap = new HashMap<>();
        Map<Integer, CircuitBreaker> cbMap = new HashMap<>();
        List<Source> sources = new ArrayList<>();

        JsonNode networkNode = root.get("Network");
        JsonNode busArray = networkNode.get("Bus").get("_ArrayData_");
        JsonNode faultBusArray = networkNode.get("FaultBus").get("_ArrayData_");

        Set<Integer> faultBusSet = new HashSet<>();
        for (JsonNode fb : faultBusArray) {
            faultBusSet.add(fb.asInt());
        }

        for (JsonNode busIdNode : busArray) {
            int busId = busIdNode.asInt();
            Bus bus = new Bus(busId);
            bus.setFaultCandidate(faultBusSet.contains(busId));
            busMap.put(busId, bus);
        }

        Set<Integer> sourceBusIds = new HashSet<>();
        for (JsonNode sourceNode : networkNode.get("Sources")) {
            int busId = sourceNode.get("Bus_ID").asInt();
            sourceBusIds.add(busId);
        }

        for (int busId : sourceBusIds) {
            busMap.get(busId).setSource(true);
        }

        JsonNode linesNode = networkNode.get("Lines");
        for (JsonNode lineNode : linesNode) {
            int id = lineNode.get("ID").asInt();
            int fromId = lineNode.get("OutgoingBus").asInt();
            int toId = lineNode.get("IncidentBus").asInt();
            int iioc = lineNode.get("Iioc").asInt();
            int ith = lineNode.get("Ith").asInt();
            boolean fault = lineNode.get("FaultFlag").asInt() == 1;
            if (busMap.get(fromId).isSource()) fault = false;

            Bus from = busMap.get(fromId);
            Bus to = busMap.get(toId);
            Line line = new Line(id, from, to, iioc, ith);
            line.setFaultCandidate(fault);
            lineMap.put(id, line);
            from.getOutLines().add(line);
            to.getInLines().add(line);
        }

        for (JsonNode sourceNode : networkNode.get("Sources")) {
            int busId = sourceNode.get("Bus_ID").asInt();
            Bus srcBus = busMap.get(busId);
            Source source = new Source(srcBus);
            sources.add(source);

            JsonNode iscLines = sourceNode.get("Isc_Lines");
            JsonNode iscBuses = sourceNode.get("Isc_Buses");

            int i = 0;
            for (JsonNode lineNode : linesNode) {
                Line line = lineMap.get(lineNode.get("ID").asInt());
                int isc = iscLines.get(i++).asInt();
                source.getIscLines().put(line.getId(), isc);
            }

            i = 0;
            for (JsonNode busIdNode : busArray) {
                Bus bus = busMap.get(busIdNode.asInt());
                int isc = iscBuses.get(i++).asInt();
                source.getIscBuses().put(bus.getId(), isc);
            }
        }

        JsonNode cbArray = root.get("CB_settings").get("CB");
        for (JsonNode cbNode : cbArray) {
            int cbId = cbNode.get("CB_ID").asInt();
            Line line = lineMap.get(cbNode.get("Line_ID").asInt());
            Bus bus = busMap.get(cbNode.get("Bus_ID").asInt());

            // Leggi il tipo di selettività
            int selTypeId = cbNode.get("Sel_Type").asInt();
            SelectivityType selType = SelectivityType.getTypeFromId(selTypeId);

            // Campi comuni per tutti i tipi
            int m = cbNode.get("m").asInt();
            int i1 = cbNode.get("i1").asInt();
            int t1 = cbNode.get("t1").asInt();

            // Campi specifici per Time-Current (null per Directional Zone)
            Integer i2 = null;
            Integer t2 = null;
            Boolean i2Flag = null;
            Integer i3 = null;
            Boolean i3Flag = null;

            // Campi specifici per Directional Zone (null per Time-Current)
            Integer i7 = null;
            Integer t7FW = null;
            Integer t7BW = null;
            Boolean tselFW = null;
            Boolean tselBW = null;

            if (selType == SelectivityType.TIME_CURRENT) {
                // Leggi i campi specifici per TIME_CURRENT
                i2 = cbNode.get("i2").asInt();
                t2 = cbNode.get("t2").asInt();
                i2Flag = cbNode.get("i2_flag").asInt() == 1;
                i3 = (cbNode.hasNonNull("i3") && !cbNode.get("i3").asText().equalsIgnoreCase("Nan")) ?
                        Integer.parseInt(cbNode.get("i3").asText()) : null;
                i3Flag = cbNode.get("i3_flag").asInt() == 1;
            } else if (selType == SelectivityType.DIRECTIONAL_ZONE) {
                // Leggi i campi specifici per DIRECTIONAL_ZONE
                i7 = cbNode.get("i7").asInt();
                t7FW = cbNode.get("t7FW").asInt();
                t7BW = cbNode.get("t7BW").asInt();
                tselFW = cbNode.get("tselFW").asInt() == 1;
                tselBW = cbNode.get("tselBW").asInt() == 1;
            }

            CircuitBreaker cb = new CircuitBreaker(
                    cbId, line, bus, selType, m, i1, t1, i2, t2, i2Flag, i3, i3Flag,
                    i7, t7FW, t7BW, tselFW, tselBW, new ArrayList<>()
            );

            cbMap.put(cbId, cb);
            line.setCircuitBreaker(cb);
        }

        if (root.get("CB_settings").has("Ekip_Link")) {
            JsonNode ekipLinkArray = root.get("CB_settings").get("Ekip_Link");
            for (JsonNode ekipLinkNode : ekipLinkArray) {
                int cbId = ekipLinkNode.get("CB_ID").asInt();
                CircuitBreaker cb = cbMap.get(cbId);

                if (cb != null && cb.getSelType() == SelectivityType.DIRECTIONAL_ZONE && ekipLinkNode.has("Actors")) {
                    JsonNode actorsArray = ekipLinkNode.get("Actors");
                    List<Actor> actors = new ArrayList<>();

                    for (JsonNode actorNode : actorsArray) {
                        int actorCbId = actorNode.get("Actor_CB").asInt();
                        int actorCbDir = actorNode.get("Actor_CB_Dir").asInt();
                        int cbDir = actorNode.get("CB_Dir").asInt();

                        CircuitBreaker actorCb = cbMap.get(actorCbId);
                        if (actorCb != null) {
                            CurrentDirection actorDirection = (actorCbDir == 1) ?
                                    CurrentDirection.FORWARD : CurrentDirection.BACKWARD;
                            CurrentDirection blockingDirection = (cbDir == 1) ?
                                    CurrentDirection.FORWARD : CurrentDirection.BACKWARD;

                            Actor actor = new Actor(actorCb, actorDirection, blockingDirection);
                            actors.add(actor);
                        }
                    }

                    cb.setEkipLinkActors(actors);
                }
            }
        }

        return new Network(busMap, lineMap, cbMap, sources);
    }
}