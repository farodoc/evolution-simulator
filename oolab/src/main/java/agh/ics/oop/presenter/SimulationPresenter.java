package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationEngine;
import agh.ics.oop.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SimulationPresenter implements MapChangeListener {
    @FXML
    private TextField MAP_WIDTH;
    @FXML
    private TextField MAP_HEIGHT;
    @FXML
    private ComboBox<String> mapComboBox;

    @FXML
    private TextField ANIMAL_STARTING_AMOUNT;
    @FXML
    private TextField ANIMAL_STARTING_ENERGY;
    @FXML
    private TextField ANIMAL_ENERGY_PER_MOVE;

    @FXML
    private TextField ANIMAL_MIN_ENERGY_TO_REPRODUCE;
    @FXML
    private TextField ANIMAL_ENERGY_TO_REPRODUCE_COST;

    @FXML
    private TextField ANIMAL_GENES_AMOUNT;
    @FXML
    private ComboBox<String> genesComboBox;

    @FXML
    private TextField ANIMAL_MIN_MUTATIONS;
    @FXML
    private TextField ANIMAL_MAX_MUTATIONS;

    @FXML
    private TextField FOOD_STARTING_AMOUNT;
    @FXML
    private TextField FOOD_GROWTH_PER_DAY;
    @FXML
    private TextField FOOD_ENERGY;


    @FXML
    private Label infoLabel;
    @FXML
    private GridPane mapGrid;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField CONFIG_NAME;

    @FXML
    private void initialize(){
        CONFIG_NAME.setText("Example config");
        MAP_WIDTH.setText("30");
        MAP_HEIGHT.setText("30");
        mapComboBox.setValue("Equator map");
        ANIMAL_STARTING_AMOUNT.setText("15");
        ANIMAL_STARTING_ENERGY.setText("50");
        ANIMAL_ENERGY_PER_MOVE.setText("5");
        ANIMAL_MIN_ENERGY_TO_REPRODUCE.setText("35");
        ANIMAL_ENERGY_TO_REPRODUCE_COST.setText("15");
        ANIMAL_GENES_AMOUNT.setText("10");
        ANIMAL_GENES_AMOUNT.setText("10");
        genesComboBox.setValue("Looped");
        ANIMAL_MIN_MUTATIONS.setText("1");
        ANIMAL_MAX_MUTATIONS.setText("5");
        FOOD_STARTING_AMOUNT.setText("20");
        FOOD_GROWTH_PER_DAY.setText("5");
        FOOD_ENERGY.setText("15");
    }

    int mapWidth;
    int mapHeight;
    String selectedMap;
    int animalStartingAmount;
    int animalStartingEnergy;
    int animalEnergyPerMove;
    int animalMinEnergyToReproduce;
    int animalEnergyToReproduceCost;
    int animalGenesAmount;
    String selectedGenes;
    int animalMinMutations;
    int animalMaxMutations;
    int foodStartingAmount;
    int foodGrowthPerDay;
    int foodEnergy;
    AbstractWorldMap map;
    private int CELL_SIZE;

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
        });
    }

    public void onSimulationLoadClicked(javafx.event.ActionEvent actionEvent) throws FileNotFoundException {
        String[] availableConfigs = SettingsHandler.getConfigNames();

        if (availableConfigs.length == 0) {
            errorLabel.setText("No configurations available.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(availableConfigs[0], availableConfigs);
        dialog.setTitle("Load Settings");
        dialog.setHeaderText("Select a configuration to load:");
        dialog.setContentText("Configuration:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(selectedConfig -> {
            String[] config;
            try {
                config = SettingsHandler.findConfig(selectedConfig);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            loadConfigIntoLabels(config);
            errorLabel.setText("Loaded config: " + selectedConfig);
        });
    }

    private void loadConfigIntoLabels(String[] config){
        if(!checkAndSetInputValues()){
            return;
        }
        CONFIG_NAME.setText(config[0]);
        MAP_WIDTH.setText(config[1]);
        MAP_HEIGHT.setText(config[2]);
        ANIMAL_STARTING_AMOUNT.setText(config[3]);
        ANIMAL_STARTING_ENERGY.setText(config[4]);
        ANIMAL_ENERGY_PER_MOVE.setText(config[5]);
        ANIMAL_MIN_ENERGY_TO_REPRODUCE.setText(config[6]);
        ANIMAL_ENERGY_TO_REPRODUCE_COST.setText(config[7]);
        ANIMAL_GENES_AMOUNT.setText(config[8]);
        ANIMAL_MIN_MUTATIONS.setText(config[9]);
        ANIMAL_MAX_MUTATIONS.setText(config[10]);
        FOOD_STARTING_AMOUNT.setText(config[11]);
        FOOD_GROWTH_PER_DAY.setText(config[12]);
        FOOD_ENERGY.setText(config[13]);
        mapComboBox.setValue(config[14]);
        genesComboBox.setValue(config[15]);
    }

    public void onSimulationSaveClicked(javafx.event.ActionEvent actionEvent) throws Exception {
        Settings settings;
        try {
            checkAndSetInputValues();
            String[] attributesArray = {
                    CONFIG_NAME.getText(),
                    String.valueOf(mapWidth),
                    String.valueOf(mapHeight),
                    String.valueOf(animalStartingAmount),
                    String.valueOf(animalStartingEnergy),
                    String.valueOf(animalEnergyPerMove),
                    String.valueOf(animalMinEnergyToReproduce),
                    String.valueOf(animalEnergyToReproduceCost),
                    String.valueOf(animalGenesAmount),
                    String.valueOf(animalMinMutations),
                    String.valueOf(animalMaxMutations),
                    String.valueOf(foodStartingAmount),
                    String.valueOf(foodGrowthPerDay),
                    String.valueOf(foodEnergy),
                    mapComboBox.getValue(),
                    genesComboBox.getValue()
            };
            settings = new Settings(attributesArray);
        } catch (Exception e) {
            errorLabel.setText("Bledne wartosci! Wprowadz jeszcze raz.");
            return;
        }

        try {
            if(settings.getName().isEmpty()){
                errorLabel.setText("Jesli chcesz zapisac config to musisz nadac mu nazwe.");
                return;
            }
            if(SettingsHandler.findConfig(settings.getName()) != null){
                errorLabel.setText("Config o takiej nazwie juz istnieje! Podaj inna nazwe.");
                return;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        SettingsHandler.add(settings.getAttributesAsArray());
        errorLabel.setText("Config o nazwie \"" + settings.getName() + "\" zostal zapisany poprawnie!");
    }

    public void onSimulationStartClicked(javafx.event.ActionEvent actionEvent){
        if(!checkAndSetInputValues()){
            infoLabel.setText("Wpisano bledne dane, wprowadz je ponownie!");
            return;
        }

        AbstractWorldMap map;
        if(Objects.equals(selectedMap, "Poison map")){
            map = new PoisonMap(foodStartingAmount, mapWidth, mapHeight);
        }
        else{
            map = new EquatorMap(foodStartingAmount, mapWidth, mapHeight);
        }

        map.addObserver(this);
        setMap(map);

        CELL_SIZE = Math.min(
                (int)(Screen.getPrimary().getVisualBounds().getHeight()/(map.getMapHeight()+3)),
                (int)(Screen.getPrimary().getVisualBounds().getWidth()/(map.getMapWidth()+3)));

        Simulation simulation = new Simulation(this.map, animalStartingAmount,
                animalStartingEnergy, animalEnergyPerMove,
                animalMinEnergyToReproduce, animalEnergyToReproduceCost,
                animalGenesAmount, !selectedGenes.equals("Default"),
                animalMinMutations, animalMaxMutations,
                foodGrowthPerDay, foodEnergy);

        List<Simulation> simulations = new ArrayList<>();
        simulations.add(simulation);

        mapGrid.setManaged(true);
        mapGrid.setVisible(true);

        setMap(map);

        SimulationEngine engine = new SimulationEngine(simulations, 4);
        engine.runAsync();
    }

    private boolean checkAndSetInputValues(){
        try {
            mapWidth = Integer.parseInt(MAP_WIDTH.getText());
            mapHeight = Integer.parseInt(MAP_HEIGHT.getText());
            selectedMap = mapComboBox.getValue();
            animalStartingAmount = Integer.parseInt(ANIMAL_STARTING_AMOUNT.getText());
            animalStartingEnergy = Integer.parseInt(ANIMAL_STARTING_ENERGY.getText());
            animalEnergyPerMove = Integer.parseInt(ANIMAL_ENERGY_PER_MOVE.getText());
            animalMinEnergyToReproduce = Integer.parseInt(ANIMAL_MIN_ENERGY_TO_REPRODUCE.getText());
            animalEnergyToReproduceCost = Integer.parseInt(ANIMAL_ENERGY_TO_REPRODUCE_COST.getText());
            animalGenesAmount = Integer.parseInt(ANIMAL_GENES_AMOUNT.getText());
            selectedGenes = genesComboBox.getValue();
            animalMinMutations = Integer.parseInt(ANIMAL_MIN_MUTATIONS.getText());
            animalMaxMutations = Integer.parseInt(ANIMAL_MAX_MUTATIONS.getText());
            foodStartingAmount = Integer.parseInt(FOOD_STARTING_AMOUNT.getText());
            foodGrowthPerDay = Integer.parseInt(FOOD_GROWTH_PER_DAY.getText());
            foodEnergy = Integer.parseInt(FOOD_ENERGY.getText());

            if (mapWidth <= 0 || mapHeight <= 0 || animalStartingAmount<=0 || animalStartingEnergy<=0 || animalEnergyPerMove<0 ||
               animalMinEnergyToReproduce<=0 || animalEnergyToReproduceCost<=0 || animalGenesAmount<=0 || animalMinMutations<0 ||
               animalMaxMutations<0 || foodStartingAmount<0 || foodGrowthPerDay<0 || foodEnergy<0){
                System.out.println("Blad: wartosci <=0");
                return false;
            }
            if(animalEnergyToReproduceCost > animalMinEnergyToReproduce){
                System.out.println("Blad wartosci kosztu oraz minima energii do reprodukcji");
                return false;
            }
            if(animalMinMutations>animalMaxMutations || animalMaxMutations>animalGenesAmount){
                System.out.println("Blad wartosci minimalnych/maksymalnych mutacji");
                return false;
            }

            System.out.println("Wprowadzone wartosci są poprawne!");
            return true;

        } catch (NumberFormatException e) {
            System.out.println("Blad: Wprowadzone wartosci musza bycć liczbami naturalnymi!");
            return false;
        }
    }
}