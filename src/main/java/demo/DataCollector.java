package demo;

import java.util.*;

public class DataCollector {
    private final LinkedList<Item> queue = new LinkedList<>();
    private final Set<String> processed = new HashSet<>();
    private long processedCount = 0;
    private final int capacity;

    public DataCollector(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void collectItem(Item item) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait();
        }
        if (!processed.contains(item.key())) {
            queue.addLast(item);
            notifyAll();
        }
    }

    public synchronized Item takeItem() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        Item it = queue.removeFirst();
        notifyAll();
        return it;
    }

    public synchronized boolean isAlreadyProcessed(String key) {
        return processed.contains(key);
    }

    public synchronized void incrementProcessed(String key) {
        if (!processed.add(key)) return;
        processedCount++;
    }

    public synchronized long getProcessedCount() {
        return processedCount;
    }
}
