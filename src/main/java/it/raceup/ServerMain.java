package it.raceup;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class ServerMain {
    private static XYSeries series;
    private static LocalTime lastTime = null;
    private static int index = 1;
    private final static int SECTORS = 3;
    private static Map<Integer, String> pointLabels = new HashMap<Integer, String>();

    private static void addDataToDb(int minutes, int seconds, int millis, float speed) {
        try {
            Connection connection = ConnectionHandler.getConnection();

            String sql = "INSERT INTO telemetry (time, speed) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, minutes + ":" + seconds + "." + millis);
            statement.setFloat(2, speed);
            statement.executeUpdate();
        } catch (RuntimeException e) {
            System.out.println("Couldn't connect to db. " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private static void addDataToChart(int minutes, int seconds, int millis, float speed, int sector) {
        SwingUtilities.invokeLater(() -> {
            String timeStr = String.format("%02d:%02d.%03d", minutes, seconds, millis);
            pointLabels.put(index, "Sector " + sector + " - "+ timeStr);

            series.add(index++, speed);
        });
    }

    private static void getDataFromClient(Socket clientSocket) {
        try {
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

            while (true) {
                float speed = input.readFloat();
                System.out.println("Data received from the car: " + speed + " km/h");

                LocalTime time = LocalTime.now();

                Duration sectorTime = Duration.between(lastTime, time);
                int minutes = sectorTime.toMinutesPart();
                int seconds = sectorTime.toSecondsPart();
                int millis = sectorTime.toMillisPart();
                int sector = index % SECTORS == 0 ? 3 : index % SECTORS;

                addDataToDb(minutes, seconds, millis, speed);
                addDataToChart(minutes, seconds, millis, speed, sector);

                lastTime = time;
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }

    private static void getConnection() {
        ServerSocket serverSocket = null;
        boolean clientConnected = false;
        try {
            serverSocket = new ServerSocket(8080);
            System.out.println("[TCP] Listening on port " + 8080);
            while (!clientConnected) {
                Socket clientSocket = null;
                clientSocket = serverSocket.accept();
                clientConnected = true;

                System.out.println("Client accepted");
                lastTime = LocalTime.now();

                getDataFromClient(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Socket accept failed");
        }
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Telemetry Data");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        series = new XYSeries("Speed");
        XYSeriesCollection dataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createXYLineChart("Final speed at each sector", "Sample", "Speed (km/h)", dataset);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator((dataset1, seriesIndex, item) -> {
            int xIndex = (int) dataset1.getXValue(seriesIndex, item);
            return pointLabels.getOrDefault(xIndex, "");
        });
        chart.getXYPlot().setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        frame.setContentPane(chartPanel);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    public static void main( String[] args ) {
        Thread serverThread = new Thread(ServerMain::getConnection);
        serverThread.start();

        SwingUtilities.invokeLater(ServerMain::createAndShowGUI);
    }
}
