package manager;

import aircraft.Aircraft;
import ui.SimulationGUI;

import java.util.List;

public class AirTrafficControl extends Thread {

    private final List<Aircraft> aircraftList;
    private final double safeDistanceThreshold;  // e.g., 500 units
    private final int checkIntervalMillis;       // e.g., 1000ms
    private SimulationGUI gui;

    public AirTrafficControl(List<Aircraft> aircraftList, double safeDistanceThreshold, int checkIntervalMillis) {
        this.aircraftList = aircraftList;
        this.safeDistanceThreshold = safeDistanceThreshold;
        this.checkIntervalMillis = checkIntervalMillis;
    }

    @Override
    public void run() {
        System.out.println("✅ AirTrafficControl started monitoring...");

        while (true) {
            checkForConflicts();
            try {
                Thread.sleep(checkIntervalMillis);
            } catch (InterruptedException e) {
                System.out.println("⚠️ AirTrafficControl interrupted.");
                break;
            }
        }
    }

    private void checkForConflicts() {
        int size = aircraftList.size();

        for (int i = 0; i < size; i++) {
            Aircraft a1 = aircraftList.get(i);

            // Only check planes that are currently flying
            if (!a1.hasTakenOff() || a1.isLanded()) {
                continue;
            }

            for (int j = i + 1; j < size; j++) {
                Aircraft a2 = aircraftList.get(j);

                if (!a2.hasTakenOff() || a2.isLanded()) {
                    continue;
                }

                // Calculate distance between aircraft a1 and a2
                double distance = calculateDistance(a1, a2);

                if (distance <= safeDistanceThreshold) {
                    String warning = String.format("⚠️ Aircraft %d and Aircraft %d are too close! (Distance: %.0f)", a1.getAircraftId(), a2.getAircraftId(), distance);
                    System.out.println(warning);

                    if (gui != null) {
                        gui.displayWarning(warning);
                    }

                    // Reroute BOTH aircraft if not already in avoidance
                    a1.reroute();
                   // a2.reroute();
                } else {
                    // Planes are safe -> revert back if they were rerouted before
                    a1.revertToOriginalRoute();
                    a2.revertToOriginalRoute();
                }
            }
        }
    }

    private double calculateDistance(Aircraft a1, Aircraft a2) {
        double dx = a1.getX() - a2.getX();
        double dy = a1.getY() - a2.getY();

        return Math.sqrt(dx * dx + dy * dy);
    }
    public void setGUI(SimulationGUI gui) {
        this.gui = gui;
    }
}
