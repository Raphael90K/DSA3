package dsa3.A2;

import org.oxoo2a.sim4da.Node;

import java.util.List;

public class NetworkNode extends Node {

    private List<String> connections;

    public NetworkNode(int id){
        super("Node"+id);
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
    }
}
