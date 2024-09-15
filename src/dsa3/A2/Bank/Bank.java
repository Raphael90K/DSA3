package dsa3.A2.Bank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Bank {
    private Transaction lastTransaction;
    private String owner;
    private double balance;
    private List<Transaction> transactions;

    public Bank() {
    }

    public Bank(String owner, double balance, List<Transaction> transactions) {
        this.lastTransaction = null;
        this.owner = owner;
        this.balance = balance;
        this.transactions = transactions;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    private void setLastTransaction(Transaction last) {
        this.lastTransaction = last;
    }

    public boolean checkValidTransaction(Transaction transaction) {
        if (this.lastTransaction == null) {
            return true;
        }
        if (transaction.getTxId() != lastTransaction.getTxId() + 1) {
            return false;
        }
        return transaction.getValue() == lastTransaction.getValue() + transaction.getChange();
    }

    public void saveAccount(String handler) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(String.format("%s.json", handler)), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bank fromFile(String handler) {

        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(String.format("%s.json", handler));
        Bank account = null;
        if (file.exists()) {
            try {
                account = objectMapper.readValue(file, Bank.class);
                account.setLastTransaction(account.getTransactions().getLast());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            account = new Bank("client", 0, new ArrayList<>());
        }
        return account;
    }

}



