package dsa3.A2.Bank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Die Klasse Bank repräsentiert den Kontostand und die Transaktionen eines Kunden. Zur Vereinfachung wird nicht zwischen
 * Auszahlung, Zahlung und Überweisung unterschieden. Die Klasse stellt Optionen bereit den aktuellen Status als JSON zu
 * speichern und zu laden.
 *
 */
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

    /**
     * Fügt der Bank eine neue Transaktion hinzu.
     *
     * @param transaction
     */
    public void addTransaction(Transaction transaction) {
        this.lastTransaction = transaction;
        this.transactions.add(transaction);
        this.balance = transaction.getValue();
    }

    public void setLastTransaction(Transaction last) {
        this.lastTransaction = last;
    }

    public Transaction getLastTransaction() {
        return lastTransaction;
    }

    /**
     * Erstellt eine neue Transaktion anhand der bereits in der Bank vorhanden Transaktionen.
     *
     * @param proposalNumber
     * @param change
     * @return
     */
    public Transaction newTransaction(int proposalNumber, double change) {
        Transaction newTx;
        if (lastTransaction == null) {
            newTx = new Transaction(0, proposalNumber, change, change);
        } else {
            newTx = new Transaction(lastTransaction.getTxId() + 1, proposalNumber, change, lastTransaction.getValue() + change);
        }
        return newTx;
    }

    /**
     * Überprüft anhand der bereits vorhanden Transaktionen, ob die neue Transaktion zu den vorherigen passt.
     *
     * @param transaction
     * @return
     */
    public boolean checkValidTransaction(Transaction transaction) {
        if (this.lastTransaction == null) {
            return transaction.getTxId() == 0;
        }
        if (transaction.getTxId() != lastTransaction.getTxId() + 1) {
            return false;
        }
        return transaction.getValue() == lastTransaction.getValue() + transaction.getChange();
    }

    /**
     * Speichert das aktuelle Bankobjekt als JSON.
     *
     * @param handler Name der Datei ohne Endung. Im Fall der Anwendung der Name des Knotens.
     */
    public void saveAccount(String handler) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(String.format("%s.json", handler)), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Läd ein Bankobjekt von einer JSON Datei.
     *
     * @param handler
     * @return
     */
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



