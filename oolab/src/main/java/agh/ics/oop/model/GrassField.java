package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.sqrt;

public class GrassField extends AbstractWorldMap{
    private int mapSize = 10;
    private int grassNumber;
    public GrassField(int grassNumber) {
        super();
        this.grassNumber = grassNumber;
        generateGrassTiles();
    }

    private final Map<Vector2d, Grass> grassTiles = new HashMap<>();
    private Vector2d BOTTOM_LEFT_MAP_BORDER = new Vector2d(0,0);
    private Vector2d TOP_RIGHT_MAP_BORDER = new Vector2d(mapSize - 1,mapSize - 1);

    private void generateGrassTiles(){
        int cords = (int) sqrt(grassNumber * 10);
        int cnt = 0;
        while(cnt < grassNumber){
            int x = (int) (Math.random() * cords);
            int y = (int) (Math.random() * cords);
            Vector2d newGrassPosition = new Vector2d(x,y);
            Grass newGrass = new Grass(newGrassPosition);
            if(!grassTiles.containsKey(newGrassPosition)){
                grassTiles.put(newGrassPosition,newGrass);
                cnt++;
            }
        }
    }

    Map<Vector2d, Animal> getAnimals() {
        return Collections.unmodifiableMap(animals);
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return super.isOccupied(position) || grassTiles.containsKey(position);
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        if(super.objectAt(position) != null){
            return super.objectAt(position);
        }
        return grassTiles.getOrDefault(position, null);
    }

    @Override
    public ArrayList<WorldElement> getElements(){
        ArrayList<WorldElement> result = super.getElements();
        result.addAll(grassTiles.values());
        return result;
    }

    @Override
    public Boundary getCurrentBounds(){
        return new Boundary(BOTTOM_LEFT_MAP_BORDER, TOP_RIGHT_MAP_BORDER);
    }
}