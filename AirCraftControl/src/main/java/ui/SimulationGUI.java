package ui;

import aircraft.Aircraft;
import airport.Airport;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationGUI {

    private JFrame frame;
    private SimulationPanel panel;
    private List<Aircraft> aircraftList;
    private List<Airport> airportList;

    private List<String> warningsList = new ArrayList<>();
    private long simulationStartTime;

    // World size for scaling purposes
    private final double worldXMax = 20000;
    private final double worldYMax = 12000;

    // Plane image & scale
    private Image planeImage;
    private double scaleFactor = 0.05;

    // ✅ Constructor
    public SimulationGUI(List<Aircraft> aircraftList, List<Airport> airportList) {
        this.aircraftList = aircraftList;
        this.airportList = airportList;

        // Load plane image from resources (make sure it's in src/main/resources)
        planeImage = new ImageIcon(getClass().getResource("/plane.png")).getImage();

        simulationStartTime = System.currentTimeMillis();

        // Setup JFrame
        frame = new JFrame("Air Traffic Control Simulation");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setup Panel
        panel = new SimulationPanel();
        frame.add(panel);
        frame.setVisible(true);

        // Refresh every 50ms
        Timer timer = new Timer(50, e -> panel.repaint());
        timer.start();
    }

    // ✅ Display warning message
    public void displayWarning(String message) {
        warningsList.add(message);

        // Optional: Automatically remove the message after 3 seconds
        Timer clearWarningTimer = new Timer(3000, e -> warningsList.remove(message));
        clearWarningTimer.setRepeats(false);
        clearWarningTimer.start();
    }

    // ===========================
    // ✅ Inner SimulationPanel
    // ===========================
    private class SimulationPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawScene((Graphics2D) g);
        }

        private void drawScene(Graphics2D g2d) {
            // Background
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Draw airports
            drawAirports(g2d);

            // Draw aircraft
            drawAircraft(g2d);

            // Draw simulation time
            drawSimulationTime(g2d);

            // Draw warnings
            drawWarnings(g2d);
        }

        private void drawAirports(Graphics2D g2d) {
            int airportWidth = 50;
            int airportHeight = 50;

            g2d.setColor(Color.BLUE);
            for (Airport airport : airportList) {
                int screenX = mapX(airport.getX());
                int screenY = getHeight() - 100;

                g2d.fillRect(screenX - airportWidth / 2, screenY, airportWidth, airportHeight);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Airport " + airport.getId(), screenX - 20, screenY + airportHeight + 15);

                g2d.setColor(Color.BLUE);
            }
        }

        private void drawAircraft(Graphics2D g2d) {
            for (Aircraft aircraft : aircraftList) {
                int screenX = mapX(aircraft.getX());
                int screenY = mapY(aircraft.getY());

                // Draw plane image
                g2d.drawImage(planeImage, screenX - 20, screenY - 20, 40, 40, this);

                // Highlight rerouted/conflict avoidance planes
                if (aircraft.isInConflictAvoidance()) {
                    g2d.setColor(Color.RED);
                    g2d.drawRect(screenX - 20, screenY - 20, 40, 40);
                }

                // Draw aircraft label
                g2d.setColor(Color.BLACK);
                g2d.drawString("A" + aircraft.getAircraftId(), screenX - 10, screenY - 25);
            }
        }

        private void drawSimulationTime(Graphics2D g2d) {
            long elapsedMillis = System.currentTimeMillis() - simulationStartTime;
            long seconds = elapsedMillis / 1000;

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("Simulation Time: " + seconds + "s", getWidth() - 200, 30);
        }

        private void drawWarnings(Graphics2D g2d) {
            int warningY = 80;

            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));

            for (String warning : warningsList) {
                g2d.drawString("⚠ " + warning, getWidth() - 350, warningY);
                warningY += 20;
            }
        }

        // ✅ Coordinate mapping
        private int mapX(double worldX) {
            double scale = (getWidth() - 100) / worldXMax;
            return (int) (50 + worldX * scale);
        }

        private int mapY(double worldY) {
            double scale = (getHeight() - 200) / worldYMax;
            return getHeight() - 150 - (int) (worldY * scale);
        }
    }
}
