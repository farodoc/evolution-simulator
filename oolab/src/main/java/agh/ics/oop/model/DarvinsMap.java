package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DarvinsMap extends AbstractWorldMap{
    private final int mapSize = 30;
    private int grassNumber;
    public DarvinsMap(int grassNumber) {
        super();
        //this.grassNumber = grassNumber;
        this.grassNumber = mapSize;
        generateTiles();
        generatePoisonedTiles();
        generateFood();
    }

    private final Map<Vector2d, AbstractFood> foodTiles = new HashMap<>();
    private final Vector2d BOTTOM_LEFT_MAP_BORDER = new Vector2d(0,0);
    private final Vector2d TOP_RIGHT_MAP_BORDER = new Vector2d(mapSize - 1,mapSize - 1);
    private final TileType[][] tiles = new TileType[mapSize][mapSize];
    private final boolean[][]isMaybePoisonedTile  = new boolean[mapSize][mapSize];
    private static final double POISON_PROBABILITY = 0.2;

    private void generateFood()
    {
        int cnt = 0;
        while(cnt < grassNumber){
            int x = (int) (Math.random() * mapSize);
            int y = (int) (Math.random() * mapSize);
            Vector2d newFoodPosition = new Vector2d(x,y);

            if(!foodTiles.containsKey(newFoodPosition)){
                if(isFoodSpawnedAtYX(y, x))
                {
                    if(isMaybePoisonedTile[y][x] && Math.random() < POISON_PROBABILITY) //generate poisonedFruit
                        foodTiles.put(newFoodPosition, new PoisonedFruit(newFoodPosition));

                    else foodTiles.put(newFoodPosition,new Grass(newFoodPosition));

                    cnt++;
                }
            }
        }
    }

    private boolean isFoodSpawnedAtYX(int y, int x) {
        return tiles[y][x] == TileType.JUNG && Math.random() < 0.8 || tiles[y][x] == TileType.DIRT && Math.random() < 0.2;
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
        double probabilityForRow = 1.0;
        int equator = mapSize/2;

        boolean generateUpper = true;
        int yModifier = 0;

        while(jungleTilesCounter < jungleTilesAmount && isInMap(equator, yModifier))
        {
            int x=0;
            while(x<mapSize && jungleTilesCounter<jungleTilesAmount)
            {
                if(Math.random() < probabilityForRow)
                {
                    jungleTilesCounter++;
                    tiles[equator+yModifier][x] = TileType.JUNG;
                }
                x++;
            }

            if(generateUpper)
            {
                yModifier +=1;
                yModifier *= (-1);
                generateUpper = false;
            }
            else
            {
                yModifier *= (-1);
                generateUpper = true;
            }

            probabilityForRow/=1.25;
        }

    }

    private void generatePoisonedTiles()
    {
        double poisonedTilesAmount =mapSize*mapSize*0.2;
        int a = (int) Math.sqrt(poisonedTilesAmount); //a = lengthOfSquare
        int startingIndex = (mapSize - a)/2;

        for(int x=startingIndex; x<startingIndex+a; x++)
        {
            for(int y=startingIndex; y<startingIndex+a; y++)
            {
                isMaybePoisonedTile[y][x]=true;
            }
        }
    }
    private boolean isInMap(int equator, int yModifier)
    {
        return equator + yModifier < mapSize && equator - yModifier >= 0;
    }


    public TileType[][] getTiles(){
        return tiles;
    }

    public void printMap() {
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                System.out.print(isMaybePoisonedTile[i][j] + " ");
            }
            System.out.println();
        }
    }

    Map<Vector2d, Animal> getAnimals() {
        return Collections.unmodifiableMap(animals);
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return super.isOccupied(position) || foodTiles.containsKey(position);
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        if(super.objectAt(position) != null){
            return super.objectAt(position);
        }
        return foodTiles.getOrDefault(position, null);
    }

    @Override
    public ArrayList<WorldElement> getElements(){
        ArrayList<WorldElement> result = super.getElements();
        result.addAll(foodTiles.values());
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