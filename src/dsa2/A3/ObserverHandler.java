package dsa2.A3;

public class ObserverHandler extends Thread {

    private final ObserverNode node;

    public ObserverHandler(ObserverNode node) {
        this.node = node;
        this.setDaemon(true);
        this.setName("ObserverHandler");
    }

    @Override
    public void run() {
        node.resetReceiveCount();
        while (!Thread.interrupted()) {
            try {
                node.checkIncomingMessages();
                if (node.getReceiveCount() == node.getConnectionCount()) {
                    System.out.printf("%d, %d", node.getReceiveCount(), node.getConnectionCount());
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
