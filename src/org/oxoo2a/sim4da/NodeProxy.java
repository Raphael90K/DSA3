package org.oxoo2a.sim4da;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodeProxy {
    public NodeProxy(NetworkConnection nc) {
        this.nc = nc;
    }

    public void deliver(Message message, NetworkConnection sender) {
        synchronized (messages) {
            messages.add(new ReceivedMessage(message, sender));
            messages.notify();
        }
    }

    // Geändert um die InterruptedException abfangen und einen Thread, der nur Nachrichten lesen soll,
    // sicher beenden zu können.
    public Message receive() throws InterruptedException {
        synchronized (messages) {
            while (messages.isEmpty()) {
                messages.wait();
            }
            int candidate_index = SimulationBehavior.selectMessageInQueue(messages.size());
            Message candidate = messages.remove(candidate_index).message;
            return candidate;
        }
    }

    private record ReceivedMessage(Message message, NetworkConnection sender) {
    }

    ;

    private final List<ReceivedMessage> messages = Collections.synchronizedList(new ArrayList<>());
    private final NetworkConnection nc;
}
