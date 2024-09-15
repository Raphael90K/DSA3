package dsa3.A2;

import org.oxoo2a.sim4da.Simulator;

import java.util.Arrays;
import java.util.List;

public class MainA2 {

    public static void main(String[] args) {
        Simulator simulator = Simulator.getInstance();
        NetworkNode n1 = new NetworkNode(0);
        NetworkNode n2 = new NetworkNode(1);
        NetworkNode n3 = new NetworkNode(2);
        List<String> nodes = Arrays.asList(n1.getName(), n2.getName(), n3.getName());

        n1.setConnections(nodes);
        n2.setConnections(nodes);
        n3.setConnections(nodes);

        Client client = new Client("client");
        client.setBankNodes(nodes);
        simulator.simulate(3);
        simulator.shutdown();
    }


}
