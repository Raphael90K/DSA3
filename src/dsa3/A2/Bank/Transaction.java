package dsa3.A2.Bank;

public class Transaction {
    private int consensusID;
    private String type; // z.B. "Deposit" oder "Withdrawal"
    private double change;
    private double value;

    public Transaction() {}

    public Transaction(int consensId, String type, double change, double value) {
        this.consensusID = consensId;
        this.type = type;
        this.change = change;
        this.value = value;
    }

    public int getConsensusID() {
        return consensusID;
    }

    public void setConsensusID(int consensusID) {
        this.consensusID = consensusID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

}
