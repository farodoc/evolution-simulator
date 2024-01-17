package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
    private Label errorLabel;
    @FXML
    private TextField CONFIG_NAME;

    @FXML
    private void initialize(){
        CONFIG_NAME.setText("Example config");
        MAP_WIDTH.setText("5");
        MAP_HEIGHT.setText("5");
        mapComboBox.setValue("Equator map");
        ANIMAL_STARTING_AMOUNT.setText("2");
        ANIMAL_STARTING_ENERGY.setText("10");
        ANIMAL_ENERGY_PER_MOVE.setText("1");
        ANIMAL_MIN_ENERGY_TO_REPRODUCE.setText("35");
        ANIMAL_ENERGY_TO_REPRODUCE_COST.setText("15");
        ANIMAL_GENES_AMOUNT.setText("10");
        ANIMAL_GENES_AMOUNT.setText("10");
        genesComboBox.setValue("Looped");
        ANIMAL_MIN_MUTATIONS.setText("1");
        ANIMAL_MAX_MUTATIONS.setText("5");
        FOOD_STARTING_AMOUNT.setText("3");
        FOOD_GROWTH_PER_DAY.setText("1");
        FOOD_ENERGY.setText("1");
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
    private GridPane simulationGrid;
    private Label[][] cellLabels;

    private Set<Vector2d> prevOccupiedPositions;

    private Label selectedCellLabel;
    private Animal trackedAnimal;

    public void setMap(AbstractWorldMap map) {
        this.map = map;
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
                    int clickedY = mapHeight - GridPane.getRowIndex(cellLabel) - 1;

                    if (map.objectAt(new Vector2d(clickedX, clickedY)) instanceof Animal) {
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

    private void handleAnimalClick(Vector2d position) {
        WorldElement objectAtPosition = map.objectAt(position);

        if (objectAtPosition instanceof Animal) {
            Animal clickedAnimal = (Animal) objectAtPosition;

            if (trackedAnimal != null && trackedAnimal.equals(clickedAnimal)) {
                untrackAnimal();
            } else {
                trackAnimal(position);
            }
            Platform.runLater(this::drawMap);
        }
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

            if (objectAtPosition != null) {
                if (trackedAnimal != null && trackedAnimal.getPosition() == vec && trackedAnimal.getEnergy() <= 0){
                    untrackAnimal();
                }

                if (objectAtPosition instanceof Animal) {
                    Animal animal = (Animal) objectAtPosition;
                    if(trackedAnimal != null && animal.getPosition() == trackedAnimal.getPosition()){
                        continue;
                    }

                    if(animal.getEnergy() > 0){
                        cellLabel.setGraphic(drawAnimal(animal));
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
    }

    @Override
    public void mapChanged(WorldMap map, String message) {
        Platform.runLater(() -> {
            drawMap();
            updateStats();
        });
    }

    private Label[] statValues;
    private final int numberOfStats = 8;

    private void updateStats() {
        String[] currentStats = map.getCurrentStats();
        for(int i = 0; i < numberOfStats; i++){
            statValues[i].setText(currentStats[i]);
        }
    }

    public void onSimulationStartClicked(javafx.event.ActionEvent actionEvent) {
        if (!checkAndSetInputValues()) {
            infoLabel.setText("Wrong input values!");
            return;
        }

        Platform.runLater(() -> {
            AbstractWorldMap map;
            if (Objects.equals(selectedMap, "Poison map")) {
                map = new PoisonMap(foodStartingAmount, mapWidth, mapHeight);
            } else {
                map = new EquatorMap(foodStartingAmount, mapWidth, mapHeight);
            }

            map.addObserver(this);
            setMap(map);

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

            Settings settings;
            try {
                settings = new Settings(attributesArray, map);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }

            Simulation simulation = new Simulation(settings);



            Stage simulationStage = new Stage();
            simulationStage.initStyle(StageStyle.DECORATED);
            simulationStage.initModality(Modality.NONE);
            simulationStage.setTitle("Simulation");

            simulationGrid = new GridPane();
            simulationStage.setScene(new Scene(simulationGrid, 1200, 1000));
            simulationStage.setOnCloseRequest(event -> {
                simulationStage.close();
            });
            simulationStage.show();

            Button pauseResumeButton = new Button("Pause/Resume");
            pauseResumeButton.setOnAction(event -> {
                if (simulation != null) {
                    if (simulation.getIsPaused()) {
                        simulation.resumeSimulation();
                    } else {
                        simulation.pauseSimulation();
                    }
                }
            });

            HBox buttonContainer = new HBox(pauseResumeButton);
            buttonContainer.setPadding(new Insets(10));
            buttonContainer.setAlignment(Pos.CENTER);

            VBox statisticsPane = new VBox(10);
            statisticsPane.setPadding(new Insets(10));

            statValues = new Label[numberOfStats];
            String[] statNames = {"Day","Current animal amount",
                    "Current plants amount",
                    "Free tiles amount",
                    "Most popular genotype",
                    "Average energy level",
                    "Average lifespan of dead animals",
                    "Average children amount"};

            for (int i = 0; i < numberOfStats; i++) {
                HBox statBox = new HBox(10);
                statValues[i] = new Label("N/A");
                Label statLabel = new Label(statNames[i] + ":");
                statBox.getChildren().addAll(statLabel, statValues[i]);
                statisticsPane.getChildren().add(statBox);
            }

            HBox mainContainer = new HBox(simulationGrid, buttonContainer, statisticsPane);

            mainContainer.setPrefWidth(1600);

            Scene scene = new Scene(mainContainer, 1600, 1000);

            simulationStage.setScene(scene);

            CELL_SIZE = Math.min(
                    (int) (Screen.getPrimary().getVisualBounds().getHeight() / (map.getMapHeight()) * 0.5),
                    (int) (Screen.getPrimary().getVisualBounds().getWidth() / (map.getMapWidth()) * 0.5));


            simulationGrid.setManaged(true);
            simulationGrid.setVisible(true);

            cellLabels = new Label[mapHeight][mapWidth];
            drawGrid();

            new Thread(simulation).start();
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
            errorLabel.setText("Wrong input values!");
            return;
        }

        try {
            if(settings.getName().isEmpty()){
                errorLabel.setText("In order to save this config give it a unique name.");
                return;
            }
            if(SettingsHandler.findConfig(settings.getName()) != null){
                errorLabel.setText("Config with that name already exists!");
                return;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        SettingsHandler.add(settings.getAttributesAsArray());
        errorLabel.setText("Config \"" + settings.getName() + "\" has been saved!");
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

            if (mapWidth <= 0 || mapHeight <= 0 || animalStartingAmount <= 0 || animalStartingEnergy <= 0 || animalEnergyPerMove < 0 ||
                    animalMinEnergyToReproduce <= 0 || animalEnergyToReproduceCost <= 0 || animalGenesAmount <= 0 || animalMinMutations < 0 ||
                    animalMaxMutations < 0 || foodStartingAmount < 0 || foodGrowthPerDay < 0 || foodEnergy < 0){
                System.out.println("Error: values <= 0.");
                return false;
            }
            if(animalEnergyToReproduceCost > animalMinEnergyToReproduce){
                System.out.println("Error: animalMinEnergyToReproduce < animalEnergyToReproduceCost.");
                return false;
            }
            if(animalMinMutations>animalMaxMutations || animalMaxMutations>animalGenesAmount){
                System.out.println("Error: wrong min/max mutations amount.");
                return false;
            }

            System.out.println("Input values are correct!");
            return true;

        } catch (NumberFormatException e) {
            System.out.println("Error: input values have to be numbers!");
            return false;
        }
    }
}