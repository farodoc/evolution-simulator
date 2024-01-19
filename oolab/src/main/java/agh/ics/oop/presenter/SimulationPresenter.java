package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
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

    private Simulation simulation;
    private List<Integer> mostFrequentGenes = null;
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

    public void initialize(Simulation simulation){
        this.simulation = simulation;
        this.map = simulation.getMap();
        CELL_SIZE = Math.min(
                600 / (map.getMapHeight()),
                600 / (map.getMapWidth()));

        cellLabels = new Label[map.getMapHeight()][map.getMapWidth()];

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
                    cellLabel.setBackground(new Background(new BackgroundFill(Color.rgb(161, 92, 32), CornerRadii.EMPTY, Insets.EMPTY)));
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
                            Color.ORANGE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)
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
            //tu jest problem z podswietlaniem najsilniejszego genu
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
                        if(animal.getGenes().getGenesList().equals(mostFrequentGenes)){
                            System.out.println(animal.getPosition());
                            cellLabel.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                        }
                    }
                }
                else {
                    if (objectAtPosition instanceof Grass){
                        cellLabel.setText(objectAtPosition.toString());
                        cellLabel.setTextFill(Color.GREEN);
                        cellLabel.setStyle("-fx-alignment: CENTER;-fx-font-weight: bold;-fx-font-size: " + grassSize + "px;");
                    }
                    else{
                        cellLabel.setText("?");
                        cellLabel.setTextFill(Color.rgb(149, 31, 255));
                        cellLabel.setStyle("-fx-alignment: CENTER;-fx-font-weight: bold;-fx-font-size: " + grassSize/1.5 + "px;");
                    }
                }
            }
        }

        if (trackedAnimal != null) {
            int x = trackedAnimal.getPosition().x();
            int y = trackedAnimal.getPosition().y();
            Label cellLabel = cellLabels[y][x];
            cellLabel.setGraphic(drawAnimal(trackedAnimal));
            cellLabel.setBorder(new Border(new BorderStroke(
                    Color.ORANGE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)
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
        double hue = 1;
        double brightness = 0.9;
        double opacity = 1.0;

        return Color.hsb(hue * 360, healthPercentage, brightness, opacity);
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
            mostFrequentGenes = map.getCurrentMostFrequentGenotype();
            System.out.println(mostFrequentGenes);
        }
        else{
            mostFrequentGenes = null;
        }
        drawMap();
    }
}
