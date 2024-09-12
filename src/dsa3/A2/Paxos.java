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
        this.highestProposalNumber = ((this.highestProposalNumber + 1) / this.networkSize) * this.networkSize + this.nodeId;
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

    public void handlePropose(Message msg) {
        if (msg.queryInteger("ID") >= this.highestProposalNumber) {
            this.highestProposalNumber = msg.queryInteger("ID");
            this.acceptedPropose = msg.queryInteger("balance");

            Message acceptMessage = new Message();
            acceptMessage.addHeader("type", "ACCEPT");
            acceptMessage.add("ID", this.highestProposalNumber);
            acceptMessage.add("balance", this.acceptedPropose);
            this.node.sendAll(acceptMessage);
        }
    }

    private Message handleAccept(Message msg) {
        int proposalId = msg.queryInteger("ID");
        int proposedValue = msg.queryInteger("balance");
        Message response = new Message();

        if (proposalId >= this.highestProposalNumber) {
            // Update accepted proposal and highest proposal number
            this.highestProposalNumber = proposalId;
            this.acceptedPropose = proposedValue;

            // Send ACCEPT message as confirmation
            response.addHeader("type", "ACCEPT");
            response.add("ID", this.highestProposalNumber);
            response.add("balance", this.acceptedPropose);
        } else {
            // If the proposal ID is less than the promised proposal, reject the accept
            response = null; // Could also send a REJECT message if desired
        }

        return response;
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
