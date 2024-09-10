package dsa3.A2;

import org.oxoo2a.sim4da.Message;

public class Paxos {
    private int highestProposalNumber;
    private int networkSize;
    private int nodeId;
    private int acceptedPropose;
    private NetworkNode node;

    public Paxos(NetworkNode node, int nodeId, int networkSize) {
        this.highestProposalNumber = 0;
        this.acceptedPropose = -1;
        this.networkSize = networkSize;
        this.nodeId = nodeId;
        this.node = node;
    }

    private void propose(int balance, int difference) {
        this.highestProposalNumber = (this.highestProposalNumber / this.networkSize) * this.networkSize + this.nodeId;
        Message msg = new Message();
        msg.addHeader("type", "PROPOSE");
        msg.add("difference", difference);
        msg.add("balance", balance + difference);
        this.node.sendAll(msg);
    }

    private void prepare(){
        this.highestProposalNumber = (this.highestProposalNumber / this.networkSize) * this.networkSize + this.nodeId;
        Message msg = new Message();
        msg.addHeader("type", "PREPARE");
        msg.add("ID", this.highestProposalNumber);
        this.node.sendAll(msg);
    }

    private Message handlePrepare(Message msg) {
        Integer id = msg.queryInteger("ID");
        msg = new Message();
        if (id >= this.highestProposalNumber) {
            this.highestProposalNumber = id;
            msg.addHeader("type", "PROMISE");
            msg.add("ID", this.highestProposalNumber);
        } else {
            msg = null;
        }
        return msg;
    }


    
    public void handle(Message message) {
        Command c = Command.valueOf(message.queryHeader("type"));
        switch (c) {
            case Command.PREPARE:
                break;
            case Command.PROMISE:
                // Handle PROMISE messages (simplified)
                break;
            case Command.PROPOSE:
                break;
            case Command.ACCEPT:
                // handle ACCEPT
                break;
            case Command.LEARN:
                // learn
                break;
            default:
                throw new IllegalArgumentException("Unknown message type: " + c);
        }
    }

}

enum Command {
    PREPARE,
    PROMISE,
    PROPOSE,
    ACCEPT,
    LEARN
}
