package dsa3.A2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dsa3.A2.Bank.Bank;
import dsa3.A2.Bank.Transaction;

import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main2(String[] args) throws JsonProcessingException {
        NetworkNode n1 = new NetworkNode(0);
        NetworkNode n2 = new NetworkNode(1);
        List<String> nodes = Arrays.asList(n1.getName(), n2.getName());

        n1.setConnections(nodes);
        n2.setConnections(nodes);

        NetworkNode n3 = new NetworkNode(2);
    }

    public static void main(String[] args) {
        Transaction t1 = new Transaction(1, 1, 5, 5);
        Transaction t2 = new Transaction(2, 2, 5, 10);
        Transaction t3 = new Transaction(3, 3, -5, 5);
        String s = t1.toString();
        Transaction t4 = Transaction.fromString(s);
        System.out.println(t4);
        Bank ba = new Bank("Me", 5, Arrays.asList(t1, t2, t3));

        ObjectMapper om = new ObjectMapper();

        try {
            System.out.println(om.writeValueAsString(ba));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ba.saveAccount("test.json");

        Bank ba2 = Bank.fromFile("test");
        try {
            System.out.println(om.writeValueAsString(ba2));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
