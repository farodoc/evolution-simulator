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
    private TextField fxmlMapWidth, fxmlMapHeight, fxmlAnimalStartingAmount, fxmlAnimalStartingEnergy, fxmlAnimalEnergyPerMove,
            fxmlAnimalMinEnergyToReproduce, fxmlAnimalEnergyToReproduce, fxmlAnimalGenesAmount, fxmlAnimalMinMutations,
            fxmlAnimalMaxMutations, fxmlFoodStartingAmount, fxmlFoodGrowthPerDay, fxmlFoodEnergy, fxmlConfigName, fxmlRefreshTime;

    @FXML
    private ComboBox<String> mapComboBox, genesComboBox;

    @FXML
    private Label errorLabel;

    @FXML
    private CheckBox fxmlSaveStats;

    @FXML
    private void initialize(){
        fxmlConfigName.setText("Example config");
        fxmlMapWidth.setText("20");
        fxmlMapHeight.setText("20");
        mapComboBox.setValue("Poison map");
        fxmlAnimalStartingAmount.setText("30");
        fxmlAnimalStartingEnergy.setText("100");
        fxmlAnimalEnergyPerMove.setText("5");
        fxmlAnimalMinEnergyToReproduce.setText("30");
        fxmlAnimalEnergyToReproduce.setText("10");
        fxmlAnimalGenesAmount.setText("10");
        genesComboBox.setValue("Looped");
        fxmlAnimalMinMutations.setText("1");
        fxmlAnimalMaxMutations.setText("5");
        fxmlFoodStartingAmount.setText("50");
        fxmlFoodGrowthPerDay.setText("15");
        fxmlFoodEnergy.setText("25");
        fxmlRefreshTime.setText("1000");
    }

    int mapWidth, mapHeight, animalStartingAmount, animalStartingEnergy, animalEnergyPerMove, animalMinEnergyToReproduce,
    animalEnergyToReproduceCost, animalGenesAmount, animalMinMutations, animalMaxMutations, foodStartingAmount,
    foodGrowthPerDay, foodEnergy, refreshTime;

    boolean saveStats;

    String selectedMap, selectedGenes; // String?

    public void onSimulationStartClicked(javafx.event.ActionEvent actionEvent) {
        if (!checkAndSetInputValues()) return;

        String[] attributesArray = {
                fxmlConfigName.getText(),
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
        fxmlConfigName.setText(config[0]);
        fxmlMapWidth.setText(config[1]);
        fxmlMapHeight.setText(config[2]);
        fxmlAnimalStartingAmount.setText(config[3]);
        fxmlAnimalStartingEnergy.setText(config[4]);
        fxmlAnimalEnergyPerMove.setText(config[5]);
        fxmlAnimalMinEnergyToReproduce.setText(config[6]);
        fxmlAnimalEnergyToReproduce.setText(config[7]);
        fxmlAnimalGenesAmount.setText(config[8]);
        fxmlAnimalMinMutations.setText(config[9]);
        fxmlAnimalMaxMutations.setText(config[10]);
        fxmlFoodStartingAmount.setText(config[11]);
        fxmlFoodGrowthPerDay.setText(config[12]);
        fxmlFoodEnergy.setText(config[13]);
        mapComboBox.setValue(config[14]);
        genesComboBox.setValue(config[15]);
    }

    public void onSimulationSaveClicked(javafx.event.ActionEvent actionEvent) throws Exception {
        Settings settings;
        try {
            checkAndSetInputValues();
            String[] attributesArray = {
                    fxmlConfigName.getText(),
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
            mapWidth = Integer.parseInt(fxmlMapWidth.getText());
            mapHeight = Integer.parseInt(fxmlMapHeight.getText());
            selectedMap = mapComboBox.getValue();
            animalStartingAmount = Integer.parseInt(fxmlAnimalStartingAmount.getText());
            animalStartingEnergy = Integer.parseInt(fxmlAnimalStartingEnergy.getText());
            animalEnergyPerMove = Integer.parseInt(fxmlAnimalEnergyPerMove.getText());
            animalMinEnergyToReproduce = Integer.parseInt(fxmlAnimalMinEnergyToReproduce.getText());
            animalEnergyToReproduceCost = Integer.parseInt(fxmlAnimalEnergyToReproduce.getText());
            animalGenesAmount = Integer.parseInt(fxmlAnimalGenesAmount.getText());
            selectedGenes = genesComboBox.getValue();
            animalMinMutations = Integer.parseInt(fxmlAnimalMinMutations.getText());
            animalMaxMutations = Integer.parseInt(fxmlAnimalMaxMutations.getText());
            foodStartingAmount = Integer.parseInt(fxmlFoodStartingAmount.getText());
            foodGrowthPerDay = Integer.parseInt(fxmlFoodGrowthPerDay.getText());
            foodEnergy = Integer.parseInt(fxmlFoodEnergy.getText());
            refreshTime = Integer.parseInt(fxmlRefreshTime.getText());
            saveStats = fxmlSaveStats.isSelected();

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