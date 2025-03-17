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

        Airport airport1 = new Airport(1, 0, 2, 5);
        Airport airport2 = new Airport(2, 10000, 2, 5);
        Airport airport3 = new Airport(3, 20000, 2, 5);
        Airport airport4 = new Airport(4, 30000, 1, 5);

        System.out.println("Airports created:");
        System.out.println(airport1);
        System.out.println(airport2);
        System.out.println(airport3);
        System.out.println(airport4);

        Aircraft aircraft1 = new Aircraft(1, 1000);
        Aircraft aircraft2 = new Aircraft(2, 800);
        Aircraft aircraft3 = new Aircraft(3, 900);
        Aircraft aircraft4 = new Aircraft(4, 850);
        Aircraft aircraft5 = new Aircraft(5, 1000);
        Aircraft aircraft6 = new Aircraft(6, 1200);

        // assign routes
        aircraft1.assignRoute(new Route(airport1, airport2));
        aircraft2.assignRoute(new Route(airport2, airport1));
        aircraft3.assignRoute(new Route(airport2, airport3));
        aircraft4.assignRoute(new Route(airport3, airport2));
        aircraft5.assignRoute(new Route(airport3, airport4));
        aircraft6.assignRoute(new Route(airport4, airport1));
        System.out.println("Aircraft assigned to routes.");


        List<Aircraft> aircraftList = new ArrayList<>();
        aircraftList.add(aircraft1);
        aircraftList.add(aircraft2);
        aircraftList.add(aircraft3);
        aircraftList.add(aircraft4);
        aircraftList.add(aircraft5);
        aircraftList.add(aircraft6);

       //create and start aircraft control
        double safeDistanceThreshold = 3000;   // if two aircraft are within 3000 units=>CONFLICT
        int checkIntervalMillis = 1000;        // check every second.

        AirTrafficControl atc = new AirTrafficControl(aircraftList, safeDistanceThreshold, checkIntervalMillis);
        atc.start();

        List<Airport> airportList = new ArrayList<>();
        airportList.add(airport1);
        airportList.add(airport2);
        airportList.add(airport3);
        airportList.add(airport4);

        SwingUtilities.invokeLater(() -> {
            SimulationGUI gui = new SimulationGUI(aircraftList, airportList);
            atc.setGUI(gui);  //to display warnings
        });


        aircraft1.start();
        aircraft2.start();
        aircraft3.start();
        aircraft4.start();
        aircraft5.start();
        aircraft6.start();

        try {
            Thread.sleep(60000); // 60 seconds simulation
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Simulation complete.");


        for (Aircraft aircraft : aircraftList) {
            aircraft.interrupt();
        }
        atc.interrupt();

        for (Aircraft aircraft : aircraftList) {
            try {
                aircraft.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            atc.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All threads have terminated. Main thread exiting.");
    }
}
