package demo;

import java.util.*;

public class DataCollectorUnsafe {
    private final LinkedList<Item> queue = new LinkedList<>();
    private final Set<String> processed = new HashSet<>();
    private long processedCount = 0;
    private final int capacity;

    public DataCollectorUnsafe(int capacity) {
        this.capacity = capacity;
    }

    public void collectItem(Item item) {
        if (queue.size() >= capacity) return;
        if (!processed.contains(item.key())) {
            queue.addLast(item);
        }
    }

    public Item takeItem() {
        if (queue.isEmpty()) return null;
        return queue.removeFirst();
    }

    public boolean isAlreadyProcessed(String key) {
        return processed.contains(key);
    }

    public void incrementProcessed(String key) {
        if (!processed.add(key)) return;
        processedCount++;
    }

    public long getProcessedCount() { return processedCount; }
}
