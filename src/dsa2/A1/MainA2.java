package dsa2.A1;

import org.oxoo2a.sim4da.Node;
import org.oxoo2a.sim4da.Simulator;

import java.util.LinkedList;

public class MainA2 {

    public static void main(String[] args) {

        Simulator simulator = Simulator.getInstance();
        int nNodes = 10;
        LinkedList<Node> nodes = new LinkedList<Node>();
        LinkedList<String> nodeNames = new LinkedList<>();
        for (int i = 0; i < nNodes; i++) {
            NetworkNode node = new NetworkNode(i);
            nodeNames.add("Node"+i);
            nodes.add(node);
        }
        for (Node node : nodes) {
            ((NetworkNode) node).setConnections(nodeNames);
        }
        simulator.simulate(5);
        simulator.shutdown();

    }

}
