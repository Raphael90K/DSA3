package dsa3.A2;

import org.oxoo2a.sim4da.Node;

import java.util.List;

public class NetworkNode extends Node {

    private final int id;
    private int count;
    private List<String> connections;
    private double majority;

    public NetworkNode(int id) {
        super("Node" + id);
        this.id = id;
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
        this.majority = ((double) connections.size()) / 2;
    }
}
