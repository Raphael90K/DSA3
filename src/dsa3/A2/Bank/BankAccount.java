package dsa3.A2.Bank;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BankAccount {
    private String owner;
    private double balance;
    private List<Transaction> transactions;

    public BankAccount() {}

    public BankAccount(String owner, double balance, List<Transaction> transactions) {
        this.owner = owner;
        this.balance = balance;
        this.transactions = transactions;
    }

    public String getOwner() {return owner;}

    public void setOwner(String owner) {this.owner = owner;}

    public double getBalance() {return balance;}

    public void setBalance(double balance) {this.balance = balance;}

    public List<Transaction> getTransactions() {return transactions;}

    public void setTransactions(List<Transaction> transactions) {this.transactions = transactions;}

    public void saveAccount(String filename) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(filename), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BankAccount fromFile(String filename) {
        ObjectMapper objectMapper = new ObjectMapper();
        BankAccount account = null;
        try {
            account = objectMapper.readValue(new File(filename), BankAccount.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return account;
    }

}



