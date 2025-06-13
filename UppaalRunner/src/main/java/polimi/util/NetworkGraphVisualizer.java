package polimi.util;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;
import polimi.model.*;

import java.util.*;

public class NetworkGraphVisualizer {

    public static void visualize(Network network) throws InterruptedException {
        System.setProperty("org.graphstream.ui", "swing");

        Graph graph = new SingleGraph("Power Network");
        graph.setAttribute("ui.stylesheet",
                "node { size: 25px; text-size: 32px; fill-color: lightblue; text-alignment: above;}" +
                        "node.source { fill-color: orange; text-alignment: above;}" +
                        "node.cb { shape: box; fill-color: red; size: 10px, 10px; text-size: 22px; text-alignment: above;}" +
                        "edge { arrow-shape: arrow; text-size: 26px; text-alignment: above;}" +
                        "edge.noArrow { arrow-shape: none; }"
        );

        Bus sourceBus = network.getSources().get(0).getSourceBus();

        Node sourceNode = graph.addNode(sourceBus.getId() + "");
        sourceNode.setAttribute("ui.label", "Bus " + sourceBus.getId() + " (source)");
        sourceNode.setAttribute("ui.class", "source");

        Set<Integer> visited = new HashSet<>();
        Queue<Bus> queue = new LinkedList<>();
        queue.add(sourceBus);
        visited.add(sourceBus.getId());

        while (!queue.isEmpty()) {
            Bus current = queue.poll();

            for (Line line : current.getOutLines()) {
                Bus to = line.getToBus();

                if (graph.getNode(to.getId() + "") == null) {
                    Node node = graph.addNode(to.getId() + "");
                    node.setAttribute("ui.label", "Bus " + to.getId());
                }

                CircuitBreaker cb = line.getCircuitBreaker();
                String cbId = "CB" + cb.getId();

                if (graph.getNode(cbId) == null) {
                    Node cbNode = graph.addNode(cbId);
                    cbNode.setAttribute("ui.label", "CB " + cb.getId());
                    cbNode.setAttribute("ui.class", "cb");
                }

                String edge1Id = "L" + line.getId() + "_a";
                String edge2Id = "L" + line.getId() + "_b";

                if (graph.getEdge(edge1Id) == null) {
                    Edge edge1 = graph.addEdge(edge1Id, current.getId() + "", cbId, true);
                    edge1.setAttribute("ui.label", "\nL " + line.getId());
                    edge1.setAttribute("ui.class", "noArrow");
                }
                if (graph.getEdge(edge2Id) == null) {
                    Edge edge2 = graph.addEdge(edge2Id, cbId, to.getId() + "", true);
                }

                if (visited.add(to.getId())) {
                    queue.add(to);
                }
            }
        }

        Viewer viewer = graph.display();
        viewer.enableAutoLayout(new SpringBox(false));
    }
}

