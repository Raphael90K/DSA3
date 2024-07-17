package dsa2.A3;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;

public class ObserverNode extends Node {

    static ObserverNode observerNode = null;

    private final HashMap<String, Boolean> connections = new HashMap<>();
    int activeNodes = 0;
    int sentCount = 0;
    int receiveInactiveCount = 0;
    int roundsSilentNodes = 0;
    private int receiveCount = 0;

    private ObserverNode() {
        super("ObserverNode");
        System.out.printf("%s instantiated.\n", this.NodeName());
    }

    public static ObserverNode getInstance() {
        if (observerNode == null) {
            observerNode = new ObserverNode();
            return observerNode;
        } else {
            return observerNode;
        }
    }

    public String getNodeName() {
        return this.NodeName();
    }

    public void setConnections(List<String> connections) {
        for (String connection : connections) {
            this.connections.put(connection, true);
        }
        this.activeNodes = connections.size();
    }

    public int getConnectionCount() {
        return this.connections.keySet().size();
    }

    public int getReceiveCount(){
        return this.receiveCount;
    }

    public void resetReceiveCount() {
        this.receiveCount = 0;
    }

    public int getReceiveInactiveCount() {
        return this.receiveInactiveCount;
    }

    public void resetReceiveInactiveCount() {
        this.receiveInactiveCount = 0;
    }

    public void sendMessage() {
        Message m = new Message().addHeader("status", 0);
        this.sentCount = 0;
        for (String c : connections.keySet()) {
            this.sendBlindly(m, c);
            this.sentCount++;
        }
    }

    public void checkIncomingMessages() {
        Message m = this.receive();
        if (m.getHeader().containsKey("status")) {
            String sender = m.queryHeader("sender");
            Boolean status = m.queryHeader("status").equals("1");
            System.out.printf("Status %b received from Node %s\n", status, m.queryHeader("sender"));
            this.connections.put(sender, status);
            System.out.println(status);
            if (!status){
                this.receiveInactiveCount++;
            }
            this.receiveCount++;
        }
    }

    private void logTermination() {
        Logger logger = this.getLogger();
        logger.debug("Network terminal state reached.");
        System.out.println("Network terminated");
    }

    @Override
    protected void engage() {
        Message m;

        while (true) {
            this.sendMessage();
            Thread observe = new ObserverHandler(this);
            observe.start();
            this.sleep(3000);
            observe.interrupt();
            System.out.printf("%d:%d\n", sentCount, receiveInactiveCount);
            if (this.sentCount == this.receiveInactiveCount) {
                this.roundsSilentNodes++;
            } else {
                this.roundsSilentNodes = 0;
            }
            if (this.roundsSilentNodes >= 2){
                this.logTermination();
                break;
            }
        }
        System.exit(0);
    }

}
