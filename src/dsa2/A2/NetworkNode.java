package dsa2.A2;

import dsa2.A3.ObserverNode;
import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;
import org.oxoo2a.sim4da.UnknownNodeException;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkNode extends Node {

    static Random rng = new Random();


    private double possibilityToSend;
    private AtomicBoolean isActive = new AtomicBoolean(true);
    private List<String> connections;
    private final boolean netHasObserver;
    private String observerName = "";

    public NetworkNode(int id) {
        super("Node" + id);
        this.possibilityToSend = rng.nextDouble();
        this.isActive.set(true);
        this.netHasObserver = false;
        System.out.printf("%s with rnd %.2f instantiated.\n", this.NodeName(), possibilityToSend);
    }

    public void setObserver(String observerName) {
        this.observerName = observerName;
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
    }

    public void sendMessages() {
        Message message = new Message().addHeader("Firework", 1);
        if (this.isActive.get()) {
            if (this.possibilityToSend > rng.nextDouble()) {
                for (String connection : connections) {
                    if (!connection.equals(this.NodeName()) && this.possibilityToSend > rng.nextDouble()) {
                        try {
                            this.send(message, connection);
                        } catch (UnknownNodeException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                this.possibilityToSend /= 2;
            } else {
                if (!this.observerName.isEmpty()) {
                    System.out.printf("Node %s currently deactivated.\n", this.NodeName());
                }
                this.isActive.set(false);
                this.sendBlindly(new Message().addHeader("status", "false"), this.observerName);
            }
            sleep(1000);
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
                    this.isActive.set(true);
                    if (!this.observerName.isEmpty()) {
                        this.sendBlindly(new Message().addHeader("status", "true"), this.observerName);
                    }
                    System.out.printf("Node %s activated again.\n", this.NodeName());
                }
            }
        }
    }
}
