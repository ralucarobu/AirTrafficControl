# Air Traffic Control Simulation

A multi-threaded Air Traffic Control (ATC) simulation developed in Java. This project models aircraft flight behavior between airports, manages takeoff and landing permissions, detects potential mid-air conflicts, and performs conflict resolution through dynamic rerouting. It also includes a real-time graphical user interface (GUI) to visualize the simulation.

---

## Features

- **Multi-threaded Aircraft Simulation**  
  Each aircraft runs as a separate thread, independently simulating flight from its departure airport to its destination.

- **Airport and Slot Management**  
  Airports manage multiple slots. Aircraft must request permission to take off and land, ensuring runway capacity is not exceeded.

- **Air Traffic Control System (ATC)**  
  Monitors all active aircraft, continuously checking for potential conflicts between them based on a configurable safety distance threshold.

- **Collision Detection and Avoidance**  
  - If two aircraft get closer than the safe distance threshold, ATC detects the conflict.
  - The system reroutes one of the aircraft to avoid collision.

- **Landing Permission System**  
  Aircraft request landing permission before completing their journey. Landing is only authorized if a slot is available and the required time gap since the last landing/takeoff is satisfied.

- **Graphical User Interface (GUI)**  
  A real-time 2D visualization of the simulation:
  - Displays aircraft and airports on a coordinate grid.
  - Highlights aircraft in conflict avoidance mode.
  - Displays warnings when aircraft are too close.

---

## Concepts and Architecture

### Threading
- Each `Aircraft` instance runs in its own thread.
- The `AirTrafficControl` system runs on a separate thread, monitoring aircraft positions and detecting conflicts.

### Synchronization
- `AirportControlManager` uses synchronized methods to handle takeoff and landing slot requests, preventing race conditions.

### Waypoint Navigation
- Aircraft follow routes from one airport to another.
- In conflict scenarios, aircraft dynamically add temporary waypoints to their route for rerouting.

### Conflict Avoidance Strategy
- Conflicts are resolved by rerouting the aircraft with the higher altitude at the time of detection.
- Rerouted aircraft move horizontally and vertically before pausing and resuming normal flight.
- The reroute path is calculated relative to their current direction of movement.

### Landing Coordination
- Aircraft request permission to land when reaching their destination.
- Landing is coordinated via `AirportControlManager`, which ensures time-based separation between landings and takeoffs and manages slot availability.

---

