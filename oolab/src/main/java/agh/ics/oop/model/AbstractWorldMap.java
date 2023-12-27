package agh.ics.oop.model;

import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap{
    protected int mapWidth;
    protected int mapHeight;
    private final List<MapChangeListener> observers = new ArrayList<>();
    protected final Map<Vector2d, Animal> animals = new HashMap<>();
    protected final MapVisualizer mapVisualizer;
    protected final UUID id;
    protected final Map<Vector2d, AbstractFood> foodTiles = new HashMap<>();
    protected final Vector2d BOTTOM_LEFT_MAP_BORDER = new Vector2d(0,0);
    protected final Vector2d TOP_RIGHT_MAP_BORDER;
    protected final TileType[][] tiles;
    protected final List<Vector2d> dirtTilesPositions = new ArrayList<>();
    protected int dirtFoodAmount = 0;
    protected final List<Vector2d> jungleTilesPositions = new ArrayList<>();
    protected int jungleFoodAmount = 0;
    protected int lastIndex = 0;

    public AbstractWorldMap(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        TOP_RIGHT_MAP_BORDER = new Vector2d(mapWidth - 1,mapHeight - 1);
        tiles = new TileType[mapHeight][mapWidth];
        id = UUID.randomUUID();
        mapVisualizer = new MapVisualizer(this);
    }

    public void place(Animal animal) {
        animals.put(animal.getPosition(), animal);
    }
    public void generateFood(int howManyFoodToGenerate){

    }

    protected Vector2d generateNewFoodPosition(){
        if(jungleFoodAmount<jungleTilesPositions.size() && Math.random() < 0.8){//jungle drawn
            jungleFoodAmount++;
            return getFreeTile(jungleTilesPositions);
        }
        else if(dirtFoodAmount<dirtTilesPositions.size()){
            dirtFoodAmount++;
            return getFreeTile(dirtTilesPositions);
        }
        return null;
    }

    protected Vector2d getFreeTile(List<Vector2d> tilesPositions){
        int listSize = tilesPositions.size();
        int localIndex = (lastIndex + 1)%listSize;
        int cnt=0;
        while(cnt<listSize){
            Vector2d position = tilesPositions.get(localIndex);
            if(!foodTiles.containsKey(position)){
                lastIndex = localIndex;
                return position;
            }

            cnt++;
            localIndex = (localIndex + 1)%listSize;
        }
        return null;
    }


    protected void generateTiles(){
        generateJungleTiles();

        for(int x=0; x<mapWidth; x++){
            for(int y=0; y<mapHeight; y++){
                if(tiles[y][x]!=TileType.JUNG){
                    tiles[y][x] = TileType.DIRT;
                    dirtTilesPositions.add(new Vector2d(x,y));
                }
                else {
                    jungleTilesPositions.add(new Vector2d(x,y));
                }
            }
        }
        Collections.shuffle(dirtTilesPositions);
        Collections.shuffle(jungleTilesPositions);
    }

    protected void generateJungleTiles(){

    }

    protected boolean isInMap(int equator, int yModifier){
        return equator + yModifier < mapHeight && equator - yModifier >= 0;
    }

    public void move(Animal animal, int ANIMAL_ENERGY_PER_MOVE) {
        Vector2d oldPosition = animal.getPosition();

        animal.move(this, ANIMAL_ENERGY_PER_MOVE, Collections.unmodifiableMap(foodTiles));

        if(oldPosition != animal.getPosition()){
            animals.remove(oldPosition, animal);
            animals.put(animal.getPosition(), animal);
        }
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

    protected boolean willAnimalBeOutOfBorder(Vector2d position){
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

    public void feedAnimal(Animal animalThatEats, int foodEnergy){
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

    @Override
    public String toString(){
        Boundary boundaries = getCurrentBounds();
        return mapVisualizer.draw(boundaries.bottomLeftCorner(), boundaries.topRightCorner());
    }

    public void removeAnimal(Animal animal){
        animals.remove(animal.getPosition(), animal);
    }
    public int getMapHeight() {return mapHeight;}
    public int getMapWidth() {return mapWidth;}
    public UUID getId() {
        return id;
    }
    public void addObserver(MapChangeListener observer) {
        observers.add(observer);
    }
    protected void notifyObservers(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this, message);
        }
    }
    public void initializeDrawMap(){ notifyObservers(""); }

    public String getName() {return "Abstract map";}
}
