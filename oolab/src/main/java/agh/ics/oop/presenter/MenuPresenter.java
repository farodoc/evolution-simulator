package agh.ics.oop.presenter;

import agh.ics.oop.SimulationEngine;
import agh.ics.oop.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Optional;

public class MenuPresenter{
    @FXML
    private TextField MAP_WIDTH, MAP_HEIGHT, ANIMAL_STARTING_AMOUNT, ANIMAL_STARTING_ENERGY, ANIMAL_ENERGY_PER_MOVE,
    ANIMAL_MIN_ENERGY_TO_REPRODUCE, ANIMAL_ENERGY_TO_REPRODUCE_COST, ANIMAL_GENES_AMOUNT, ANIMAL_MIN_MUTATIONS,
    ANIMAL_MAX_MUTATIONS, FOOD_STARTING_AMOUNT, FOOD_GROWTH_PER_DAY, FOOD_ENERGY, CONFIG_NAME, REFRESH_TIME; // zastanowiłbym się nad tymi wielkimi literami

    @FXML
    private ComboBox<String> mapComboBox, genesComboBox;

    @FXML
    private Label errorLabel;

    @FXML
    private CheckBox SAVE_STATS;

    @FXML
    private void initialize(){
        CONFIG_NAME.setText("Example config");
        MAP_WIDTH.setText("20");
        MAP_HEIGHT.setText("20");
        mapComboBox.setValue("Poison map");
        ANIMAL_STARTING_AMOUNT.setText("30");
        ANIMAL_STARTING_ENERGY.setText("100");
        ANIMAL_ENERGY_PER_MOVE.setText("5");
        ANIMAL_MIN_ENERGY_TO_REPRODUCE.setText("30");
        ANIMAL_ENERGY_TO_REPRODUCE_COST.setText("10");
        ANIMAL_GENES_AMOUNT.setText("10");
        genesComboBox.setValue("Looped");
        ANIMAL_MIN_MUTATIONS.setText("1");
        ANIMAL_MAX_MUTATIONS.setText("5");
        FOOD_STARTING_AMOUNT.setText("50");
        FOOD_GROWTH_PER_DAY.setText("15");
        FOOD_ENERGY.setText("25");
        REFRESH_TIME.setText("1000");
    }

    int mapWidth, mapHeight, animalStartingAmount, animalStartingEnergy, animalEnergyPerMove, animalMinEnergyToReproduce,
    animalEnergyToReproduceCost, animalGenesAmount, animalMinMutations, animalMaxMutations, foodStartingAmount,
    foodGrowthPerDay, foodEnergy, refreshTime;

    boolean saveStats;

    String selectedMap, selectedGenes; // String?

    public void onSimulationStartClicked(javafx.event.ActionEvent actionEvent) {
        if (!checkAndSetInputValues()) return;

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
                genesComboBox.getValue(),
                String.valueOf(refreshTime),
                String.valueOf(saveStats)
        };

        AbstractWorldMap map;
        if (Objects.equals(selectedMap, "Poison map")) {
            map = new PoisonMap(foodStartingAmount, mapWidth, mapHeight); // czy to dobrze, że presenter instancjonuje mapę?
        } else {
            map = new EquatorMap(foodStartingAmount, mapWidth, mapHeight);
        }

        Settings settings = new Settings(attributesArray, map);

        SimulationEngine simulationEngine = new SimulationEngine();
        simulationEngine.start(settings);
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
                    genesComboBox.getValue(),
                    String.valueOf(refreshTime),
                    String.valueOf(saveStats)
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
            refreshTime = Integer.parseInt(REFRESH_TIME.getText());
            saveStats = SAVE_STATS.isSelected();

            if (mapWidth <= 0 || mapHeight <= 0 || animalStartingAmount <= 0 || animalStartingEnergy <= 0 || animalEnergyPerMove < 0 ||
                    animalMinEnergyToReproduce <= 0 || animalEnergyToReproduceCost <= 0 || animalGenesAmount <= 0 || animalMinMutations < 0 ||
                    animalMaxMutations < 0 || foodStartingAmount < 0 || foodGrowthPerDay < 0 || foodEnergy < 0 || refreshTime < 0){
                errorLabel.setText("Error: values <= 0.");
                return false;
            }
            if(animalEnergyToReproduceCost > animalMinEnergyToReproduce){
                errorLabel.setText("Error: animalMinEnergyToReproduce < animalEnergyToReproduceCost.");
                return false;
            }
            if(animalMinMutations>animalMaxMutations || animalMaxMutations>animalGenesAmount){
                errorLabel.setText("Error: wrong min/max mutations amount.");
                return false;
            }

            errorLabel.setText("Input values are correct!");
            return true;

        } catch (NumberFormatException e) {
            errorLabel.setText("Error: input values have to be numbers!");
            return false;
        }
    }
}