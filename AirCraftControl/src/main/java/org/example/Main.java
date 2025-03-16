package org.example;

import aircraft.Aircraft;
import aircraft.Route;
import airport.Airport;
import manager.AirTrafficControl;
import ui.SimulationGUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // ✅ 1. Create Airports
        Airport airport1 = new Airport(1, 0, 2, 5);         // x = 0
        Airport airport2 = new Airport(1, 10000, 2, 5);     // x = 10000
        Airport airport3 = new Airport(3, 20000, 2, 5);     // x = 20000
        Airport airport4 = new Airport(4, 30000, 1, 5);     // x = 30000

        System.out.println("✅ Airports created:");
        System.out.println(airport1);
        System.out.println(airport2);
        System.out.println(airport3);
        System.out.println(airport4);

        // ✅ 2. Create Aircraft
        Aircraft aircraft1 = new Aircraft(1, 1000); // A1 ➡ A2
        Aircraft aircraft2 = new Aircraft(2, 800);  // A2 ➡ A1
        Aircraft aircraft3 = new Aircraft(3, 900);  // A2 ➡ A3
        Aircraft aircraft4 = new Aircraft(4, 850);  // A3 ➡ A2
        Aircraft aircraft5 = new Aircraft(5, 1000); // A3 ➡ A4
        Aircraft aircraft6 = new Aircraft(6, 1200); // A4 ➡ A1

        // ✅ 3. Assign Routes
        aircraft1.assignRoute(new Route(airport1, airport2)); // ➡ 10000
        aircraft2.assignRoute(new Route(airport2, airport1)); // ➡ 0
        aircraft3.assignRoute(new Route(airport2, airport3)); // ➡ 20000
        aircraft4.assignRoute(new Route(airport3, airport2)); // ➡ 10000
        aircraft5.assignRoute(new Route(airport3, airport4)); // ➡ 30000
        aircraft6.assignRoute(new Route(airport4, airport1)); // ➡ 0

        System.out.println("✅ Aircraft assigned to routes.");

        // ✅ 4. Prepare Aircraft List for AirTrafficControl
        List<Aircraft> aircraftList = new ArrayList<>();
        aircraftList.add(aircraft1);
        aircraftList.add(aircraft2);
        aircraftList.add(aircraft3);
        aircraftList.add(aircraft4);
        aircraftList.add(aircraft5);
        aircraftList.add(aircraft6);

        // ✅ 5. Create and Start AirTrafficControl
        double safeDistanceThreshold = 3000;   // If two aircraft are within 3000 units, reroute them.
        int checkIntervalMillis = 1000;        // Check every second.

        AirTrafficControl atc = new AirTrafficControl(aircraftList, safeDistanceThreshold, checkIntervalMillis);
        atc.start();

        List<Airport> airportList = new ArrayList<>();
        airportList.add(airport1);
        airportList.add(airport2);
        airportList.add(airport3);
        airportList.add(airport4);

        SwingUtilities.invokeLater(() -> {
            SimulationGUI gui = new SimulationGUI(aircraftList, airportList);
            atc.setGUI(gui);  // So it can display warnings
        });


        // ✅ 6. Start All Aircraft
        aircraft1.start();
        aircraft2.start();
        aircraft3.start();
        aircraft4.start();
        aircraft5.start();
        aircraft6.start();

        // ✅ 7. Run the Simulation for 60 Seconds
        try {
            Thread.sleep(60000); // 60 seconds simulation
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("✅ Simulation complete. Main thread exiting.");
    }
}
