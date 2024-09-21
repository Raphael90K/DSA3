package dsa3.A2.BankNode;

/**
 * Klasse zum Zählen derr eingehenden Nachrichten nach einem Prepare und Propose.
 * Sobald der Timeout in BankNode zuschlägt, wird der Thread beendet.
 *
 */
class BankResponseCounter extends Thread {

    private final BankNode node;
    private final Command command;
    private final int consensusId;

    public BankResponseCounter(BankNode node, Command command, int consensusId) {
        this.node = node;
        this.command = command;
        this.consensusId = consensusId;
        this.setDaemon(true);
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            this.node.countMessages(this.command, this.consensusId);
        }
    }
}
