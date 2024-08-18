package dsa3.A2;

import org.oxoo2a.sim4da.Message;

public class Paxos {
    private int highestProposalNumber;
    private int networkSize;
    private int nodeId;

    public Paxos(int nodeId, int networkSize) {
        this.highestProposalNumber = 0;
        this.networkSize = networkSize;
        this.nodeId = nodeId;

    }

    public void sendPropose(String content) {
        this.highestProposalNumber = (this.highestProposalNumber / this.networkSize) * this.networkSize + this.nodeId;
        // broadcast
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
