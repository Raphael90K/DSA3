package dsa3.A2;

import dsa3.A2.BankNode.BankNode;
import org.oxoo2a.sim4da.Simulator;

import java.util.Arrays;
import java.util.List;

public class MainA2 {

    public static void main(String[] args) {
        Simulator simulator = Simulator.getInstance();
        BankNode n1 = new BankNode(0);
        BankNode n2 = new BankNode(1);
        BankNode n3 = new BankNode(2);
        BankNode n4 = new BankNode(3);
        BankNode n5= new BankNode(4);
        List<String> nodes = Arrays.asList(n1.getName(), n2.getName(), n3.getName(), n4.getName(), n5.getName());

        n1.setConnections(nodes);
        n2.setConnections(nodes);
        n3.setConnections(nodes);
        n4.setConnections(nodes);
        n5.setConnections(nodes);

        Client client = new Client("client");
        client.setBankNodes(nodes);
        simulator.simulate(30);
        simulator.shutdown();
    }

}
