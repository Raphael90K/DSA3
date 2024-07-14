package dsa2.A2;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;
import org.oxoo2a.sim4da.UnknownNodeException;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkNode extends Node {

    static Random rng = new Random(0);

    private final double initialPosToSend;
    private final AtomicBoolean isActive = new AtomicBoolean(true);
    private List<String> connections;
    private String observerName = "";
    private double actualPosToSend;

    public NetworkNode(int id) {
        super("Node" + id);
        this.initialPosToSend = rng.nextDouble();
        this.actualPosToSend = this.initialPosToSend;
        this.isActive.set(true);
        System.out.printf("%s with rnd %.2f instantiated.\n", this.NodeName(), initialPosToSend);
    }

    public void setObserver(String observerName) {
        this.observerName = observerName;
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
    }

    private void activate() {
        this.isActive.set(true);
        this.actualPosToSend = this.initialPosToSend;
        this.sendBlindly(new Message().addHeader("status", "true"), this.observerName);
        System.out.printf("Node %s activated again.\n", this.NodeName());
    }

    private void deactivate() {
        this.isActive.set(false);
        this.sendBlindly(new Message().addHeader("status", "false"), this.observerName);
        System.out.printf("Node %s currently deactivated.\n", this.NodeName());
    }

    private void addressNodes(Message message) {
        for (String connection : connections) {
            if (!connection.equals(this.NodeName()) && this.initialPosToSend > rng.nextDouble()) {
                try {
                    this.send(message, connection);
                } catch (UnknownNodeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initSending() {
        this.sleep(rng.nextInt(0, 1000));
        Message message = new Message().addHeader("Firework", 1);
        if (this.actualPosToSend > rng.nextDouble()) {
            addressNodes(message);
        }
        this.deactivate();
    }

    public void sendMessages() {
        Message message = new Message().addHeader("Firework", 1);
        if (this.isActive.get()) {
            if (this.actualPosToSend > rng.nextDouble()) {
                this.addressNodes(message);
                this.actualPosToSend /= 2;
            } else {
                this.deactivate();
            }
            sleep(500);
        }
    }

    @Override
    protected void engage() {
        Message m;
        SendHandler sendHandler = new SendHandler(this, this.NodeName());
        sendHandler.start();
        while (true) {
            m = this.receive();
            if (m.getHeader().containsKey("Firework")) {
                System.out.printf("Node %s Firework received from %s\n", this.NodeName(), m.queryHeader("sender"));
                if (!this.isActive.get()) {
                    this.activate();
                }
            }
        }
    }
}
