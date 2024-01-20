package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.*;
import com.sun.javafx.charts.Legend;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.List;
import java.util.Set;

public class SimulationPresenter implements MapChangeListener {
    @FXML
    private GridPane simulationGrid;
    @FXML
    private Label STAT_1,STAT_2,STAT_3,STAT_4,STAT_5,STAT_6,STAT_7,STAT_8,STAT_9;
    @FXML
    private Label STAT_A1,STAT_A2,STAT_A3,STAT_A4,STAT_A5,STAT_A6,STAT_A7,STAT_A8,STAT_A9,STAT_A10;
    @FXML
    private HBox lineChartContainer;

    private Simulation simulation;
    private List<Animal> mostFrequentGenesAnimals = null;
    private AbstractWorldMap map;
    private int CELL_SIZE;
    private Label[][] cellLabels;
    private Set<Vector2d> prevOccupiedPositions;
    private Label selectedCellLabel;
    private Animal trackedAnimal;
    private Animal prevTrackedAnimal;
    private final int numberOfStats = 9;
    private Label[] statValues = new Label[numberOfStats];
    private Label[] trackedAnimalStats = new Label[10];
    private LineChart<Number, Number> lineChart;
    public void initialize(Simulation simulation){
        this.simulation = simulation;
        this.map = simulation.getMap();
        CELL_SIZE = Math.min(
                800 / (map.getMapHeight()),
                800 / (map.getMapWidth()));

        cellLabels = new Label[map.getMapHeight()][map.getMapWidth()];
        initializeChart();

        statValues[0] = STAT_1;
        statValues[1] = STAT_2;
        statValues[2] = STAT_3;
        statValues[3] = STAT_4;
        statValues[4] = STAT_5;
        statValues[5] = STAT_6;
        statValues[6] = STAT_7;
        statValues[7] = STAT_8;
        statValues[8] = STAT_9;

        trackedAnimalStats[0] = STAT_A1;
        trackedAnimalStats[1] = STAT_A2;
        trackedAnimalStats[2] = STAT_A3;
        trackedAnimalStats[3] = STAT_A4;
        trackedAnimalStats[4] = STAT_A5;
        trackedAnimalStats[5] = STAT_A6;
        trackedAnimalStats[6] = STAT_A7;
        trackedAnimalStats[7] = STAT_A8;
        trackedAnimalStats[8] = STAT_A9;
        trackedAnimalStats[9] = STAT_A10;

        drawGrid();
    }

    private void drawGrid() {
        simulationGrid.getColumnConstraints().clear();
        simulationGrid.getRowConstraints().clear();

        Boundary boundaries = map.getCurrentBounds();
        int width = boundaries.topRightCorner().x() - boundaries.bottomLeftCorner().x() + 1;
        int height = boundaries.topRightCorner().y() - boundaries.bottomLeftCorner().y() + 1;

        for (int x = 0; x < width; x++) {
            simulationGrid.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));
        }

        for (int y = 0; y < height; y++) {
            simulationGrid.getRowConstraints().add(new RowConstraints(CELL_SIZE));
        }

        colorGrid(height, width);
    }

    private void colorGrid(int height, int width) {
        TileType[][] tiles = map.getTiles();

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                Label cellLabel = new Label();
                cellLabel.setMinWidth(CELL_SIZE);
                cellLabel.setMinHeight(CELL_SIZE);
                cellLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, null, new BorderWidths(0.4))));
                cellLabel.setStyle("-fx-alignment: CENTER;");

                if(tiles[y][x] == TileType.JUNG){
                    cellLabel.setBackground(new Background(new BackgroundFill(Color.rgb(18, 74, 13), CornerRadii.EMPTY, Insets.EMPTY)));
                }
                else{
                    cellLabel.setBackground(new Background(new BackgroundFill(Color.rgb(210, 180, 140), CornerRadii.EMPTY, Insets.EMPTY)));
                }

                cellLabel.setOnMouseClicked(event -> {
                    int clickedX = GridPane.getColumnIndex(cellLabel);
                    int clickedY = map.getMapHeight() - GridPane.getRowIndex(cellLabel) - 1;
                    Vector2d position = new Vector2d(clickedX, clickedY);

                    if (trackedAnimal != null && position.equals(trackedAnimal.getPosition()) && trackedAnimal.getEnergy() <= 0) {
                        untrackAnimal();
                    }
                    else if (map.objectAt(position) instanceof Animal) {
                        handleAnimalClick(new Vector2d(clickedX, clickedY));
                    }
                });

                GridPane.setHalignment(cellLabel, HPos.CENTER);
                cellLabels[y][x] = cellLabel;
                simulationGrid.add(cellLabel, x, height - y - 1);
            }
        }
    }

    private void trackAnimal(Vector2d position) {
        WorldElement objectAtPosition = map.objectAt(position);

        if (objectAtPosition instanceof Animal) {
            Animal potentialTrackedAnimal = (Animal) objectAtPosition;

            if (potentialTrackedAnimal.getEnergy() > 0) {
                trackedAnimal = potentialTrackedAnimal;

                if (selectedCellLabel != null) {
                    selectedCellLabel.setBorder(new Border(new BorderStroke(
                            Color.ORANGE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4)
                    )));
                }
            }
        }
    }

    private void untrackAnimal() {
        trackedAnimal = null;
        if (selectedCellLabel != null) {
            selectedCellLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, null, new BorderWidths(0.4))));
            selectedCellLabel = null;
        }
    }

    private void untrackAnimalAfterDeath(){
        if (selectedCellLabel != null) {
            selectedCellLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, null, new BorderWidths(0.4))));
            selectedCellLabel = null;
        }
    }


    private void showTrackedAnimalStats() {
        if(trackedAnimal != null) {
            String[] stats = trackedAnimal.getAnimalStats();

            for(int i = 0; i < stats.length; i++)
                trackedAnimalStats[i].setText(stats[i]);
        }
        else {
            for (Label trackedAnimalStat : trackedAnimalStats)
                trackedAnimalStat.setText("N/A");
        }
    }


    private void handleAnimalClick(Vector2d position) {
        Animal clickedAnimal = (Animal) map.objectAt(position);
        Label clickedLabel = cellLabels[clickedAnimal.getPosition().y()][clickedAnimal.getPosition().x()];

        if (trackedAnimal != null && selectedCellLabel.equals(clickedLabel)) {
            untrackAnimal();
        } else {
            trackAnimal(position);
        }

        Platform.runLater(this::drawMap);
    }

    private void fillMap(){
        clearGrid();
        prevOccupiedPositions = map.getAllOccupiedPositions();
        int grassSize = (int)(1.75*CELL_SIZE);

        for (Vector2d vec : prevOccupiedPositions) {
            int x = vec.x();
            int y = vec.y();
            Object objectAtPosition = map.objectAt(new Vector2d(x,y));
            Label cellLabel = cellLabels[y][x];

            if (trackedAnimal != null && trackedAnimal.getPosition() == vec && trackedAnimal.getEnergy() <= 0){
                prevTrackedAnimal = trackedAnimal;
                untrackAnimalAfterDeath();
            }

            if (objectAtPosition != null) {
                if (objectAtPosition instanceof Animal) {
                    Animal animal = (Animal) objectAtPosition;
                    if(trackedAnimal != null && animal.getPosition().equals(trackedAnimal.getPosition())){
                        continue;
                    }

                    if(animal.getEnergy() > 0){
                        cellLabel.setGraphic(drawAnimal(animal));
                    }
                }
                else {
                    if (objectAtPosition instanceof Grass){
                        cellLabel.setText(objectAtPosition.toString());
                        cellLabel.setTextFill(Color.rgb(0, 128, 0));
                        cellLabel.setStyle("-fx-alignment: CENTER;-fx-font-weight: bold;-fx-font-size: " + grassSize + "px;");
                    }
                    else{
                        cellLabel.setText("?");
                        cellLabel.setTextFill(Color.rgb(255, 0, 255));
                        cellLabel.setStyle("-fx-alignment: CENTER;-fx-font-weight: bold;-fx-font-size: " + grassSize/1.5 + "px;");
                    }
                }
            }
        }

        if (mostFrequentGenesAnimals != null){
            for(Animal animal : mostFrequentGenesAnimals){
                int x = animal.getPosition().x();
                int y = animal.getPosition().y();
                Label cellLabel = cellLabels[y][x];
                cellLabel.setBorder(new Border(new BorderStroke(
                        Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4)
                )));
            }
        }

        if (trackedAnimal != null) {
            int x = trackedAnimal.getPosition().x();
            int y = trackedAnimal.getPosition().y();
            Label cellLabel = cellLabels[y][x];
            cellLabel.setGraphic(drawAnimal(trackedAnimal));
            cellLabel.setBorder(new Border(new BorderStroke(
                    Color.ORANGE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4)
            )));
            selectedCellLabel = cellLabel;
        }
    }

    private void clearGrid() {
        if(prevTrackedAnimal != null){
            Label cellLabel = cellLabels[prevTrackedAnimal.getPosition().y()][prevTrackedAnimal.getPosition().x()];
            cellLabel.setText(null);
            cellLabel.setGraphic(null);
            cellLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, null, new BorderWidths(0.4))));
        }

        if (prevOccupiedPositions != null) {
            for (Vector2d vec : prevOccupiedPositions) {
                Label cellLabel = cellLabels[vec.y()][vec.x()];
                cellLabel.setText(null);
                cellLabel.setGraphic(null);
                cellLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, null, new BorderWidths(0.4))));
            }
        }
    }

    private Circle drawAnimal(Animal animal) {
        double healthPercentage = (double) animal.getEnergy() /animal.getMaxEnergy();
        healthPercentage = Math.max(healthPercentage, 0);
        Circle redCircle = new Circle();
        redCircle.setRadius(CELL_SIZE * 0.3);
        redCircle.setFill(getColorForAnimal(healthPercentage));
        return redCircle;
    }

    private Color getColorForAnimal(double healthPercentage){
        return Color.hsb(360, 1, healthPercentage, 1);
    }

    public void drawMap(){
        fillMap();
        showTrackedAnimalStats();
    }

    @Override
    public void mapChanged(WorldMap map, String message) {
        Platform.runLater(() -> {
            drawMap();
            updateStats();
            updateChart();
        });
    }

    private void updateStats() {
        String[] currentStats = map.getCurrentStats();
        for(int i = 0; i < numberOfStats; i++){
            statValues[i].setText(currentStats[i]);
        }
    }

    public void onPauseResumeClicked(javafx.event.ActionEvent actionEvent){
        simulation.changeState();
        if(!simulation.getIsActive()){
            mostFrequentGenesAnimals = map.getCurrentMostFrequentGenotypeAnimalList();
        }
        else{
            mostFrequentGenesAnimals = null;
        }
        drawMap();
    }

    private void initializeChart() {
        lineChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        series1.setName("Animal amount");
        series2.setName("Plant amount");

        lineChart.setCreateSymbols(false);

        lineChart.getData().addAll(series1, series2);

        setSeriesColor(series1, Color.ORANGE);
        setSeriesColor(series2, Color.GREEN);

        setLegendColor(lineChart, Color.ORANGE, Color.GREEN);

        lineChartContainer.getChildren().add(lineChart);
    }

    private void setSeriesColor(XYChart.Series<Number, Number> series, Color color) {
        Node seriesNode = series.getNode().lookup(".chart-series-line");
        seriesNode.setStyle("-fx-stroke: " + color.toString().replace("0x", "#") + ";");
    }

    private void setLegendColor(LineChart<Number, Number> chart, Color... colors) {
        Legend legend = (Legend) chart.lookup(".chart-legend");
        if (legend != null) {
            Legend.LegendItem[] legendItems = legend.getItems().toArray(new Legend.LegendItem[0]);
            for (int i = 0; i < legendItems.length && i < colors.length; i++) {
                Node legendNode = legendItems[i].getSymbol();
                legendNode.setStyle("-fx-background-color: " + colors[i].toString().replace("0x", "#") + ";");
            }
        }
    }

    private void updateChart() {
        String[] stats = map.getCurrentStats();
        XYChart.Series<Number, Number> series1 = lineChart.getData().get(0);
        XYChart.Series<Number, Number> series2 = lineChart.getData().get(1);

        series1.getData().add(new XYChart.Data<>(Integer.parseInt(stats[0]), Integer.parseInt(stats[1])));
        series2.getData().add(new XYChart.Data<>(Integer.parseInt(stats[0]), Integer.parseInt(stats[2])));
    }
}
