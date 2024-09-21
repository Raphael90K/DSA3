package dsa3.A2.BankNode;

import dsa3.A2.Bank.Bank;
import dsa3.A2.Bank.Transaction;
import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse repräsentiert einen Knoten im dezentralen Banking System. Er nutzt die Bank Klasse, um den aktuellen Kontostand
 * und die Paxos Klasse um den Konsens herbeizuführen.
 *
 */
public class BankNode extends Node {

    private final int id;
    private List<String> connections = new ArrayList<>();
    private double majority;
    private Bank bank;
    private Paxos paxos;
    private int answers;

    public BankNode(int id) {
        super("Node" + id);
        this.id = id;
        this.bank = Bank.fromFile(this.getName());
        this.paxos = new Paxos(this, this.id, this.connections.size());
    }

    /**
     * Die Namen der Knoten im Netzwerk werden als Liste mit dieser Methode gesetzt.
     *
     * @param connections
     */
    public void setConnections(List<String> connections) {
        this.connections = connections;
        this.majority = ((double) connections.size()) / 2;
        this.paxos.setNetworkSize(connections.size());
    }

    public List<String> getConnections() {
        return connections;
    }

    /**
     * Methode zum Versenden von Nachrichten an alle anderen Bankknoten im Netz.
     *
     * @param msg
     */
    public void sendAll(Message msg) {
        for (String node : this.connections) {
            if (!node.equals(this.getName())) {
                sendBlindly(msg, node);
            }
        }
    }

    /**
     * Sendet eine Nachricht an einen Zielknoten.
     *
     * @param msg
     * @param node
     */
    public void sendOne(Message msg, String node) {
        sendBlindly(msg, node);
    }

    public String getName() {
        return this.NodeName();
    }

    /**
     * Prüft ob es sich bei der Transaktion um eine gültige Transaktion handelt.
     * @param transaction
     * @return
     */
    public boolean checkTx(Transaction transaction) {
        return this.bank.checkValidTransaction(transaction);
    }

    /**
     * Fügt eine Transaktion der Bank zu.
     *
     * @param newTx
     */
    public void addTransaction(Transaction newTx) {
        this.bank.addTransaction(newTx);
    }

    /**
     * Engage Methode der Simulation. So lange die Simutation läuft, werden Nachrichten gelesen und mit der Methode
     * handel(msg) behandelt.
     *
     */
    public void engage() {
        while (true) {
            Message msg = null;
            try {
                msg = this.receive();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.handle(msg);
        }
    }

    /**
     * Hauptmethode der Klasse. Die empfangen Nachrichten werden nach Typen getrennt behandelt.
     * Bei Eingang einer CHANGE Nachricht von einem Klienten wird der Paxos Algorithmus initialisiert.
     *
     * @param message
     */
    public void handle(Message message) {
        Command c = Command.valueOf(message.queryHeader("type"));
        switch (c) {
            case Command.CHANGE:
                // If a Node gets the CHANGE message it initiates the Paxos Protocol
                double amount = Double.parseDouble(message.query("amount"));
                this.getLogger().debug("{} handle CHANGE {}", this.getName(), amount);
                int proposalNumber = paxos.getProposalNumber();
                String sender = message.queryHeader("sender");

                // prepare a possible transaction send prepare and handle promises
                Transaction newTx = this.bank.newTransaction(proposalNumber, amount);
                this.paxos.prepare(proposalNumber);
                countResponses(Command.PROMISE, proposalNumber);
                if (consensusFailed(sender)) break;

                // send PROPOSE and handle the ACCEPT responses
                this.paxos.propose(newTx, proposalNumber);
                this.countResponses(Command.ACCEPT, proposalNumber);
                if (consensusFailed(sender)) break;

                // add newTx to Transactions save on Hard Disk and propagate in the network
                this.addTransaction(newTx);
                this.bank.saveAccount(this.getName());
                this.paxos.learn(newTx);
                this.sendSuccess(sender, true);
                break;

            case Command.PREPARE:
                // handles the PREPARE message
                this.logAndPrintMessage(message, Command.PREPARE);
                this.paxos.handlePrepare(message);
                break;

            case Command.PROPOSE:
                // handles the PROPOSE message
                this.logAndPrintMessage(message, Command.PROPOSE);
                this.paxos.handlePropose(message);
                break;

            case Command.LEARN:
                // handles the LEARN message and Save the current
                this.logAndPrintMessage(message, Command.LEARN);
                this.paxos.handleLearn(message);
                this.bank.saveAccount(this.getName());
                break;

            default:
                this.getLogger().debug("unknown message {} received", message.queryHeader("type"));
        }
    }

    /**
     * Methode zum Logging der Nachrichten
     *
     * @param message
     * @param command
     */
    private void logAndPrintMessage(Message message, Command command) {
        System.out.println("%s handle %s".formatted(this.getName(), command));
        this.getLogger().debug("{} from {} with id {}", command, message.queryHeader("sender"), message.query("ID"));
    }

    /**
     * Prüft nach dem Eingang von Promise- und Accept Nachrichten ob der Konsensus fehlgeschlagen ist.
     *
     * @param sender
     * @return
     */
    private boolean consensusFailed(String sender) {
        if (this.answers + 1 <= this.majority) {
            this.logFailure();
            this.sendSuccess(sender, false);
            return true;
        }
        return false;
    }

    /**
     * Resets the counter for messages and counts the incoming messages.
     *
     * @param command
     */
    private void countResponses(Command command, int consensusId) {
        this.answers = 0;
        BankResponseCounter nc = new BankResponseCounter(this, command, consensusId);
        nc.start();
        try {
            Thread.sleep(500);
            nc.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode zum Versenden des Konsensergebnisses an den Client.
     *
     * @param sender
     * @param success
     */
    private void sendSuccess(String sender, boolean success) {
        Message msg = new Message();
        msg.addHeader("status", success ? "SUCCESS" : "FAILURE");
        this.sendBlindly(msg, sender);
    }

    /**
     * Loggt den Fehlschlag.
     *
     */
    private void logFailure() {
        this.getLogger().debug("No consensus found.");
    }

    /**
     * Methode zum Zählen der eingehenden Nachrichten in einem gesonderten Thread.
     *
     * @param command
     * @param consesusId
     */
    public synchronized void countMessages(Command command, int consesusId) {
        Message msg = null;
        try {
            msg = this.receive();
            Command type = Command.valueOf(msg.queryHeader("type"));
            int id = msg.queryInteger("ID");
            if (command == type && id == consesusId) {
                logAndPrintMessage(msg, command);
                this.answers++;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}


