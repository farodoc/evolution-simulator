package agh.ics.oop.model;

import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap{
    private final List<MapChangeListener> observers = new ArrayList<>();

    public void addObserver(MapChangeListener observer) {
        observers.add(observer);
    }

    public void removeObserver(MapChangeListener observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this, message);
        }
    }

    protected final Map<Vector2d, Animal> animals = new HashMap<>();
    protected final MapVisualizer mapVisualizer;
    protected final UUID id;

    public AbstractWorldMap(){
        id = UUID.randomUUID();
        mapVisualizer = new MapVisualizer(this);
    }

    @Override
    public UUID getId() {
        return id;
    }

    public void move(Animal animal, MoveDirection direction) {
        Vector2d oldPosition = animal.getPosition();
        MapDirection oldOrientation = animal.getOrientation();
        animal.move(direction, this);

        if(oldPosition != animal.getPosition()){
            animals.remove(oldPosition, animal);
            animals.put(animal.getPosition(), animal);
            notifyObservers("Animal moved from " + oldPosition + " to " + animal.getPosition());
        }
        else if(oldOrientation != animal.getOrientation()){
            notifyObservers("Animal changed direction from [" + oldOrientation + "] to [" + animal.getOrientation() + "]");
        }
        else{
            notifyObservers("No move has been made");
        }
    }

    public String toString(){
        Boundary boundaries = getCurrentBounds();
        return mapVisualizer.draw(boundaries.bottomLeftCorner(), boundaries.topRightCorner());
    }
    public boolean canMoveTo(Vector2d position) {
        return true;
    }

    public WorldElement objectAt(Vector2d position) {
        return animals.getOrDefault(position, null);
    }

    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position);
    }

    public void place(Animal animal) throws PositionAlreadyOccupiedException {
        if(canMoveTo(animal.getPosition())){
            animals.put(animal.getPosition(), animal);
            notifyObservers("Animal placed at " + animal.getPosition());
        }
        else throw new PositionAlreadyOccupiedException(animal.getPosition());
    }

    public ArrayList<WorldElement> getElements(){
        return new ArrayList<>(animals.values());
    }
}