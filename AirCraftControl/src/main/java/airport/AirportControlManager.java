package airport;

import aircraft.Aircraft;

public class AirportControlManager {

    private final Airport airport;
    private final int timeBetween;
    private long lastOperationTime = 0;

    public AirportControlManager(Airport airport, int timeBetween) {
        this.airport = airport;
        this.timeBetween = timeBetween;
    }

    public synchronized Slot requestTakeoffPermission(Aircraft aircraft) {
        long currentTime = System.currentTimeMillis() / 1000;
        while ((currentTime - lastOperationTime) < timeBetween || getAvailableSlot() == null) {
            long waitTime = timeBetween - (currentTime - lastOperationTime);
            if (waitTime <= 0) waitTime = 1;
            try {
                wait(waitTime * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Aircraft " + aircraft.getAircraftId() + " interrupted while waiting for takeoff.");
                return null;
            }
            currentTime = System.currentTimeMillis() / 1000;
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
        return null;
    }

    public synchronized Slot requestLandingPermission(Aircraft aircraft) {
        long currentTime = System.currentTimeMillis() / 1000;
        while ((currentTime - lastOperationTime) < timeBetween || getAvailableSlot() == null) {
            long waitTime = timeBetween - (currentTime - lastOperationTime);
            if (waitTime <= 0) waitTime = 1;
            try {
                wait(waitTime * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Aircraft " + aircraft.getAircraftId() + " interrupted while waiting for landing.");
                return null;
            }
            currentTime = System.currentTimeMillis() / 1000;
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
        return null;
    }

    public synchronized void releaseSlot(Slot slot) {
        slot.setAvailable(true);
        System.out.println("Slot " + slot.getId() + " is now available again at Airport " + airport.getId());
        notifyAll(); // notify waiting threads
    }

    private Slot getAvailableSlot() {
        for (Slot slot : airport.getSlots()) {
            if (slot.isAvailable()) {
                return slot;
            }
        }
        return null;
    }
}
