package agh.ics.oop.presenter;

import agh.ics.oop.OptionsParser;
import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationEngine;
import agh.ics.oop.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

public class SimulationPresenter implements MapChangeListener {
    DarvinsMap map;
    private static final int CELL_SIZE = 40;

    @FXML
    private Label infoLabel;

    @FXML
    private TextField movementTextField;

    @FXML
    private Label descriptionLabel;

    @FXML
    private GridPane mapGrid;

    public void setMap(DarvinsMap map) {
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
        int width = boundaries.topRightCorner().getX() - boundaries.bottomLeftCorner().getX() + 1;
        int height = boundaries.topRightCorner().getY() - boundaries.bottomLeftCorner().getY() + 1;

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
            Label columnLabel = new Label(String.valueOf(x - 1 + boundaries.bottomLeftCorner().getX()));
            mapGrid.add(columnLabel, x, 0);
            GridPane.setHalignment(columnLabel, HPos.CENTER);
        }

        for (int y = height; y >= 1; y--) {
            Label rowLabel = new Label(String.valueOf(y - 1 + boundaries.bottomLeftCorner().getY()));
            mapGrid.add(rowLabel, 0, height - y + 1);
            GridPane.setHalignment(rowLabel, HPos.CENTER);
        }
    }

    private void fillMap(){
        Boundary boundaries = map.getCurrentBounds();
        int width = boundaries.topRightCorner().getX() - boundaries.bottomLeftCorner().getX() + 1;
        int height = boundaries.topRightCorner().getY() - boundaries.bottomLeftCorner().getY() + 1;
        TileType[][] tiles = map.getTiles();

        for (int y = height; y >= 1; y--) {
            for (int x = 1; x < width + 1; x++) {
                Label cellLabel = new Label();
                cellLabel.setMinWidth(CELL_SIZE);
                cellLabel.setMinHeight(CELL_SIZE);
                cellLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(0.4))));
                cellLabel.setStyle("-fx-alignment: CENTER;-fx-font-weight: bold;-fx-font-size: 30px;");
                Vector2d translatedPosition = new Vector2d(x - 1 + boundaries.bottomLeftCorner().getX(), y - 1 + boundaries.bottomLeftCorner().getY());

                if(tiles[translatedPosition.getY()][translatedPosition.getX()] == TileType.JUNG){
                    cellLabel.setBackground(new Background(new BackgroundFill(Color.rgb(18, 74, 13), CornerRadii.EMPTY, Insets.EMPTY)));
                }
                else{
                    cellLabel.setBackground(new Background(new BackgroundFill(Color.rgb(135, 65, 4), CornerRadii.EMPTY, Insets.EMPTY)));
                }

                if (map.objectAt(translatedPosition) != null) {
                    cellLabel.setText(map.objectAt(translatedPosition).toString());
                }

                GridPane.setHalignment(cellLabel, HPos.CENTER);
                mapGrid.add(cellLabel, x, height - y + 1);
            }
        }
    }

    public void drawMap(){
        drawGrid();
        fillMap();
    }

    @Override
    public void mapChanged(WorldMap map, String message) {
        Platform.runLater(() -> {
            drawMap();
            descriptionLabel.setText(message);
        });
    }

    public void onSimulationStartClicked(javafx.event.ActionEvent actionEvent){
        String movementList = movementTextField.getText();
        String[] inputArray = movementList.split(" ");
        List<MoveDirection> directions = OptionsParser.parse(inputArray);

        List<Vector2d> initialPositions = Arrays.asList(new Vector2d(0, 0), new Vector2d(0, 3));
        Simulation simulation = new Simulation(initialPositions, directions, map);
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