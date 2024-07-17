package dsa2.A3;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;

public class ObserverNode extends Node {

    static ObserverNode observerNode = null;

    // speichert die Verbindungen
    private final HashMap<String, Boolean> connections = new HashMap<>();

    // zählt in jeder Iteration die gesendeten Nachrichten
    private int sentCount = 0;
    // zählt in jeder Iteration die empfangenen Nachrichten
    private int receiveCount = 0;
    // zählt in jeder Iteration die empfangenen Nachrichten mit Status 0 / false
    private int receiveInactiveCount = 0;
    // zählt die Runden in denen alle Knoten inaktiv waren.
    private int roundsAllInactive = 0;


    /**
     * Obersever Knoten, implementiert nach dem Doppelzählverfahren aus der Vorlesung.
     */
    private ObserverNode() {
        super("ObserverNode");
        System.out.printf("#%s#: instantiated.\n", this.NodeName());
    }

    /**
     * Gibt den die Singleton Instanz des Observerknotens zurück.
     *
     * @return
     */
    public static ObserverNode getInstance() {
        if (observerNode == null) {
            observerNode = new ObserverNode();
            return observerNode;
        } else {
            return observerNode;
        }
    }

    /**
     * gibt den Namen des Knoten zurück.
     *
     * @return
     */
    public String getNodeName() {
        return this.NodeName();
    }

    public void setConnections(List<String> connections) {
        for (String connection : connections) {
            this.connections.put(connection, true);
        }
    }

    public int getConnectionCount() {
        return this.connections.keySet().size();
    }

    public int getReceiveCount() {
        return this.receiveCount;
    }

    public void resetReceiveCount() {
        this.receiveCount = 0;
    }

    public void resetReceiveInactiveCount() {
        this.receiveInactiveCount = 0;
    }

    /**
     * Sendet in jeder Iteration die Kontrollnachricht an alle Knoten.
     */
    public void sendMessage() {
        Message m = new Message().addHeader("status", 0);
        this.sentCount = 0;
        for (String c : connections.keySet()) {
            this.sendBlindly(m, c);
            this.sentCount++;
        }
    }

    /**
     * Checkt die eingehenden Nachrichten und zählt die Inaktiven Knoten und Nachrichten insgesamt.
     */
    public void checkIncomingMessages() {
        Message m = this.receive();
        if (m.getHeader().containsKey("status")) {
            String sender = m.queryHeader("sender");
            Boolean status = m.queryHeader("status").equals("1");
            System.out.printf("#Observer#: Status %b received from Node %s\n", status, m.queryHeader("sender"));
            this.connections.put(sender, status);
            if (!status) {
                this.receiveInactiveCount++;
            }
            this.receiveCount++;
        }
    }

    /**
     * Loggt sobald die Terminierung des Netzwerks erkannt wurde.
     */
    private void logTermination() {
        Logger logger = this.getLogger();
        logger.debug("Network terminal state reached.");
        System.out.println("#Observer#: Network terminated");
    }

    /**
     * Hauptfunktion des Knotens. In jeder Iteration wird ein Thread gestartet, der die eingehenden Nachrichten verarbeitet.
     * Nach drei Sekunden wird der Thread beendet, auch wenn nicht alle Knoten geantwortet haben. So sollen
     * verlorene Nachrichten abgefangen werden.
     */
    @Override
    protected void engage() {
        Message m;

        while (true) {
            this.sendMessage();
            Thread observe = new ObserverHandler(this);
            observe.start();
            this.sleep(3000);
            observe.interrupt();
            System.out.printf("#Observer#: %d:%d Knoten inaktiv.", this.receiveInactiveCount, this.sentCount);
            if (this.sentCount == this.receiveInactiveCount) {
                this.roundsAllInactive++;
            } else {
                this.roundsAllInactive = 0;
            }
            if (this.roundsAllInactive >= 2) {
                this.logTermination();
                break;
            }
        }
        System.exit(0);
    }

}
