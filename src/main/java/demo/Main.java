package demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws Exception {
        runLoadTestSynchronized();
        DeadlockSafeTransfer.demo();
        System.out.println("DeadlockSafeTransfer: demo finished without deadlock.");
    }

    private static void runLoadTestSynchronized() throws Exception {
        System.out.println("== Load test: synchronized DataCollector ==");

        int producers = 4;
        int consumers = 4;
        int itemsPerProducer = 20000; // 🔹 можно увеличить позже
        long totalTarget = (long) producers * itemsPerProducer;
        int capacity = 1024;

        DataCollector collector = new DataCollector(capacity);
        ExecutorService pool = Executors.newFixedThreadPool(producers + consumers);

        List<Callable<Long>> tasks = new ArrayList<>();
        for (int i = 0; i < producers; i++) tasks.add(new Workers.Producer(collector, itemsPerProducer));
        for (int i = 0; i < consumers; i++) tasks.add(new ProgressConsumer(collector, totalTarget / consumers, i + 1));

        long t0 = System.nanoTime();
        List<Future<Long>> res = pool.invokeAll(tasks);
        pool.shutdown();
        pool.awaitTermination(2, TimeUnit.MINUTES);
        long t1 = System.nanoTime();

        long produced = res.subList(0, producers).stream().mapToLong(f -> get(f)).sum();
        long consumed = res.subList(producers, producers + consumers).stream().mapToLong(f -> get(f)).sum();

        System.out.printf(
                "%n✅ Completed! Produced: %,d | Consumed(unique): %,d | processedCount=%,d | time=%.3fs%n",
                produced, consumed, collector.getProcessedCount(), (t1 - t0) / 1e9);
    }

    private static long get(Future<Long> f) {
        try { return f.get(); } catch (Exception e) { throw new RuntimeException(e); }
    }

    // 🔹 Внутренний класс-потребитель с отображением прогресса
    private static class ProgressConsumer implements Callable<Long> {
        private final DataCollector collector;
        private final long targetToProcess;
        private final int id;

        public ProgressConsumer(DataCollector collector, long targetToProcess, int id) {
            this.collector = collector;
            this.targetToProcess = targetToProcess;
            this.id = id;
        }

        @Override
        public Long call() throws Exception {
            long processed = 0;
            while (processed < targetToProcess) {
                Item it = collector.takeItem();
                if (!collector.isAlreadyProcessed(it.key())) {
                    collector.incrementProcessed(it.key());
                    processed++;
                    // 🔸 вывод прогресса каждые 5000 элементов
                    if (processed % 5000 == 0) {
                        System.out.printf("Consumer-%d processed %,d items%n", id, processed);
                    }
                }
            }
            System.out.printf("Consumer-%d finished! Total processed: %,d%n", id, processed);
            return processed;
        }
    }
}
