package agh.ics.oop;

import agh.ics.oop.model.*;
import agh.ics.oop.presenter.SimulationPresenter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SimulationApp extends Application {
    private static final int FOOD_STARTING_AMOUNT = 20;
    private static final boolean EQUATOR_MAP_ACTIVE = false;
    @Override
    public void start(Stage primaryStage) throws IOException {
        MapChangeListener observer = new ConsoleMapDisplay();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
        BorderPane viewRoot = loader.load();
        SimulationPresenter presenter = loader.getController();

        AbstractWorldMap map;
        if(EQUATOR_MAP_ACTIVE) map = new EquatorMap(FOOD_STARTING_AMOUNT);
        else                   map = new PoisonMap(FOOD_STARTING_AMOUNT);

        presenter.setMap(map);

        map.addObserver(presenter);
        map.addObserver(observer);

        configureStage(primaryStage,viewRoot);
        getParameters().getRaw();
        primaryStage.show();
    }

    private void configureStage(Stage primaryStage, BorderPane viewRoot) {
        var scene = new Scene(viewRoot);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation app");
        primaryStage.minWidthProperty().bind(viewRoot.minWidthProperty());
        primaryStage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }
}
