package agh.ics.oop.model;

public class EquatorMap extends AbstractWorldMap{

    public EquatorMap(int FOOD_STARTING_AMOUNT, int mapWidth, int mapHeight) {
        super(mapWidth, mapHeight);
        generateTiles();
        generateFood(FOOD_STARTING_AMOUNT);
    }
}