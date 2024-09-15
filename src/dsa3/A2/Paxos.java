package dsa3.A2;

import dsa3.A2.Bank.Bank;
import dsa3.A2.Bank.Transaction;
import org.oxoo2a.sim4da.Message;

import java.io.File;
import java.util.ArrayList;

public class Paxos {
    private int highestProposalNumber;
    private int networkSize;
    private int nodeId;
    private int acceptedPrepare;
    private NetworkNode node;
    private Bank bankAccount;

    public Paxos(NetworkNode node, int nodeId, int networkSize) {
        this.highestProposalNumber = 0;
        this.acceptedPrepare = -1;
        this.networkSize = networkSize;
        this.nodeId = nodeId;
        this.node = node;
    }

    private void prepare() {
        this.highestProposalNumber = (this.highestProposalNumber / this.networkSize) * this.networkSize + this.nodeId;
        Message msg = new Message();
        msg.addHeader("type", "PREPARE");
        msg.add("ID", this.highestProposalNumber);
        this.node.sendAll(msg);
    }

    private void handlePrepare(Message msg) {
        Integer id = msg.queryInteger("ID");
        msg = new Message();
        if (id > this.highestProposalNumber) {
            this.highestProposalNumber = id;
            this.acceptedPrepare = id;
            msg.addHeader("type", "PROMISE");
            msg.add("ID", this.highestProposalNumber);
            this.node.sendOne(msg, msg.queryHeader("sender"));
        }
    }

    // TODO: write handle Promise
    private void handlePromise(Message msg) {

    }

    private void propose(Transaction transaction) {
        Message msg = new Message();
        msg.addHeader("type", "PROPOSE");
        msg.add("ID", this.highestProposalNumber);
        msg.add("transaction", transaction.toString());
        this.node.sendAll(msg);
    }

    public void handlePropose(Message msg) {
        if (msg.queryInteger("ID") == this.acceptedPrepare) {
            Transaction newTx = Transaction.fromString(msg.query("transaction"));
            if (this.node.checkTx(newTx)) {
                Message accept = new Message();
                accept.addHeader("type", "ACCEPT");
                accept.add("accept", "true");
                this.node.sendOne(accept, msg.queryHeader("sender"));
            }
        }
    }

    // TODO: write handle Accept
    private void handleAccept(Message msg) {

    }

    public void handle(Message message) {
        Command c = Command.valueOf(message.queryHeader("type"));
        switch (c) {
            case Command.CHANGE:
                System.out.println("handle change");
                break;
            case Command.PREPARE:
                System.out.println("handle prepare");
                break;
            case Command.PROMISE:
                System.out.println("handle promise");
                break;
            case Command.PROPOSE:
                System.out.println("handle propose");
                break;
            case Command.ACCEPT:
                System.out.println("handle accept");
                // handle ACCEPT
                break;
            case Command.LEARN:
                System.out.println("handle learn");
                // learn
                break;
            default:
                throw new IllegalArgumentException("Unknown message type: " + c);
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
