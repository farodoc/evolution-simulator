package agh.ics.oop.model;

import java.util.UUID;

public interface WorldMap {
    public Vector2d getNewPositionForAnimal(Animal animal);

    void place(Animal animal);

    void move(Animal animal, int ANIMAL_ENERGY_PER_MOVE);

    boolean isOccupied(Vector2d position);

    WorldElement objectAt(Vector2d position);

    abstract Boundary getCurrentBounds();

    UUID getId();
}