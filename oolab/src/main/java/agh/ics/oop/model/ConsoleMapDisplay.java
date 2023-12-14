package agh.ics.oop.model;

public class ConsoleMapDisplay implements MapChangeListener{
    private int totalUpdates = 0;

    @Override
    public synchronized void mapChanged(WorldMap map, String message) {
        System.out.println("Update #" + (++totalUpdates) + ": " + message);
        System.out.println("Map ID: " + map.getId() + "\n");
        System.out.println(map.toString());
    }
}