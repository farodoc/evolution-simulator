package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class DarvinsMap extends AbstractWorldMap{
    private final int mapSize = 10;
    private int grassNumber;
    public DarvinsMap(int grassNumber) {
        super();
        this.grassNumber = grassNumber;
        generateTiles();
        generateGrass();
    }

    private final Map<Vector2d, Grass> grassTiles = new HashMap<>();
    private final Vector2d BOTTOM_LEFT_MAP_BORDER = new Vector2d(0,0);
    private final Vector2d TOP_RIGHT_MAP_BORDER = new Vector2d(mapSize - 1,mapSize - 1);

    private final TileType[][] tiles = new TileType[mapSize][mapSize];

    private void generateGrass()
    {
        int cords = (int) sqrt(grassNumber * 10);
        int cnt = 0;
        while(cnt < grassNumber){
            int x = (int) (Math.random() * cords);
            int y = (int) (Math.random() * cords);
            Vector2d newGrassPosition = new Vector2d(x,y);
            Grass newGrass = new Grass(newGrassPosition);
            if(!grassTiles.containsKey(newGrassPosition)){
                if(tiles[y][x] == TileType.JUNG && Math.random() < 0.8 || tiles[y][x] == TileType.DIRT && Math.random() < 0.2)
                {
                    grassTiles.put(newGrassPosition,newGrass);
                    cnt++;
                }
            }
        }
    }

    public void printMap() {
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                System.out.print(tiles[i][j] + " ");
            }
            System.out.println();
        }
    }
    private void generateTiles()
    {
        generateJungleTiles();

        for(int x=0; x<mapSize; x++)
        {
            for(int y=0; y<mapSize; y++)
            {
                if(tiles[x][y]!=TileType.JUNG)
                    tiles[x][y] = TileType.DIRT;
            }
        }
    }

    private void generateJungleTiles()
    {
        int jungleTilesAmount = (int) (mapSize*mapSize*0.2);
        int jungleTilesCounter = 0;
        int startingIndex= mapSize/2;
        boolean generateUpper = true;
        int yModifier = 0;
        int equator = startingIndex;
        double propabilityForRow = 1.0;

        while(jungleTilesCounter < jungleTilesAmount && equator+yModifier<mapSize && equator-yModifier>=0)
        {
            for(int x=0; x < mapSize; x++)
            {
                if(jungleTilesCounter>=jungleTilesAmount)
                    break;

                else if(Math.random() < propabilityForRow)
                {
                    jungleTilesCounter++;
                    tiles[equator+yModifier][x] = TileType.JUNG;
                }
            }

            if(generateUpper)
            {
                yModifier+=1;
                yModifier *= (-1);
                generateUpper= false;
            }
            else
            {
                yModifier *= (-1);
                generateUpper= true;
            }

            propabilityForRow/=1.25;
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

    private boolean willAnimalBeOutOfBorder(Vector2d position){
        return position.getX() < BOTTOM_LEFT_MAP_BORDER.getX() || position.getX() > TOP_RIGHT_MAP_BORDER.getX() ||
                position.getY() < BOTTOM_LEFT_MAP_BORDER.getY() || position.getY() > TOP_RIGHT_MAP_BORDER.getY();
    }
    public Vector2d getNewPositionForAnimal(Animal animal){
        Vector2d oldPosition = animal.getPosition();
        MapDirection orientation = animal.getOrientation();

        Vector2d newPosition = oldPosition.add(orientation.toUnitVector());

        if(willAnimalBeOutOfBorder(newPosition)){
            if(newPosition.getY() > TOP_RIGHT_MAP_BORDER.getY() || newPosition.getY() < BOTTOM_LEFT_MAP_BORDER.getY()){
                animal.setOrientation(orientation.reverse());
                return oldPosition;
            }
            else if(newPosition.getX() < BOTTOM_LEFT_MAP_BORDER.getX()){
                return new Vector2d(TOP_RIGHT_MAP_BORDER.getX() ,oldPosition.getY());
            }
            else {
                return new Vector2d(BOTTOM_LEFT_MAP_BORDER.getX(), oldPosition.getY());
            }
        }
        else{
            return newPosition;
        }
    }
}