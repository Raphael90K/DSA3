package dsa3.A2;

import dsa3.A2.Bank.Bank;
import dsa3.A2.Bank.Transaction;
import org.oxoo2a.sim4da.Message;

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

    public void setNetworkSize(int size) {
        this.networkSize = size;
    }

    public int getProposalNumber() {
        this.highestProposalNumber = ((this.highestProposalNumber / this.networkSize) + 1) * this.networkSize + this.nodeId;
        return this.highestProposalNumber;
    }

    public void prepare(int proposalNumber) {
        Message msg = new Message();
        msg.addHeader("type", "PREPARE");
        msg.add("ID", proposalNumber);
        this.node.sendAll(msg);
    }

    public void handlePrepare(Message msg) {
        Integer id = msg.queryInteger("ID");
        Message newMsg = new Message();
        if (id >= this.highestProposalNumber) {
            this.highestProposalNumber = id;
            this.acceptedPrepare = id;
            newMsg.addHeader("type", "PROMISE");
            newMsg.add("ID", this.highestProposalNumber);
            this.node.sendOne(newMsg, msg.queryHeader("sender"));
        }
    }

    // TODO: write handle Promise
    public void handlePromise(Message msg) {

    }

    public void propose(Transaction transaction, int proposalNumber) {
        Message msg = new Message();
        msg.addHeader("type", "PROPOSE");
        msg.add("ID", proposalNumber);
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
    public void handleAccept(Message msg) {

    }

    public void learn(Transaction transaction) {
        String newTx = transaction.toString();
        Message msg = new Message();
        msg.addHeader("type", "LEARN");
        msg.add("ID", this.highestProposalNumber);
        msg.add("transaction", newTx);
        this.node.sendAll(msg);
    }

    public void handleLearn(Message msg) {
        Transaction newTx = Transaction.fromString(msg.query("transaction"));
        if (this.node.checkTx(newTx)) {
            this.node.addTransaction(newTx);
        }
    }


}