package dsa3.A2;

import dsa3.A2.Bank.Bank;
import dsa3.A2.Bank.Transaction;
import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;

import java.util.ArrayList;
import java.util.List;

public class NetworkNode extends Node {

    private final int id;
    private List<String> connections = new ArrayList<>();
    private double majority;
    private Bank bank;
    private Paxos paxos;
    private int answers;

    public NetworkNode(int id) {
        super("Node" + id);
        this.id = id;
        this.bank = Bank.fromFile(this.getName());
        this.paxos = new Paxos(this, this.id, this.connections.size());
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
        this.majority = ((double) connections.size()) / 2;
        this.paxos.setNetworkSize(connections.size());
    }

    public List<String> getConnections() {
        return connections;
    }

    public void sendAll(Message msg) {
        for (String node : this.connections) {
            if (!node.equals(this.getName())) {
                sendBlindly(msg, node);
            }
        }
    }

    public String getName() {
        return this.NodeName();
    }

    public void sendOne(Message msg, String node) {
        sendBlindly(msg, node);
    }

    public boolean checkTx(Transaction transaction) {
        return this.bank.checkValidTransaction(transaction);
    }

    public void engage() {
        while (true) {
            Message msg = this.receive();
            this.handle(msg);
        }
    }

    public void addTransaction(Transaction newTx) {
        this.bank.addTransaction(newTx);
    }

    public void handle(Message message) {
        Command c = Command.valueOf(message.queryHeader("type"));
        switch (c) {
            case Command.CHANGE:

                double amount = Double.parseDouble(message.query("amount"));
                this.getLogger().debug("handle change {}", amount);
                int proposalNumber = paxos.getProposalNumber();

                // prepare
                this.paxos.prepare(proposalNumber);
                Transaction newTx = this.bank.newTransaction(proposalNumber, amount);

                // handle promise and send propose
                NetworkNodeCounter nc = new NetworkNodeCounter(this, Command.PROMISE);
                nc.start();
                try {
                    Thread.sleep(500);
                    nc.interrupt();
                } catch (InterruptedException e) {
                }
                if (this.answers < this.majority) {
                    this.logFailure();
                    break;
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // handle accept
                this.paxos.propose(newTx, proposalNumber);
                NetworkNodeCounter ncAccept = new NetworkNodeCounter(this, Command.ACCEPT);
                ncAccept.start();
                try {
                    Thread.sleep(500);
                    ncAccept.interrupt();
                } catch (InterruptedException e) {

                }
                if (this.answers < this.majority) {
                    this.logFailure();
                    break;
                }
                break;
            case Command.PREPARE:
                System.out.println("handle prepare");
                this.getLogger().debug("PREPARE from {} with id {}", message.queryHeader("sender"), message.query("ID"));
                this.paxos.handlePrepare(message);
                break;
            case Command.PROPOSE:
                System.out.println("handle propose");
                this.getLogger().debug("PROPOSE from {} with id {}", message.queryHeader("sender"), message.query("ID"));
                this.paxos.handlePropose(message);
                break;
            case Command.LEARN:
                System.out.println("handle learn");
                // learn
                break;
            default:
                this.getLogger().debug("unknown message {} received", message.queryHeader("type"));
        }
    }

    private void logFailure() {
        this.getLogger().debug("No consensus found.");
    }


    public synchronized void countMessages(Command command) {
        Message msg = this.receive();
        System.out.println(msg);
        if (command == Command.valueOf(msg.queryHeader("type"))) {
            this.getLogger().debug("{} from {} with id {}", command, msg.queryHeader("sender"), msg.query("ID"));
            this.answers++;
        }
    }
}

class NetworkNodeCounter extends Thread {

    private final NetworkNode node;
    private final Command command;

    public NetworkNodeCounter(NetworkNode node, Command command) {
        this.node = node;
        this.command = command;
        this.setDaemon(true);
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println(Thread.currentThread());
            System.out.println(this.command);
            this.node.countMessages(this.command);
        }
    }
}

enum Command {
    CHANGE,
    PREPARE,
    PROMISE,
    PROPOSE,
    ACCEPT,
    LEARN
}


