package airport;

public class Slot {

    private final int id;
    private boolean available; // true = available, false = occupied

    public Slot(int id) {
        this.id = id;
        this.available = true;
    }

    public int getId() {
        return id;
    }

    public synchronized boolean isAvailable() {
        return available;
    }

    public synchronized void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Slot{id=" + id + ", available=" + available + "}";
    }
}
