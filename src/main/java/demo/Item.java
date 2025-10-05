package demo;

import java.util.Objects;

public class Item {
    private final String key;
    private final String payload;

    public Item(String key, String payload) {
        this.key = key;
        this.payload = payload;
    }

    public String key() { return key; }
    public String payload() { return payload; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return Objects.equals(key, item.key);
    }

    @Override
    public int hashCode() { return Objects.hash(key); }

    @Override
    public String toString() { return "Item{" + key + "}"; }
}
