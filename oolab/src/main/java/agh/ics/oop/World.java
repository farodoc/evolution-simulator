package agh.ics.oop;

import agh.ics.oop.model.ConsoleMapDisplay;
import agh.ics.oop.model.DarvinMap;
import agh.ics.oop.model.MapChangeListener;
import javafx.application.Application;

public class World {
    public static void main(String[] args)
    {
        Application.launch(SimulationApp.class, args);
        System.out.println("System zakończył działanie.");


        /*DarvinMap map = new DarvinMap(10);
        MapChangeListener observer = new ConsoleMapDisplay();
        map.addObserver(observer);

        Simulation simulation = new Simulation(map);
        simulation.run();*/
    }
}