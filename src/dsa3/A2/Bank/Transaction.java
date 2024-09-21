package dsa3.A2.Bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Transaktionen die die Kontoänderungen eines Bankobjekts repräsentieren. Werden in einem Bankaccount als Liste gespeichert.
 *
 *
 */
public class Transaction {
    private int txId;
    private int consensusID;
    private double change;
    private double value;

    public Transaction() {
    }

    public Transaction(int id, int consensId, double change, double value) {
        this.txId = id;
        this.consensusID = consensId;
        this.change = change;
        this.value = value;
    }

    public int getTxId() {
        return txId;
    }

    public void setTxId(int txId) {
        this.txId = txId;
    }

    public int getConsensusID() {
        return consensusID;
    }

    public void setConsensusID(int consensusID) {
        this.consensusID = consensusID;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Wandelt einen String in ein Transaction Object um.
     *
     * @param string
     * @return
     */
    public static Transaction fromString(String string) {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(string, Transaction.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
