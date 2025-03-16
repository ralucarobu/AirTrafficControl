package aircraft;

import airport.Airport;
import airport.AirportControlManager;
import airport.Slot;

import java.util.LinkedList;
import java.util.Queue;

public class Aircraft extends Thread {

    private final int Id;
    private double x, y; // 2D position
    private final int speed;

    private boolean tookOff;
    private boolean landed;

    private Route route;
    private double cruiseAltitude;
    private double cruiseX; // X position where altitude is max
    private Queue<double[]> waypoints = new LinkedList<>();
    private double[] currentTarget;  // Next target point (waypoint or destination)
    private boolean inConflictAvoidance = false;
    private Route originalRoute;

    public Aircraft(int id, int speed) {
        this.Id = id;
        this.speed = speed;
        this.tookOff = false;
        this.landed = true; // initially landed at origin airport
    }

    public void assignRoute(Route route) {
        this.route = route;
        this.originalRoute = route;
        this.tookOff = false;
        this.landed = true;

        // Set initial position at the origin airport
        this.x = route.getFromAirport().getX();
        this.y = 0; // always zero on ground

        double x1 = route.getFromAirport().getX();
        double x2 = route.getToAirport().getX();

        // Set cruiseAltitude = half of the horizontal distance
        this.cruiseAltitude = Math.abs(x2 - x1) / 2.0;

        // Midpoint in X where the aircraft reaches cruiseAltitude
        this.cruiseX = (x1 + x2) / 2.0;

        this.currentTarget = new double[]{ route.getToAirport().getX(), 0 };

        System.out.println("Aircraft " + Id + " assigned route from " + x1 + " to " + x2 +
                ", cruiseAltitude = " + cruiseAltitude + ", cruiseX = " + cruiseX);
    }

    @Override
    public void run() {
        if (route == null) {
            System.out.println("Aircraft " + Id + " has no route assigned!");
            return;
        }

        Airport originAirport = route.getFromAirport();
        AirportControlManager originControl = originAirport.getControlManager();

        // 1. Request takeoff permission
        Slot takeoffSlot = null;
        while (takeoffSlot == null) {
            takeoffSlot = originControl.requestTakeoffPermission(this);
            if (takeoffSlot == null) {
                try {
                    Thread.sleep(1000); // wait and try again
                } catch (InterruptedException e) {
                    System.out.println("Aircraft " + Id + " interrupted while waiting for takeoff.");
                    return;
                }
            }
        }

        System.out.println("Aircraft " + Id + " has TAKEN OFF from Airport " + originAirport.getId() + ", slot " + takeoffSlot);

        // After takeoff, release the slot
        originControl.releaseSlot(takeoffSlot);

        // 2. Start flying toward destination
        tookOff = true;
        landed = false;

        long updateInterval = 1000; // ms
        while (!landed) {
            try {
                updatePosition(updateInterval / 1000.0); // time in seconds
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                System.out.println("Aircraft " + Id + " interrupted during flight.");
                return;
            }
        }

        System.out.println("Aircraft " + Id + " has COMPLETED its route.");
    }

    private void updatePosition(double timeElapsedInSeconds) {
        if (!tookOff || landed || route == null) return;

        // --- Determine Current Target ---
        if (!waypoints.isEmpty()) {
            currentTarget = waypoints.peek(); // Next waypoint
        } else {
            currentTarget = new double[]{ route.getToAirport().getX(), 0 };
        }

        double targetX = currentTarget[0];
        double targetY = currentTarget[1]; // For now, it's usually 0, but can be set in waypoints

        double distanceToTravel = speed * timeElapsedInSeconds;
        double remainingDistanceX = Math.abs(targetX - x);

        // Move X toward targetX unless already there
        if (remainingDistanceX > 0) {
            double stepX = Math.min(remainingDistanceX, distanceToTravel);
            x += Math.signum(targetX - x) * stepX;
        }

        // Handle Y (altitude)
        if (!waypoints.isEmpty()) {
            // Waypoint has its own altitude (optional logic)
            y = targetY;  // For simple rerouting, follow given Y
        } else {
            // Normal flight path: climb to cruiseAltitude and descend
            double x1 = route.getFromAirport().getX();
            double x2 = route.getToAirport().getX();

            if (Math.abs(x - cruiseX) < 1e-3) {
                y = cruiseAltitude;
            } else if ((x1 < x2 && x < cruiseX) || (x1 > x2 && x > cruiseX)) {
                // Ascending
                double slope = cruiseAltitude / (cruiseX - x1);
                y = slope * (x - x1);
            } else {
                // Descending
                double slope = cruiseAltitude / (x2 - cruiseX);
                y = cruiseAltitude - slope * (x - cruiseX);
            }

            if (y < 0) y = 0;
        }

        // --- Check if Reached Current Target ---
        boolean atTargetX = Math.abs(x - targetX) < 1e-3;
        boolean atTargetY = Math.abs(y - targetY) < 1e-3;

        if (atTargetX && atTargetY) {
            if (!waypoints.isEmpty()) {
                waypoints.poll(); // Finished waypoint, continue to next
                System.out.println("Aircraft " + Id + " reached waypoint at (" + x + ", " + y + ")");
            } else if (!landed) {
                landed = true;
                System.out.println("Aircraft " + Id + " has LANDED at Airport " + route.getToAirport().getId());
            }
        }

        System.out.printf("Aircraft %d position updated to (%.0f, %.0f)%n", Id, x, y);
    }

    public void addWaypoint(double x, double y) {
        waypoints.add(new double[]{ x, y });
        System.out.println("Aircraft " + Id + " added waypoint (" + x + ", " + y + ")");
    }
    public void reroute() {
        if (!inConflictAvoidance) {
            // Move to a higher cruiseAltitude for simplicity
            //cruiseAltitude += 1000;
            this.y+=2000;
            inConflictAvoidance = true;

            System.out.println("Aircraft " + Id + " rerouted to avoid collision. New cruiseAltitude: " + cruiseAltitude);
        }
    }

    public void revertToOriginalRoute() {
        if (inConflictAvoidance) {
            // Revert cruiseAltitude based on original route distance
            double x1 = originalRoute.getFromAirport().getX();
            double x2 = originalRoute.getToAirport().getX();
            cruiseAltitude = Math.abs(x2 - x1) / 2.0;
            inConflictAvoidance = false;

            System.out.println("Aircraft " + Id + " reverted to original route. CruiseAltitude: " + cruiseAltitude);
        }
    }


    // Getters
    public int getAircraftId() { return Id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isLanded() { return landed; }
    public boolean hasTakenOff() {
        return tookOff;
    }


    // Setters (optional, usually not needed)
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public boolean isInConflictAvoidance() { return inConflictAvoidance; }
    public void setInConflictAvoidance(boolean value) { inConflictAvoidance = value; }

    public boolean isTookOff() {
        return tookOff;
    }
}
