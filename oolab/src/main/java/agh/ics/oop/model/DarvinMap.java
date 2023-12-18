package agh.ics.oop.model;

import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;

public class DarvinMap implements WorldMap{
    private final int mapSize = 2;
    private final List<MapChangeListener> observers = new ArrayList<>();
    public void addObserver(MapChangeListener observer) {
        observers.add(observer);
    }
    protected void notifyObservers(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this, message);
        }
    }
    protected final Map<Vector2d, Animal> animals = new HashMap<>();
    protected final MapVisualizer mapVisualizer;
    protected final UUID id;
    private static final double POISON_PROBABILITY = 0.2;

    public DarvinMap(int FOOD_STARTING_AMOUNT) {
        id = UUID.randomUUID();
        mapVisualizer = new MapVisualizer(this);
        generateTiles();
        generatePoisonedTiles();
        generateFood(FOOD_STARTING_AMOUNT);
    }
    public UUID getId() {
        return id;
    }

    private final Map<Vector2d, AbstractFood> foodTiles = new HashMap<>();
    private final Vector2d BOTTOM_LEFT_MAP_BORDER = new Vector2d(0,0);
    private final Vector2d TOP_RIGHT_MAP_BORDER = new Vector2d(mapSize - 1,mapSize - 1);
    private final TileType[][] tiles = new TileType[mapSize][mapSize];
    private final List<Vector2d> dirtTilesPositions = new ArrayList<>();
    private int dirtFoodAmount = 0;
    private final List<Vector2d> jungleTilesPositions = new ArrayList<>();
    private int jungleFoodAmount = 0;
    private final boolean[][]isMaybePoisonedTile  = new boolean[mapSize][mapSize];
    private int lastIndex = 0;
    public void place(Animal animal) {
        animals.put(animal.getPosition(), animal);
        notifyObservers("Animal placed at " + animal.getPosition());
    }

    public void generateFood(int howManyFoodToGenerate)
    {
        int cnt = 0;
        while(cnt < howManyFoodToGenerate){
            Vector2d newFoodPosition;
            if(jungleFoodAmount<jungleTilesPositions.size() && Math.random() < 0.8){//jungle drawn

                newFoodPosition = getFreeTile(jungleTilesPositions);
                jungleFoodAmount++;
            }
            else if(dirtFoodAmount<dirtTilesPositions.size())
            {
                newFoodPosition = getFreeTile(dirtTilesPositions);
                dirtFoodAmount++;
            }
            else break; //whole map in food

            if(!foodTiles.containsKey(newFoodPosition))
            {
                if(isMaybePoisonedTile[newFoodPosition.y()][newFoodPosition.x()] && Math.random() < POISON_PROBABILITY) //generate poisonedFruit
                    foodTiles.put(newFoodPosition, new PoisonedFruit(newFoodPosition));

                else foodTiles.put(newFoodPosition,new Grass(newFoodPosition));
                notifyObservers("Food generated at " + newFoodPosition);
                cnt++;
            }
        }
    }

    private Vector2d getFreeTile(List<Vector2d> tilesPositions)
    {

        int listSize = tilesPositions.size();
        int localIndex = (lastIndex + 1)%listSize;
        int cnt=0;
        while(cnt<listSize)
        {
            Vector2d position = tilesPositions.get(localIndex);
            if(!foodTiles.containsKey(position))
            {
                lastIndex = localIndex;
                return position;
            }

            cnt++;
            localIndex = (localIndex + 1)%listSize;
        }
        return null;
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

    public void move(Animal animal, int ANIMAL_ENERGY_PER_MOVE) {
        Vector2d oldPosition = animal.getPosition();

        animal.move(this, ANIMAL_ENERGY_PER_MOVE, Collections.unmodifiableMap(foodTiles));

        if(oldPosition != animal.getPosition()){
            animals.remove(oldPosition, animal);
            animals.put(animal.getPosition(), animal);
        }
        notifyObservers("XAnimal has " + animal.getChildrenAmount() + " children \n Animal has " + animal.getDescendantAmount() + " descendant \n");
    }


    public TileType[][] getTiles(){
        return tiles;
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position) || foodTiles.containsKey(position);
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        if(animals.getOrDefault(position, null) != null){
            return animals.getOrDefault(position, null);
        }
        return foodTiles.getOrDefault(position, null);
    }

    @Override
    public Boundary getCurrentBounds(){
        return new Boundary(BOTTOM_LEFT_MAP_BORDER, TOP_RIGHT_MAP_BORDER);
    }

    private boolean willAnimalBeOutOfBorder(Vector2d position){
        return position.x() < BOTTOM_LEFT_MAP_BORDER.x() || position.x() > TOP_RIGHT_MAP_BORDER.x() ||
                position.y() < BOTTOM_LEFT_MAP_BORDER.y() || position.y() > TOP_RIGHT_MAP_BORDER.y();
    }
    public Vector2d getNewPositionForAnimal(Animal animal){
        Vector2d oldPosition = animal.getPosition();
        MapDirection orientation = animal.getOrientation();

        Vector2d newPosition = oldPosition.add(orientation.toUnitVector());

        if(willAnimalBeOutOfBorder(newPosition)){
            if(newPosition.y() > TOP_RIGHT_MAP_BORDER.y() || newPosition.y() < BOTTOM_LEFT_MAP_BORDER.y()){
                animal.setOrientation(orientation.reverse());
                return oldPosition;
            }
            else if(newPosition.x() < BOTTOM_LEFT_MAP_BORDER.x()){
                return new Vector2d(TOP_RIGHT_MAP_BORDER.x() ,oldPosition.y());
            }
            else {
                return new Vector2d(BOTTOM_LEFT_MAP_BORDER.x(), oldPosition.y());
            }
        }
        else{
            return newPosition;
        }
    }

    public Map<Vector2d, AbstractFood> getFoodTiles() {
        return Collections.unmodifiableMap(foodTiles);
    }

    public void feedAnimal(Animal animalThatEats, int foodEnergy)
    {
        Vector2d position = animalThatEats.getPosition();
        if(tiles[position.y()][position.x()] == TileType.DIRT){
            dirtFoodAmount--;
        }
        else {
            jungleFoodAmount--;
        }
        foodTiles.remove(animalThatEats.getPosition());
        animalThatEats.eat(foodEnergy);
    }

    public int getMapSize() {
        return mapSize;
    }

    @Override
    public String toString(){
        Boundary boundaries = getCurrentBounds();
        return mapVisualizer.draw(boundaries.bottomLeftCorner(), boundaries.topRightCorner());
    }

    public void removeAnimal(Animal animal){
        animals.remove(animal.getPosition(), animal);
    }
}