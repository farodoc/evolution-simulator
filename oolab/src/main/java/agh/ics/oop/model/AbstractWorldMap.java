package agh.ics.oop.model;

import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap{
    protected int mapWidth;
    protected int mapHeight;
    private final List<MapChangeListener> observers = new ArrayList<>();
    protected final Map<Vector2d, List<Animal>> animals = new HashMap<>();
    protected final Map<Vector2d, AbstractFood> foodTiles = new HashMap<>();
    protected final MapVisualizer mapVisualizer;
    protected final UUID id;
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
        Vector2d position = animal.getPosition();

        if (animals.containsKey(position)) {
            List<Animal> animalList = animals.get(position);
            animalList.add(animal);
        }
        else {
            List<Animal> newAnimalList = new ArrayList<>();
            newAnimalList.add(animal);
            animals.put(position, newAnimalList);
        }
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

        if (!oldPosition.equals(animal.getPosition())) {
            List<Animal> animalList = animals.get(oldPosition);

            animalList.remove(animal);

            if (animalList.isEmpty()) {
                animals.remove(oldPosition);
            }

            List<Animal> newAnimalList = animals.computeIfAbsent(animal.getPosition(), k -> new ArrayList<>());
            newAnimalList.add(animal);
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
        List<Animal> animalList = animals.getOrDefault(position, null);

        if (animalList != null && !animalList.isEmpty()) {
            return animalList.get(0);
        }
        else {
            return foodTiles.getOrDefault(position, null);
        }
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

    //DO USUNIECIA !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void removeAnimal(Animal animal) {
        Vector2d position = animal.getPosition();
        List<Animal> animalList = animals.get(position);

        if (animalList != null) {
            animalList.remove(animal);

            if (animalList.isEmpty()) {
                animals.remove(position);
            }
        }
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

    //Metody z symulacji
    public void generateAnimals(int ANIMAL_STARTING_AMOUNT, int ANIMAL_STARTING_ENERGY, int ANIMAL_GENES_AMOUNT, boolean LOOPED_GENES_ACTIVE){
        for(int i = 0; i < ANIMAL_STARTING_AMOUNT; i++){
            int x = (int)(Math.random() * mapWidth);
            int y = (int)(Math.random() * mapHeight);
            Animal animal = new Animal(new Vector2d(x, y), ANIMAL_STARTING_ENERGY, ANIMAL_GENES_AMOUNT, LOOPED_GENES_ACTIVE);
            place(animal);
        }
    }

    public void clearDeadAnimals() {
        for (Map.Entry<Vector2d, List<Animal>> entry : animals.entrySet()) {
            Vector2d position = entry.getKey();
            List<Animal> animalList = entry.getValue();

            for (int i = animalList.size() - 1; i >= 0; i--) {
                if(animalList.get(i).getEnergy() <= 0){
                    animalList.remove(i);
                }
            }

            if (animalList.isEmpty()) {
                animals.remove(position);
            }
        }
    }

    public void moveAllAnimals(int ANIMAL_ENERGY_PER_MOVE){
        for (Map.Entry<Vector2d, List<Animal>> entry : animals.entrySet()) {
            List<Animal> animalList = entry.getValue();

            for (Animal animal : animalList) {
                move(animal, ANIMAL_ENERGY_PER_MOVE);
                animal.updateAge();
            }
        }
    }

    public void feedAnimals(int FOOD_ENERGY){
        for (Map.Entry<Vector2d, List<Animal>> entry : animals.entrySet()) {
            Vector2d position = entry.getKey();

            if(foodTiles.containsKey(position)) {
                AbstractFood food = foodTiles.get(position);
                Animal animalThatEats = conflictManager(position);

                if(Objects.equals(food.toString(), "X"))
                    feedAnimal(animalThatEats, -FOOD_ENERGY);

                else
                    feedAnimal(animalThatEats, FOOD_ENERGY);
            }
        }
    }

    private Animal conflictManager(Vector2d position){
        List<Animal> filteredAnimals = new ArrayList<>(animals.get(position));

        Comparator<Animal> animalComparator = Comparator
                .comparingInt(Animal::getEnergy)
                .thenComparingInt(Animal::getAge)
                .thenComparingInt(Animal::getChildrenAmount)
                .reversed();

        filteredAnimals.sort(animalComparator);

        return filteredAnimals.get(0);
    }

    public void breedAnimals(int ANIMAL_MIN_ENERGY_TO_REPRODUCE, int ANIMAL_ENERGY_TO_REPRODUCE_COST,
                              int ANIMAL_GENES_AMOUNT, boolean LOOPED_GENES_ACTIVE,
                              int ANIMAL_MIN_MUTATIONS, int ANIMAL_MAX_MUTATIONS)
    {
        HashMap<Vector2d, List<Animal>> animalsCopy = new HashMap<>(animals);

        for (Map.Entry<Vector2d, List<Animal>> entry : animalsCopy.entrySet()) {
            Vector2d position = entry.getKey();
            List<Animal> animalList = entry.getValue();
            if(animalList.size() >= 2){
                List<Animal> filteredAnimals = findAnimalsToBreed(position, ANIMAL_MIN_ENERGY_TO_REPRODUCE);
                if(filteredAnimals.size() >= 2){
                    combineAnimalsAndSpawnChild(filteredAnimals.get(0), filteredAnimals.get(1),
                            ANIMAL_ENERGY_TO_REPRODUCE_COST, ANIMAL_GENES_AMOUNT, LOOPED_GENES_ACTIVE,
                            ANIMAL_MIN_MUTATIONS, ANIMAL_MAX_MUTATIONS);
                }
            }
        }
    }

    private List<Animal> findAnimalsToBreed(Vector2d position, int ANIMAL_MIN_ENERGY_TO_REPRODUCE){
        List<Animal> filteredAnimals = new ArrayList<>();
        List<Animal> animalList = new ArrayList<>(animals.get(position));

        for (Animal animal : animalList) {
            if (animal.getEnergy() >= ANIMAL_MIN_ENERGY_TO_REPRODUCE) {
                filteredAnimals.add(animal);
            }
        }

        Comparator<Animal> animalComparator = Comparator
                .comparingInt(Animal::getEnergy)
                .thenComparingInt(Animal::getAge)
                .thenComparingInt(Animal::getChildrenAmount)
                .reversed();

        filteredAnimals.sort(animalComparator);

        return filteredAnimals;
    }

    private void combineAnimalsAndSpawnChild(Animal strongerAnimal, Animal weakerAnimal,
                                             int ANIMAL_ENERGY_TO_REPRODUCE_COST,
                                             int ANIMAL_GENES_AMOUNT,
                                             boolean LOOPED_GENES_ACTIVE,
                                             int ANIMAL_MIN_MUTATIONS,
                                             int ANIMAL_MAX_MUTATIONS)
    {
        strongerAnimal.updateAnimalAfterBreeding(ANIMAL_ENERGY_TO_REPRODUCE_COST);
        weakerAnimal.updateAnimalAfterBreeding(ANIMAL_ENERGY_TO_REPRODUCE_COST);

        Animal child = new Animal(strongerAnimal.getPosition(), 2 * ANIMAL_ENERGY_TO_REPRODUCE_COST, ANIMAL_GENES_AMOUNT, LOOPED_GENES_ACTIVE,
                strongerAnimal, weakerAnimal, ANIMAL_MIN_MUTATIONS, ANIMAL_MAX_MUTATIONS);
        place(child);
    }

    public boolean isEveryAnimalDead(){
        return animals.isEmpty();
    }
}
