package dsa2.A3;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;

public class ObserverNode extends Node {

    private final HashMap<String, Boolean> connections = new HashMap<>();
    int activeNodes = 0;

    public ObserverNode() {
        super("ObserverNode");
        System.out.printf("%s instantiated.\n", this.NodeName());
    }

    public String getNodeName(){
        return this.NodeName();
    }

    public void setConnections(List<String> connections) {
        for (String connection : connections) {
            this.connections.put(connection, true);
        }
        this.activeNodes = connections.size();
    }

    public void checkMessages() {
        Message m = this.receive();
        if (m.getHeader().containsKey("status")) {
            String sender = m.queryHeader("sender");
            Boolean status = Boolean.parseBoolean(m.queryHeader("status"));
            System.out.printf("Status %b received from Node %s\n", status, m.queryHeader("sender"));
            this.activeNodes += status ? 1 : -1;
            this.connections.put(sender, status);
        }
    }

    private void logTermination(){
        Logger logger = this.getLogger();
        logger.debug("Network terminal state reached.");
        System.out.println("Network terminated");
    }

    @Override
    protected void engage() {
        Message m;
        Thread handleMessages = new ObserverHandler(this);
        handleMessages.start();

        while (true) {
            if (this.activeNodes == 0) {
                this.sleep(3000);
                if (this.activeNodes == 0) {
                    this.logTermination();
                    break;
                }
            }
            this.sleep(3000);
        }
    }
}
