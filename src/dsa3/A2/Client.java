package dsa3.A2;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;

import java.util.List;
import java.util.Random;

public class Client extends Node {
    static Random rand = new Random();

    private final int id;
    private List<String> bankNodes;
    private Random rng;

    public Client(int id) {
        super("Client" + id);
        this.id = id;
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

    public void outputResponse() {
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
        String randomBankNode = bankNodes.get(rand.nextInt(bankNodes.size()));
        this.sendBlindly(msg, randomBankNode);
        Response response = new Response(this);
        response.start();
    }

}

class Response extends Thread {

    private final Client client;

    public Response(Client client) {
        this.client = client;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        client.outputResponse();
    }
}
