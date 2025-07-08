package polimi.logic.networkParsing;

import com.fasterxml.jackson.databind.ObjectMapper;
import polimi.model.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class JacksonNetworkBuilder {

    public static Network build(File networkJsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        NetworkJson networkJson = mapper.readValue(networkJsonFile, NetworkJson.class);

        Map<Integer, Bus> busMap = new HashMap<>();
        Map<Integer, Line> lineMap = new HashMap<>();
        Map<Integer, CircuitBreaker> cbMap = new HashMap<>();
        List<Source> sources = new ArrayList<>();

        NetworkData networkData = networkJson.getNetwork();

        Set<Integer> faultBusSet = new HashSet<>(networkData.getFaultBus().getArrayData());

        for (Integer busId : networkData.getBus().getArrayData()) {
            Bus bus = new Bus(busId);
            bus.setFaultCandidate(faultBusSet.contains(busId));
            busMap.put(busId, bus);
        }

        Set<Integer> sourceBusIds = new HashSet<>();
        for (SourceData sourceData : networkData.getSources()) {
            sourceBusIds.add(sourceData.getBusId());
        }

        for (int busId : sourceBusIds) {
            busMap.get(busId).setSource(true);
        }

        for (LineData lineData : networkData.getLines()) {
            int id = lineData.getId();
            int fromId = lineData.getOutgoingBus();
            int toId = lineData.getIncidentBus();
            int iioc = lineData.getIioc();
            int ith = lineData.getIth();
            boolean fault = lineData.getFaultFlag() == 1;
            if (busMap.get(fromId).isSource()) fault = false;

            Bus from = busMap.get(fromId);
            Bus to = busMap.get(toId);
            Line line = new Line(id, from, to, iioc, ith);
            line.setFaultCandidate(fault);
            lineMap.put(id, line);
            from.getOutLines().add(line);
            to.getInLines().add(line);
        }

        for (SourceData sourceData : networkData.getSources()) {
            int busId = sourceData.getBusId();
            Bus srcBus = busMap.get(busId);
            Source source = new Source(srcBus);
            sources.add(source);

            List<Integer> iscLines = sourceData.getIscLines();
            int i = 0;
            for (LineData lineData : networkData.getLines()) {
                Line line = lineMap.get(lineData.getId());
                if (i < iscLines.size()) {
                    int isc = iscLines.get(i++);
                    source.getIscLines().put(line.getId(), isc);
                }
            }

            List<Integer> iscBuses = sourceData.getIscBuses();
            i = 0;
            for (Integer busId2 : networkData.getBus().getArrayData()) {
                Bus bus = busMap.get(busId2);
                if (i < iscBuses.size()) {
                    int isc = iscBuses.get(i++);
                    source.getIscBuses().put(bus.getId(), isc);
                }
            }
        }

        for (CBData cbData : networkJson.getCbSettings().getCb()) {
            int cbId = cbData.getCbId();
            Line line = lineMap.get(cbData.getLineId());
            Bus bus = busMap.get(cbData.getBusId());

            SelectivityType selType = SelectivityType.getTypeFromId(cbData.getSelType());

            int m = cbData.getM();
            int i1 = cbData.getI1();
            int t1 = cbData.getT1();

            Integer i2 = cbData.getI2();
            Integer t2 = cbData.getT2();
            Boolean i2Flag = cbData.getI2Flag() != null ? cbData.getI2Flag() == 1 : null;
            Integer i3 = (cbData.getI3() != null && !cbData.getI3().equalsIgnoreCase("Nan")) ?
                    Integer.parseInt(cbData.getI3()) : null;
            Boolean i3Flag = cbData.getI3Flag() != null ? cbData.getI3Flag() == 1 : null;

            Integer i7 = cbData.getI7();
            Integer t7FW = cbData.getT7FW();
            Integer t7BW = cbData.getT7BW();
            Boolean tselFW = cbData.getTselFW() != null ? cbData.getTselFW() == 1 : null;
            Boolean tselBW = cbData.getTselBW() != null ? cbData.getTselBW() == 1 : null;

            CircuitBreaker cb = new CircuitBreaker(
                    cbId, line, bus, selType, m, i1, t1, i2, t2, i2Flag, i3, i3Flag,
                    i7, t7FW, t7BW, tselFW, tselBW, new ArrayList<>()
            );

            cbMap.put(cbId, cb);
            line.setCircuitBreaker(cb);
        }

        if (networkJson.getCbSettings().getEkipLink() != null) {
            for (EkipLinkData ekipLinkData : networkJson.getCbSettings().getEkipLink()) {
                int cbId = ekipLinkData.getCbId();
                CircuitBreaker cb = cbMap.get(cbId);

                if (cb != null && cb.getSelType() == SelectivityType.DIRECTIONAL_ZONE &&
                        ekipLinkData.getActors() != null) {

                    List<Actor> actors = new ArrayList<>();

                    for (ActorData actorData : ekipLinkData.getActors()) {
                        int actorCbId = actorData.getActorCb();
                        int actorCbDir = actorData.getActorCbDir();
                        int cbDir = actorData.getCbDir();

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
