package aircraft;

import airport.Airport;
import airport.AirportControlManager;
import airport.Slot;

public class Aircraft extends Thread {

    private final int Id;
    private double x, y; // 2D position
    private final int speed;

    private boolean tookOff;
    private boolean landed;

    private boolean conflictAvoidanceActive = false; // new flag

    private Route route;
    private double cruiseAltitude;
    private double cruiseX; // X position where altitude is max
    private double[] currentTarget;  // Next target point (waypoint or destination)
    private Route originalRoute;

    public Aircraft(int id, int speed) {
        this.Id = id;
        this.speed = speed;
        this.tookOff = false;
        this.landed = true; // initially landed at origin airport
        this.route=null;
    }

    public void assignRoute(Route route) {
        this.route = route;
        this.originalRoute = route;
        this.tookOff = false;
        this.landed = true;

        this.x = route.getFromAirport().getX();
        this.y = 0; // coordonatele intiaile la aeroportul din care porneste

        double x1 = route.getFromAirport().getX();
        double x2 = route.getToAirport().getX();

        this.cruiseAltitude = Math.abs(x2 - x1) / 2.0;
        this.cruiseX = (x1 + x2) / 2.0; // Midpoint in X where the aircraft reaches cruiseAltitude

        // Initial target is the destination (ground level)
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
        Slot takeoffSlot = originControl.requestTakeoffPermission(this);
        if (takeoffSlot == null) {
            System.out.println("Aircraft " + Id + " failed to obtain takeoff permission.");
            return;
        }
        System.out.println("Aircraft " + Id + " has TAKEN OFF from Airport " + originAirport.getId() + ", slot " + takeoffSlot.getId());

        // After takeoff release the slot
        originControl.releaseSlot(takeoffSlot);

        // Start flying toward destination
        tookOff = true;
        landed = false;

        long updateInterval = 1000; // ms
        while (!landed && !Thread.currentThread().isInterrupted()) {
            try {
                updatePosition(updateInterval / 1000.0);
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                System.out.println("Aircraft " + Id + " interrupted during flight.");
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println("Aircraft " + Id + " has COMPLETED its route.");
    }

    private void updatePosition(double timeElapsedInSeconds) {
        if (!tookOff || landed || route == null) return;

        double[] target;
        if (!route.getWaypoints().isEmpty()) {
            target = route.getWaypoints().peek();
        } else {
            target = new double[]{ route.getToAirport().getX(), 0 };
        }
        currentTarget = target;

        double targetX = target[0];
        double targetY = target[1];

        double distanceToTravel = speed * timeElapsedInSeconds;
        double remainingDistanceX = Math.abs(targetX - x);


        if (remainingDistanceX > 0) {
            double stepX = Math.min(remainingDistanceX, distanceToTravel);
            x += Math.signum(targetX - x) * stepX;
        }

        if (conflictAvoidanceActive && !route.getWaypoints().isEmpty()) {
            y = currentTarget[1];
        }
        else {
            // Normal flight path
            double x1 = route.getFromAirport().getX();
            double x2 = route.getToAirport().getX();
            if (Math.abs(x - cruiseX) < 1e-3) {
                y = cruiseAltitude;
            } else if ((x1 < x2 && x < cruiseX) || (x1 > x2 && x > cruiseX)) {
                double slope = cruiseAltitude / (cruiseX - x1);
                y = slope * (x - x1);
            } else {
                double slope = cruiseAltitude / (x2 - cruiseX);
                y = cruiseAltitude - slope * (x - cruiseX);
            }
            if (y < 0) y = 0;
        }

        // Check if reached current target
        boolean atTargetX = Math.abs(x - targetX) < 1e-3;
        boolean atTargetY = Math.abs(y - targetY) < 1e-3;

        if (atTargetX && atTargetY) {
            if (!route.getWaypoints().isEmpty()) {
                route.reachWaypoint();
                System.out.println("Aircraft " + Id + " reached waypoint at (" + x + ", " + y + ")");
            } else if (!landed) {
                landed = true;
                System.out.println("Aircraft " + Id + " has LANDED at Airport " + route.getToAirport().getId());
            }
        }
        System.out.printf("Aircraft %d position updated to (%.0f, %.0f)%n", Id, x, y);
    }

    public void reroute() {
        if (!conflictAvoidanceActive) {
            conflictAvoidanceActive = true;

            double rerouteYOffset = 2000; // How much to climb
            double rerouteXOffset = 500;  // Move forward in X

            double rerouteX = this.x + rerouteXOffset;
            double rerouteY = this.y + rerouteYOffset;

            // waypoint into the aircrafts route
            route.addWaypoint(rerouteX, rerouteY);

            System.out.println("Aircraft " + Id + " activated conflict avoidance. Waypoint added: (" + rerouteX + ", " + rerouteY + ")");
        }
    }



    public void revertToOriginalRoute() {
        if (conflictAvoidanceActive) {
            conflictAvoidanceActive = false;
            route.getWaypoints().clear();

            System.out.println("Aircraft " + Id + " reverted to original route. CruiseAltitude: " + cruiseAltitude);
        }
    }


    public int getAircraftId() { return Id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isLanded() { return landed; }
    public boolean hasTakenOff() { return tookOff; }
    public boolean isInConflictAvoidance() { return conflictAvoidanceActive; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}
