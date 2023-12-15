package agh.ics.oop;

import javafx.application.Application;

public class World {
    public static void main(String[] args)
    {
        Application.launch(SimulationApp.class, args);
        System.out.println("System zakończył działanie.");

        /*GrassField map = new GrassField(10);
        String movementList = "0 4 0 0 0 0 0 0 0 0 0 0";
        String[] inputArray = movementList.split(" ");
        List<MoveDirection> directions = OptionsParser.parse(inputArray);
        MapChangeListener observer = new ConsoleMapDisplay();
        map.addObserver(observer);

        List<Vector2d> initialPositions = Arrays.asList(new Vector2d(0, 0), new Vector2d(0, 1));
        Simulation simulation = new Simulation(initialPositions, directions, map);
        simulation.run();*/
    }
}