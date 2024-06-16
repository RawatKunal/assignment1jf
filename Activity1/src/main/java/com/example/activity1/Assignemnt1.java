package com.example.activity1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Assignemnt1 extends Application {

    private Stage stage;
    private Scene genreScene, pieChartScene, barChartScene;
    private TableView<AnnualRainfallData> tableView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

// Load the icon image
        Image icon = new Image(getClass().getResourceAsStream("/com/example/activity1/rain.png"));
        primaryStage.getIcons().add(icon);

        // Connect to MySQL database (replace with your database credentials)
        String url = "jdbc:mysql://localhost:3306/RainfallData";
        String user = "root";
        String password = "";
        Connection conn = DriverManager.getConnection(url, user, password);

        // Set up TableView for annual rainfall data
        tableView = createTableView();

        // Set up PieChart for annual rainfall
        PieChart pieChart = createPieChart(conn);

        // Set up BarChart for annual rainfall
        BarChart<String, Number> barChart = createBarChart(conn);

        // Button to switch to PieChart scene
        Button switchToPieChartButton = new Button("Switch to Pie Chart");
        switchToPieChartButton.setOnAction(e -> stage.setScene(pieChartScene));

        // Button to switch to BarChart scene
        Button switchToBarChartButton = new Button("Switch to Bar Chart");
        switchToBarChartButton.setOnAction(e -> stage.setScene(barChartScene));

        // Button to switch back to TableView scene
        Button switchToTableViewButton = new Button("Back to Table View");
        switchToTableViewButton.setOnAction(e -> stage.setScene(genreScene));

        // Layout for TableView scene
        VBox genreLayout = new VBox(10);
        genreLayout.setStyle("-fx-background-color: #f0f0f0;"); // Set background color
        genreLayout.getChildren().addAll(tableView, switchToPieChartButton, switchToBarChartButton);
        genreScene = new Scene(genreLayout, 800, 600);

        // Layout for PieChart scene
        VBox pieChartLayout = new VBox(10);
        pieChartLayout.setStyle("-fx-background-color: #f0f0f0;"); // Set background color
        pieChartLayout.getChildren().addAll(pieChart, switchToTableViewButton);
        pieChartScene = new Scene(pieChartLayout, 800, 600);

        // Layout for BarChart scene
        VBox barChartLayout = new VBox(10);
        barChartLayout.setStyle("-fx-background-color: #f0f0f0;"); // Set background color
        barChartLayout.getChildren().addAll(barChart, switchToTableViewButton);
        barChartScene = new Scene(barChartLayout, 800, 600);

        // Close database connection
        conn.close();

        // Set initial scene
        stage.setScene(genreScene);
        stage.setTitle("Annual Rainfall Data");
        stage.show();
    }

    private TableView<AnnualRainfallData> createTableView() throws Exception {
        TableView<AnnualRainfallData> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define columns
        TableColumn<AnnualRainfallData, String> stateColumn = new TableColumn<>("State");
        TableColumn<AnnualRainfallData, Integer> yearColumn = new TableColumn<>("Year");
        TableColumn<AnnualRainfallData, Double> rainfallColumn = new TableColumn<>("Rainfall (mm)");

        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        rainfallColumn.setCellValueFactory(new PropertyValueFactory<>("rainfall"));

        tableView.getColumns().addAll(stateColumn, yearColumn, rainfallColumn);

        // Query data from database
        String query = "SELECT State, Year, Rainfall FROM AnnualRainfall";
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/RainfallData", "root", "");
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Populate table with data
        List<AnnualRainfallData> dataList = new ArrayList<>();
        while (resultSet.next()) {
            String state = resultSet.getString("State");
            int year = resultSet.getInt("Year");
            double rainfall = resultSet.getDouble("Rainfall");
            AnnualRainfallData data = new AnnualRainfallData(state, year, rainfall);
            dataList.add(data);
        }

        tableView.getItems().addAll(dataList);

        // Close database connection
        resultSet.close();
        stmt.close();
        conn.close();

        return tableView;
    }

    private PieChart createPieChart(Connection conn) throws Exception {
        // Query data from database
        String query = "SELECT State, SUM(Rainfall) AS TotalRainfall FROM AnnualRainfall GROUP BY State";

        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Prepare data for pie chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        while (resultSet.next()) {
            String state = resultSet.getString("State");
            double totalRainfall = resultSet.getDouble("TotalRainfall");
            pieChartData.add(new PieChart.Data(state, totalRainfall));
        }

        // Close database connection
        resultSet.close();
        stmt.close();

        // Create PieChart
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Annual Rainfall Data by State");

        return pieChart;
    }

    private BarChart<String, Number> createBarChart(Connection conn) throws Exception {
        // Define the axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("State");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Rainfall (mm)");

        // Create the BarChart
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Annual Rainfall Data by State");

        // Query data from database
        String query = "SELECT State, SUM(Rainfall) AS TotalRainfall FROM AnnualRainfall GROUP BY State";
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        // Prepare data for bar chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Rainfall");

        while (resultSet.next()) {
            String state = resultSet.getString("State");
            double totalRainfall = resultSet.getDouble("TotalRainfall");
            series.getData().add(new XYChart.Data<>(state, totalRainfall));
        }

        barChart.getData().add(series);

        // Close database connection
        resultSet.close();
        stmt.close();

        return barChart;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // AnnualRainfallData class for TableView
    public static class AnnualRainfallData {
        private final String state;
        private final int year;
        private final double rainfall;

        public AnnualRainfallData(String state, int year, double rainfall) {
            this.state = state;
            this.year = year;
            this.rainfall = rainfall;
        }

        public String getState() {
            return state;
        }

        public int getYear() {
            return year;
        }

        public double getRainfall() {
            return rainfall;
        }
    }
}
