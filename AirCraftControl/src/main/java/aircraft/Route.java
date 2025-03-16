package aircraft;

import airport.Airport;
import java.util.LinkedList;
import java.util.Queue;

public class Route {
    private Airport fromAirport;
    private Airport toAirport;
    private Queue<double[]> waypoints = new LinkedList<>(); // waypoint queue
    private double[] currentTarget; // current destination (waypoint or final)

    public Route(Airport fromAirport, Airport toAirport) {
        this.fromAirport = fromAirport;
        this.toAirport = toAirport;
        this.currentTarget = new double[]{toAirport.getX(), toAirport.getY()};
    }

    public double[] getCurrentTarget() {
        if (!waypoints.isEmpty()) {
            return waypoints.peek();
        }
        return currentTarget;
    }

    public void addWaypoint(double x, double y) {
        waypoints.add(new double[]{x, y});
    }

    public void reachWaypoint() {
        if (!waypoints.isEmpty()) {
            waypoints.poll(); // move to the next waypoint or final destination
        }
    }

    public Airport getFromAirport() {
        return fromAirport;
    }

    public Airport getToAirport() {
        return toAirport;
    }
}
