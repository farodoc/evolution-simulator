package agh.ics.oop.model;

public class EquatorMap extends AbstractWorldMap{

    public EquatorMap(int FOOD_STARTING_AMOUNT) {
        super();
        generateTiles();
        generateFood(FOOD_STARTING_AMOUNT);
    }
}