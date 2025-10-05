package demo;

import java.util.Random;
import java.util.concurrent.Callable;

public class Workers {

    public static class Producer implements Callable<Long> {
        private final DataCollector collector;
        private final int items;
        private final Random rnd = new Random();

        public Producer(DataCollector collector, int items) {
            this.collector = collector;
            this.items = items;
        }

        @Override public Long call() throws Exception {
            long pushed = 0;
            for (int i = 0; i < items; i++) {
                String key = "key-" + rnd.nextInt(items * 2);
                collector.collectItem(new Item(key, "payload-" + i));
                pushed++;
            }
            return pushed;
        }
    }

    public static class Consumer implements Callable<Long> {
        private final DataCollector collector;
        private final long targetToProcess;

        public Consumer(DataCollector collector, long targetToProcess) {
            this.collector = collector;
            this.targetToProcess = targetToProcess;
        }

        @Override public Long call() throws Exception {
            long processed = 0;
            while (processed < targetToProcess) {
                Item it = collector.takeItem();
                if (!collector.isAlreadyProcessed(it.key())) {
                    collector.incrementProcessed(it.key());
                    processed++;
                }
            }
            return processed;
        }
    }
}
