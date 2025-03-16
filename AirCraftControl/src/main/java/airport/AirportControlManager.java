package airport;

import aircraft.Aircraft;

public class AirportControlManager {

    private final Airport airport;

    // Time separation between any two operations (takeoff or landing) in seconds
    private final int timeBetween;

    // Tracks the last time a takeoff or landing was granted (in seconds)
    private long lastOperationTime = 0;

    public AirportControlManager(Airport airport, int timeBetween) {
        this.airport = airport;
        this.timeBetween = timeBetween;
    }

    /**
     * Requests permission for an aircraft to take off.
     * Enforces time separation and slot availability.
     */
    public synchronized Slot requestTakeoffPermission(Aircraft aircraft) {
        long currentTime = System.currentTimeMillis() / 1000; // convert ms to seconds

        if ((currentTime - lastOperationTime) < timeBetween) {
            System.out.println("Takeoff DENIED to Aircraft " + aircraft.getAircraftId() +
                    " at Airport " + airport.getId() + " due to time separation.");
            return null;
        }

        Slot availableSlot = getAvailableSlot();
        if (availableSlot != null) {
            availableSlot.setAvailable(false);
            lastOperationTime = currentTime;

            System.out.println("Takeoff GRANTED to Aircraft " + aircraft.getAircraftId() +
                    " from slot " + availableSlot.getId() +
                    " at Airport " + airport.getId());
            return availableSlot;
        }

        System.out.println("Takeoff DENIED to Aircraft " + aircraft.getAircraftId() +
                " at Airport " + airport.getId() + ": no slots available.");
        return null;
    }

    /**
     * Requests permission for an aircraft to land.
     * Enforces time separation and slot availability.
     */
    public synchronized Slot requestLandingPermission(Aircraft aircraft) {
        long currentTime = System.currentTimeMillis() / 1000; // convert ms to seconds

        if ((currentTime - lastOperationTime) < timeBetween) {
            System.out.println("Landing DENIED to Aircraft " + aircraft.getAircraftId() +
                    " at Airport " + airport.getId() + " due to time separation.");
            return null;
        }

        Slot availableSlot = getAvailableSlot();
        if (availableSlot != null) {
            availableSlot.setAvailable(false);
            lastOperationTime = currentTime;

            System.out.println("Landing GRANTED to Aircraft " + aircraft.getAircraftId() +
                    " on slot " + availableSlot.getId() +
                    " at Airport " + airport.getId());
            return availableSlot;
        }

        System.out.println("Landing DENIED to Aircraft " + aircraft.getAircraftId() +
                " at Airport " + airport.getId() + ": no slots available.");
        return null;
    }

    /**
     * Releases a slot after a takeoff or landing operation is complete.
     */
    public synchronized void releaseSlot(Slot slot) {
        slot.setAvailable(true);
        System.out.println("Slot " + slot.getId() + " is now available again at Airport " + airport.getId());
    }

    /**
     * Retrieves the first available slot at the airport.
     * Returns null if all slots are occupied.
     */
    private Slot getAvailableSlot() {
        for (Slot slot : airport.getSlots()) {
            if (slot.isAvailable()) {
                return slot;
            }
        }
        return null;
    }
}
