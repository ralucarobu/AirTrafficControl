package airport;

import java.util.ArrayList;
import java.util.List;

public class Airport {

    private final int Id;
    private final double x; // position along X-axis
    private final double y = 0; // airports are at ground level (fixed)

    private final List<Slot> slots;
    private final AirportControlManager airportControlManager;

    public Airport(int Id, double x, int nbOfSlots, int timeBetweenOps) {
        this.Id = Id;
        this.x = x;

        // Y is always zero for airports, we don't need to pass it

        this.slots = new ArrayList<>();
        for (int i = 0; i < nbOfSlots; i++) {
            Slot slot = new Slot(i);
            slots.add(slot);
        }

        this.airportControlManager = new AirportControlManager(this, timeBetweenOps);
    }

    // Getters
    public int getId() {
        return Id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public AirportControlManager getControlManager() {
        return airportControlManager;
    }

    // Optional: easy toString for debugging
    @Override
    public String toString() {
        return "Airport " + Id + " at (" + x + ", " + y + ")";
    }
}
