package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.sqrt;

public class GrassField extends AbstractWorldMap{
    private final int grassNumber;
    public GrassField(int grassNumber) {
        super();
        this.grassNumber = grassNumber;
        generateGrassTiles();
    }

    private final Map<Vector2d, Grass> grassTiles = new HashMap<>();
    private Vector2d DYNAMIC_BOTTOM_LEFT_BORDER = new Vector2d(Integer.MAX_VALUE,Integer.MAX_VALUE);
    private Vector2d DYNAMIC_TOP_RIGHT_BORDER = new Vector2d(Integer.MIN_VALUE,Integer.MIN_VALUE);
    private Vector2d BOTTOM_LEFT_GRASS;
    private Vector2d TOP_RIGHT_GRASS;

    private void generateGrassTiles(){
        int cords = (int) sqrt(grassNumber * 10);
        int cnt = 0;
        while(cnt < grassNumber){
            int x = (int) (Math.random() * cords);
            int y = (int) (Math.random() * cords);
            Vector2d newGrassPosition = new Vector2d(x,y);
            Grass newGrass = new Grass(newGrassPosition);
            if(!grassTiles.containsKey(newGrassPosition)){
                DYNAMIC_TOP_RIGHT_BORDER = DYNAMIC_TOP_RIGHT_BORDER.upperRight(newGrassPosition);
                DYNAMIC_BOTTOM_LEFT_BORDER = DYNAMIC_BOTTOM_LEFT_BORDER.lowerLeft(newGrassPosition);
                grassTiles.put(newGrassPosition,newGrass);
                cnt++;
            }
        }
        BOTTOM_LEFT_GRASS = DYNAMIC_BOTTOM_LEFT_BORDER;
        TOP_RIGHT_GRASS = DYNAMIC_TOP_RIGHT_BORDER;
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

    private void findBorders(){
        DYNAMIC_BOTTOM_LEFT_BORDER = BOTTOM_LEFT_GRASS;
        DYNAMIC_TOP_RIGHT_BORDER = TOP_RIGHT_GRASS;

        for(Map.Entry<Vector2d, Animal> entry : animals.entrySet()){
            Vector2d key = entry.getKey();
            DYNAMIC_BOTTOM_LEFT_BORDER = DYNAMIC_BOTTOM_LEFT_BORDER.lowerLeft(key);
            DYNAMIC_TOP_RIGHT_BORDER = DYNAMIC_TOP_RIGHT_BORDER.upperRight(key);
        }
    }

    @Override
    public ArrayList<WorldElement> getElements(){
        ArrayList<WorldElement> result = super.getElements();
        result.addAll(grassTiles.values());
        return result;
    }

    @Override
    public Boundary getCurrentBounds(){
        findBorders();
        return new Boundary(DYNAMIC_BOTTOM_LEFT_BORDER, DYNAMIC_TOP_RIGHT_BORDER);
    }
}