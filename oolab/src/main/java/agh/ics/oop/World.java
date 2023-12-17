package agh.ics.oop;

import agh.ics.oop.model.ConsoleMapDisplay;
import agh.ics.oop.model.DarvinsMap;
import agh.ics.oop.model.MapChangeListener;
import agh.ics.oop.model.Vector2d;
import javafx.application.Application;

import java.util.Arrays;
import java.util.List;

public class World {
    public static void main(String[] args)
    {
        //Application.launch(SimulationApp.class, args);
        //System.out.println("System zakończył działanie.");

        DarvinsMap map = new DarvinsMap(10);
        MapChangeListener observer = new ConsoleMapDisplay();
        map.addObserver(observer);

        List<Vector2d> initialPositions = Arrays.asList(new Vector2d(10, 10));
        Simulation simulation = new Simulation(initialPositions, map);
        simulation.run();
    }
}