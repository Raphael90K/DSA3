package dsa3.A2;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;

import java.util.List;
import java.util.Random;

public class Client extends Node {
    static Random rng = new Random();

    private List<String> bankNodes;

    public Client(String name) {
        super(name);
    }

    public void setBankNodes(List<String> bankNodes) {
        this.bankNodes = bankNodes;
    }

    public Message createChangeBankMsg(double amount) {
        Message msg = new Message();
        msg.addHeader("type", "CHANGE");
        msg.add("amount", Double.toString(amount));
        return msg;
    }

    public void outputResponse() throws InterruptedException {
        Message msg = this.receive();
        System.out.println(msg.queryHeader("status"));
    }

    @Override
    public void engage() {
        try {
            Thread.sleep(rng.nextInt(0, 1000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        double change = rng.nextDouble(-100, 100);
        Message msg = createChangeBankMsg(change);
        String randomBankNode = bankNodes.get(rng.nextInt(bankNodes.size()));
        this.sendBlindly(msg, randomBankNode);
        Message incomingMsg = null;
        try {
            incomingMsg = this.receive();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.getLogger().debug(incomingMsg.queryHeader("status"));
    }

}
