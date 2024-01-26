package agh.ics.oop;

import agh.ics.oop.presenter.SimulationPresenter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SimulationLauncher extends Application {
    private Simulation simulation;
    public void run(Stage primaryStage, Simulation simulation) {
        this.simulation = simulation;
        start(primaryStage);
    }
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            SimulationPresenter presenter = loader.getController();
            presenter.initialize(simulation);
            simulation.subscribe(presenter);

            primaryStage.setTitle("Simulation");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e) { // ?
            System.out.println(e);
        }
    }
}