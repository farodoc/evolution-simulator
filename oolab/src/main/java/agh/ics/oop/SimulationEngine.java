package agh.ics.oop;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.Settings;
import agh.ics.oop.SimulationLauncher;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulationEngine {
    public void start(Settings settings) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        SimulationLauncher simulationLauncher = new SimulationLauncher();

        Stage stage = new Stage();
        stage.setOnCloseRequest(event -> {
            executorService.shutdownNow();
        });

        Simulation simulation = new Simulation(settings);

        simulationLauncher.run(stage, simulation);

        executorService.execute(simulation);
    }

}