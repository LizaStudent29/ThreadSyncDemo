package demo;

public class DeadlockSafeTransfer {

    static class Account {
        private long balance;
        private final int id;

        public Account(int id, long initial) {
            this.id = id;
            this.balance = initial;
        }
        public int id() { return id; }
        public long balance() { return balance; }
        private void deposit(long amount) { balance += amount; }
        private void withdraw(long amount) { balance -= amount; }
    }

    public static void transfer(Account from, Account to, long amount) {
        Account first = from.id() < to.id() ? from : to;
        Account second = from.id() < to.id() ? to : from;

        synchronized (first) {
            synchronized (second) {
                if (from.balance() < amount) return;
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }

    public static void demo() throws InterruptedException {
        Account a = new Account(1, 1_000_000);
        Account b = new Account(2, 1_000_000);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100_000; i++) transfer(a, b, 1);
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100_000; i++) transfer(b, a, 1);
        });

        t1.start(); t2.start();
        t1.join(); t2.join();
    }
}
