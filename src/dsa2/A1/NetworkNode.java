package dsa2.A1;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.NetworkConnection;
import org.oxoo2a.sim4da.Node;
import org.oxoo2a.sim4da.UnknownNodeException;

import java.util.List;
import java.util.Random;

public class NetworkNode extends Node {

    static Random rng = new Random();


    private double possibilityToSend;
    private boolean isActive;
    private List<String> connections;
    private final boolean netHasObserver;
    private final String observerName;

    public NetworkNode(int id, String observerName) {
        super("Node" + id);
        this.possibilityToSend = rng.nextDouble();
        this.isActive = true;
        this.netHasObserver = true;
        this.observerName = observerName;
        System.out.printf("%s with rnd %.2f instantiated.\n", super.NodeName(), possibilityToSend);
    }

    public NetworkNode(int id) {
        super("Node" + id);
        this.possibilityToSend = rng.nextDouble();
        this.isActive = true;
        this.netHasObserver = false;
        this.observerName = null;
        System.out.printf("%s with rnd %.2f instantiated.\n", super.NodeName(), possibilityToSend);
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
    }

    public void sendMessages() throws UnknownNodeException {
        Message message = new Message().addHeader("Firework", 1);

        if (isActive) {
            if (possibilityToSend > rng.nextDouble()) {
                for (String connection : connections) {
                    if (!connection.equals(this.NodeName()) && possibilityToSend > rng.nextDouble()) {
                        this.send(message, connection);
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
            try{
                this.sendMessages();
            } catch (UnknownNodeException e) {
                e.printStackTrace();
            }
            m = this.receive();
            if (this.netHasObserver && m.getHeader().containsKey("status")) {
                this.sendBlindly(new Message().addHeader("status", this.isActive ? 1 : 0), this.observerName);
            } else if (m.getHeader().containsKey("Firework")) {
                System.out.printf("Node %s Firework received from %s\n", this.NodeName(), m.queryHeader("sender"));
                this.isActive = true;
            }
            sleep(100);
        }
    }
}
