package dsa3.A2;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;

import java.util.List;
import java.util.Random;

public class Client extends Node {
    static Random rand = new Random();

    private final int id;
    private List<String> connections;
    private Random rng;

    public Client(int id) {
        super("Client" + id);
        this.id = id;
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
    }

    @Override
    public void engage() {
        try {
            Thread.sleep(rng.nextInt(0, 1000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Message msg = new Message();
        msg.addHeader("type", "CHANGE");
        this.sendBlindly(msg, this.connections.get(rng.nextInt()));
    }
}
