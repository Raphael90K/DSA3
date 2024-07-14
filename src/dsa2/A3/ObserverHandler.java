package dsa2.A3;

import dsa2.A2.NetworkNode;

public class ObserverHandler extends Thread {

    private final ObserverNode node;

    public ObserverHandler(ObserverNode node) {
        this.node = node;
        this.setDaemon(true);
        this.setName("ObserverHandler");
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            node.checkMessages();
        }
    }

}
