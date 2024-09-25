package dsa3.A2;

import com.fasterxml.jackson.databind.ObjectMapper;
import dsa3.A2.Bank.Bank;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SimulationEvaluation {

    public static void main(String[] args) {
        ObjectMapper om = new ObjectMapper();

        String s0;
        String s1;
        String s2;
        String s3;
        String s4;

        System.out.println("Evaluation Simulation 1");
        try {
            s0 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM1/Node0.json"), Bank.class));
            s1 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM1/Node1.json"), Bank.class));
            s2 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM1/Node2.json"), Bank.class));
            s3 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM1/Node3.json"), Bank.class));
            s4 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM1/Node4.json"), Bank.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List <String> strings= Arrays.asList(s0, s1, s2, s3, s4);

        for (int i = 0; i < 5; i++) {
            for (int j = i + 1; j < 5; j++) {
                System.out.println("Node%d equals Node%s: %b".formatted(i, j, strings.get(i).equals(strings.get(j))));
            }
        }

        System.out.println("\n#######################");
        System.out.println("Evaluation Simulation 2");
        try {
            s0 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM2/Node0.json"), Bank.class));
            s1 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM2/Node1.json"), Bank.class));
            s2 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM2/Node2.json"), Bank.class));
            s3 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM2/Node3.json"), Bank.class));
            s4 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM2/Node4.json"), Bank.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        strings= Arrays.asList(s0, s1, s2, s3, s4);

        for (int i = 0; i < 5; i++) {
            for (int j = i + 1; j < 5; j++) {
                System.out.println("Node%d equals Node%s: %b".formatted(i, j, strings.get(i).equals(strings.get(j))));
            }
        }

        System.out.println("\n#######################");
        System.out.println("Evaluation Simulation 3");
        try {
            s1 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM3/Node1.json"), Bank.class));
            s2 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM3/Node2.json"), Bank.class));
            s3 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM3/Node3.json"), Bank.class));
            s4 = om.writeValueAsString(om.readValue(new File("src/dsa3/A2/SIM3/Node4.json"), Bank.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        strings= Arrays.asList(s0, s1, s2, s3, s4);

        for (int i = 1; i < 5; i++) {
            for (int j = i + 1; j < 5; j++) {
                System.out.println("Node%d equals Node%s: %b".formatted(i, j, strings.get(i).equals(strings.get(j))));
            }
        }
    }
}
