package dsa3.A2.Bank;

public class Transaction {
    private String consensusID;
    private String type; // z.B. "Deposit" oder "Withdrawal"
    private String change;
    private double value;

    public Transaction(String consensId, String type, String change, double value) {
        this.consensusID = consensId;
        this.type = type;
        this.change = change;
        this.value = value;
    }

    public String getConsensusID() {
        return consensusID;
    }

    public void setConsensusID(String consensusID) {
        this.consensusID = consensusID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
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
        return "Transaction{consensId='" + consensusID + "', type='" + type + "', change='" + change + "', value=" + value + '}';
    }
}
