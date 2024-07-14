package dsa2.A3;

import dsa2.A2.NetworkNode;
import org.oxoo2a.sim4da.Node;
import org.oxoo2a.sim4da.Simulator;

import java.util.LinkedList;

public class MainA3 {

    public static void main(String[] args) {

        Simulator simulator = Simulator.getInstance();
        int nNodes = 10;
        LinkedList<Node> nodes = new LinkedList<Node>();
        LinkedList<String> nodeNames = new LinkedList<>();
        ObserverNode observerNode = new ObserverNode();
        for (int i = 0; i < nNodes; i++) {
            NetworkNode node = new NetworkNode(i);
            node.setObserver(observerNode.getNodeName());
            nodeNames.add("Node" + i);
            nodes.add(node);
        }
        for (Node node : nodes) {
            ((NetworkNode) node).setConnections(nodeNames);
        }
        observerNode.setConnections(nodeNames);
        simulator.simulate(20);
        simulator.shutdown();
    }

}
