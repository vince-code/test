package polimi.logic;

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

            CircuitBreaker cb = new CircuitBreaker(
                    cbId, line, bus,
                    cbNode.get("Sel_Type").asInt(),
                    cbNode.get("m").asInt(),
                    cbNode.get("i1").asInt(),
                    cbNode.get("t1").asInt(),
                    cbNode.get("i2").asInt(),
                    cbNode.get("t2").asInt(),
                    cbNode.get("i2_flag").asInt() == 1,
                    (cbNode.hasNonNull("i3") && !cbNode.get("i3").asText().equalsIgnoreCase("Nan")) ?
                            Integer.parseInt(cbNode.get("i3").asText()) : null,
                    cbNode.get("i3_flag").asInt() == 1
            );

            cbMap.put(cbId, cb);
            line.setCircuitBreaker(cb);
        }

        return new Network(busMap, lineMap, cbMap, sources);
    }
}