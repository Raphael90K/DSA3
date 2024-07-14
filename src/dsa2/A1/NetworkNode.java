package dsa2.A1;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Network;
import org.oxoo2a.sim4da.NetworkConnection;
import org.oxoo2a.sim4da.Node;

import java.util.List;
import java.util.Random;

public class NetworkNode extends Node {

    static Random rng = new Random(0);


    private double possibilityToSend;
    private boolean isActive;
    private boolean netHasObserver;
    private List<NetworkConnection> connections;
    private final NetworkConnection observer;

    public NetworkNode(String name, NetworkConnection observer) {
        super(name);
        this.possibilityToSend = rng.nextDouble();
        this.isActive = true;
        this.netHasObserver = true;
        this.observer = observer;
    }

    public NetworkNode(String name) {
        super(name);
        this.possibilityToSend = rng.nextDouble();
        this.isActive = true;
        this.netHasObserver = false;
        this.observer = null;
    }

    public void setConnections(List<NetworkConnection> connections) {
        this.connections = connections;
    }

    public void sendMessages() {
        Message message = new Message().add("Firework", 0);

        if (isActive) {
            if (possibilityToSend > rng.nextDouble()) {
                for (NetworkConnection connection : connections) {
                    if (possibilityToSend > rng.nextDouble()) {
                        this.sendBlindly(message, connection.NodeName());
                    }
                }
                this.possibilityToSend /= 2;
            } else {
                this.isActive = false;
            }
        }
    }

    @Override
    protected void engage() {
        Message m;
        while (true) {
            m = this.receive();
            if (this.netHasObserver && m.getHeader().containsKey("status")) {
                this.sendBlindly(new Message().addHeader("status", this.isActive ? 1 : 0), this.observer.NodeName());
            } else if (m.getPayload().containsKey("Firework")) {
                System.out.printf("Node %s Firework received from %s\n", this.NodeName(), m.queryHeader("sender"));
                this.isActive = true;
            }
            this.sendMessages();
        }
    }
}
