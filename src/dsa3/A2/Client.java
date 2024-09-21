package dsa3.A2;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;

import java.util.List;
import java.util.Random;

/**
 * Repräsentiert einen Clientknoten, der einen zufälligen Bankknoten auswählt und eine Ein- oder Auszahlung in Auftrag gibt.
 *
 */
public class Client extends Node {
    static Random rng = new Random();

    private List<String> bankNodes;

    public Client(String name) {
        super(name);
    }

    /**
     * Setzt alle verfügbaren BankKnoten als Liste.
     *
     * @param bankNodes
     */
    public void setBankNodes(List<String> bankNodes) {
        this.bankNodes = bankNodes;
    }

    /**
     * Erstellt die Nachricht zum Initiieren einer Transaktion.
     *
     * @param amount
     * @return
     */
    public Message createChangeBankMsg(double amount) {
        Message msg = new Message();
        msg.addHeader("type", "CHANGE");
        msg.add("amount", Double.toString(amount));
        return msg;
    }

    /**
     * Engage Methode der Clients. Zunächst wird eine Zufallszeit gewartet. Dann wird eine Transaktion im Bereich von -100
     * - 100 erstellt und an einen Zufälligen Bankknoten im Netwerk gesendet. Im Anschluss wartet der Client auf die
     * Bestätigung des Netzwerks.
     *
     */
    @Override
    public void engage() {
        int count = 0;
        while (true){

            try {
                Thread.sleep(rng.nextInt(0, 500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            double change = rng.nextDouble(-100, 100);
            this.getLogger().debug("Transaction {} initiated: {}", count, change);
            Message msg = createChangeBankMsg(change);
            String randomBankNode = bankNodes.get(rng.nextInt(bankNodes.size()));
            this.sendBlindly(msg, randomBankNode);
            Message incomingMsg = null;
            try {
                incomingMsg = this.receive();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.getLogger().debug("Status of the Transaction {}: {}", count, incomingMsg.queryHeader("status"));
            count++;
        }
    }

}
