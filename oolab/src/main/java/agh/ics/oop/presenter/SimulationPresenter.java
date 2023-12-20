package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationEngine;
import agh.ics.oop.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;

import java.awt.*;
import java.util.ArrayList;

import java.util.List;

public class SimulationPresenter implements MapChangeListener {
    AbstractWorldMap map;
    private int CELL_SIZE;

    @FXML
    private Label infoLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private GridPane mapGrid;

    public void setMap(AbstractWorldMap map) {
        this.map = map;
    }

    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0));
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    private void drawGrid(){
        clearGrid();
        Boundary boundaries = map.getCurrentBounds();
        int width = boundaries.topRightCorner().x() - boundaries.bottomLeftCorner().x() + 1;
        int height = boundaries.topRightCorner().y() - boundaries.bottomLeftCorner().y() + 1;

        for (int x = 0; x < width + 1; x++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));
        }

        for (int y = 0; y < height + 1; y++) {
            mapGrid.getRowConstraints().add(new RowConstraints(CELL_SIZE));
        }

        Label xyLabel = new Label("y/x");
        mapGrid.add(xyLabel, 0, 0);
        GridPane.setHalignment(xyLabel, HPos.CENTER);

        for (int x = 1; x < width + 1; x++) {
            Label columnLabel = new Label(String.valueOf(x - 1 + boundaries.bottomLeftCorner().x()));
            mapGrid.add(columnLabel, x, 0);
            GridPane.setHalignment(columnLabel, HPos.CENTER);
        }

        for (int y = height; y >= 1; y--) {
            Label rowLabel = new Label(String.valueOf(y - 1 + boundaries.bottomLeftCorner().y()));
            mapGrid.add(rowLabel, 0, height - y + 1);
            GridPane.setHalignment(rowLabel, HPos.CENTER);
        }
    }

    private void fillMap(){
        int grassSize = (int)(1.75*CELL_SIZE);
        Boundary boundaries = map.getCurrentBounds();
        int width = boundaries.topRightCorner().x() - boundaries.bottomLeftCorner().x() + 1;
        int height = boundaries.topRightCorner().y() - boundaries.bottomLeftCorner().y() + 1;
        TileType[][] tiles = map.getTiles();

        for (int y = height; y >= 1; y--) {
            for (int x = 1; x < width + 1; x++) {
                Label cellLabel = new Label();
                cellLabel.setMinWidth(CELL_SIZE);
                cellLabel.setMinHeight(CELL_SIZE);
                cellLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, null, new BorderWidths(0.4))));
                cellLabel.setStyle("-fx-alignment: CENTER;-fx-font-weight: bold;-fx-font-size: " + grassSize + "px;");
                Vector2d translatedPosition = new Vector2d(x - 1 + boundaries.bottomLeftCorner().x(), y - 1 + boundaries.bottomLeftCorner().y());

                if(tiles[translatedPosition.y()][translatedPosition.x()] == TileType.JUNG){
                    cellLabel.setBackground(new Background(new BackgroundFill(Color.rgb(18, 74, 13), CornerRadii.EMPTY, Insets.EMPTY)));
                }
                else{
                    cellLabel.setBackground(new Background(new BackgroundFill(Color.rgb(161, 92, 32), CornerRadii.EMPTY, Insets.EMPTY)));
                }

                Object objectAtPosition = map.objectAt(translatedPosition);
                if (objectAtPosition != null) {
                    if (objectAtPosition instanceof Animal) {
                        cellLabel.setGraphic(drawAnimal((Animal) objectAtPosition));
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

                GridPane.setHalignment(cellLabel, HPos.CENTER);
                mapGrid.add(cellLabel, x, height - y + 1);
            }
        }
    }

    private Circle drawAnimal(Animal animal) {
        double healthPercentage = (double) animal.getEnergy() /animal.getMaxEnergy();
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
        drawGrid();
        fillMap();
    }

    @Override
    public void mapChanged(WorldMap map, String message) {
        Platform.runLater(() -> {
            drawMap();
            if(message.startsWith("X"))
                descriptionLabel.setText(message);
        });
    }

    public void onSimulationStartClicked(javafx.event.ActionEvent actionEvent){
        CELL_SIZE = (int)(Screen.getPrimary().getVisualBounds().getHeight()/map.getMapHeight() * 0.8);
        Simulation simulation = new Simulation(map);
        List<Simulation> simulations = new ArrayList<>();
        simulations.add(simulation);

        infoLabel.setVisible(false);
        mapGrid.setManaged(true);
        mapGrid.setVisible(true);

        setMap(map);

        SimulationEngine engine = new SimulationEngine(simulations, 2);
        engine.runAsync();
    }
}