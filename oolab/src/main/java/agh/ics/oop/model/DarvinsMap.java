package agh.ics.oop.model;

import java.util.*;

public class DarvinsMap extends AbstractWorldMap{
    public int getMapSize() {
        return mapSize;
    }

    private final int mapSize = 20;
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
    private final List<Vector2d> dirtTilesPositions = new ArrayList<>();
    private int dirtTilesPositionsIndex = 0;
    private final List<Vector2d> jungleTilesPositions = new ArrayList<>();
    private int jungleTilesPositionsIndex = 0;
    private final boolean[][]isMaybePoisonedTile  = new boolean[mapSize][mapSize];
    private static final double POISON_PROBABILITY = 0.2;

    private void generateFood()
    {
        int cnt = 0;
        while(cnt < grassNumber){
            Vector2d newFoodPosition;
            if(Math.random() < 0.8){//wylosowala sie jungla
                newFoodPosition = jungleTilesPositions.get(jungleTilesPositionsIndex);
                jungleTilesPositionsIndex = (jungleTilesPositionsIndex + 1) % jungleTilesPositions.size();
            }
            else{
                newFoodPosition = dirtTilesPositions.get(dirtTilesPositionsIndex);
                dirtTilesPositionsIndex = (dirtTilesPositionsIndex + 1) % dirtTilesPositions.size();
            }

            if(!foodTiles.containsKey(newFoodPosition)){
                if(isMaybePoisonedTile[newFoodPosition.getY()][newFoodPosition.getX()] && Math.random() < POISON_PROBABILITY) //generate poisonedFruit
                    foodTiles.put(newFoodPosition, new PoisonedFruit(newFoodPosition));

                else foodTiles.put(newFoodPosition,new Grass(newFoodPosition));

                cnt++;
            }
        }
    }

    private void generateTiles()
    {
        generateJungleTiles();

        for(int x=0; x<mapSize; x++)
        {
            for(int y=0; y<mapSize; y++)
            {
                if(tiles[x][y]!=TileType.JUNG){
                    tiles[x][y] = TileType.DIRT;
                    dirtTilesPositions.add(new Vector2d(y,x));
                }
                else {
                    jungleTilesPositions.add(new Vector2d(y,x));
                }
            }
        }
        Collections.shuffle(dirtTilesPositions);
        Collections.shuffle(jungleTilesPositions);
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