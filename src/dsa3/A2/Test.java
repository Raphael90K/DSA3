package dsa3.A2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dsa3.A2.Bank.BankAccount;
import dsa3.A2.Bank.Transaction;

import java.util.ArrayList;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        Transaction t1 = new Transaction(1, "add", 5, 5);
        Transaction t2 = new Transaction(2, "add", 5, 10);
        Transaction t3 = new Transaction(3, "add", -5, 5);
        BankAccount ba = new BankAccount("Me", 5, Arrays.asList(t1, t2, t3));

        ObjectMapper om = new ObjectMapper();

        try {
            System.out.println(om.writeValueAsString(ba));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ba.saveAccount("test.json");

        BankAccount ba2 = BankAccount.fromFile("test.json");
        try {
            System.out.println(om.writeValueAsString(ba2));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
