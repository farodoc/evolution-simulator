package agh.ics.oop.model;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap {
    protected int mapWidth;
    protected int mapHeight;
    private final List<MapChangeListener> observers = new ArrayList<>();
    protected final Map<Vector2d, List<Animal>> animals = Collections.synchronizedMap(new HashMap<>());
    protected final Map<Vector2d, AbstractFood> foodTiles = Collections.synchronizedMap(new HashMap<>());
    private final Map<List<Integer>, Integer> currentGenotypeCounts = Collections.synchronizedMap(new HashMap<>());
    protected final UUID id;
    protected final Vector2d BOTTOM_LEFT_MAP_BORDER = new Vector2d(0, 0);
    protected final Vector2d TOP_RIGHT_MAP_BORDER;
    protected final TileType[][] tiles;
    protected final List<Vector2d> dirtTilesPositions = new ArrayList<>();
    protected int dirtFoodAmount = 0;
    protected final List<Vector2d> jungleTilesPositions = new ArrayList<>();
    protected int jungleFoodAmount = 0;
    protected int lastIndex = 0;

    //values for stats
    private int day = 0;
    protected int totalAnimalAmount = 0;
    protected int deadAnimalCount = 0;
    protected int deadAnimalSumAge = 0;

    public AbstractWorldMap(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        TOP_RIGHT_MAP_BORDER = new Vector2d(mapWidth - 1, mapHeight - 1);
        tiles = new TileType[mapHeight][mapWidth];
        id = UUID.randomUUID();
    }

    public void place(Animal animal) {
        Vector2d position = animal.getPosition();

        if (animals.containsKey(position)) {
            List<Animal> animalList = animals.get(position);
            animalList.add(animal);
        } else {
            List<Animal> newAnimalList = new ArrayList<>();
            newAnimalList.add(animal);
            animals.put(position, newAnimalList);
        }
        totalAnimalAmount++;
        currentGenotypeCounts.put(animal.getGenes().getGenesList(), currentGenotypeCounts.getOrDefault(animal.getGenes().getGenesList(), 0) + 1);
    }

    public void nextDay() {
        day++;
    }

    abstract public void generateFood(int howManyFoodToGenerate);

    abstract protected void generateJungleTiles();

    protected Vector2d generateNewFoodPosition() {
        if (jungleFoodAmount < jungleTilesPositions.size() && Math.random() < 0.8) {//jungle drawn
            jungleFoodAmount++;
            return getFreeTile(jungleTilesPositions);
        } else if (dirtFoodAmount < dirtTilesPositions.size()) {
            dirtFoodAmount++;
            return getFreeTile(dirtTilesPositions);
        }
        return null;
    }

    protected Vector2d getFreeTile(List<Vector2d> tilesPositions) {
        int listSize = tilesPositions.size();
        int localIndex = (lastIndex + 1) % listSize;
        int cnt = 0;
        while (cnt < listSize) {
            Vector2d position = tilesPositions.get(localIndex);
            if (!foodTiles.containsKey(position)) {
                lastIndex = localIndex;
                return position;
            }

            cnt++;
            localIndex = (localIndex + 1) % listSize;
        }
        return null;
    }

    protected void generateTiles() {
        generateJungleTiles();

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (tiles[y][x] != TileType.JUNG) {
                    tiles[y][x] = TileType.DIRT;
                    dirtTilesPositions.add(new Vector2d(x, y));
                } else {
                    jungleTilesPositions.add(new Vector2d(x, y));
                }
            }
        }
        Collections.shuffle(dirtTilesPositions);
        Collections.shuffle(jungleTilesPositions);
    }

    protected boolean isInMap(int equator, int yModifier) {
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

    public TileType[][] getTiles() {
        TileType[][] copy = new TileType[tiles.length][tiles[0].length];

        for (int i = 0; i < tiles.length; i++) {
            copy[i] = Arrays.copyOf(tiles[i], tiles[i].length);
        }

        return copy;
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position) || foodTiles.containsKey(position);
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        List<Animal> animalList;

        synchronized (animals) {
            animalList = animals.getOrDefault(position, null);
        }

        if (animalList != null && !animalList.isEmpty()) {
            Animal animalToReturn = animalList.get(0);

            synchronized (animals) {
                for (Animal animal : animalList) {
                    if (animal.getEnergy() > animalToReturn.getEnergy()) {
                        animalToReturn = animal;
                    }
                }
            }

            if (animalToReturn.getEnergy() <= 0)
                return foodTiles.getOrDefault(position, null);

            return animalToReturn;
        }

        return foodTiles.getOrDefault(position, null);
    }


    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(BOTTOM_LEFT_MAP_BORDER, TOP_RIGHT_MAP_BORDER);
    }

    protected boolean willAnimalBeOutOfBorder(Vector2d position) {
        return !(position.follows(BOTTOM_LEFT_MAP_BORDER) && position.precedes(TOP_RIGHT_MAP_BORDER));
    }

    public Vector2d getNewPositionForAnimal(Animal animal) {
        Vector2d oldPosition = animal.getPosition();
        MapDirection orientation = animal.getOrientation();

        Vector2d newPosition = oldPosition.add(orientation.toUnitVector());

        if (willAnimalBeOutOfBorder(newPosition)) {
            if (newPosition.y() > TOP_RIGHT_MAP_BORDER.y() || newPosition.y() < BOTTOM_LEFT_MAP_BORDER.y()) {
                animal.setOrientation(orientation.reverse());
                return oldPosition;
            } else if (newPosition.x() < BOTTOM_LEFT_MAP_BORDER.x()) {
                return new Vector2d(TOP_RIGHT_MAP_BORDER.x(), oldPosition.y());
            } else {
                return new Vector2d(BOTTOM_LEFT_MAP_BORDER.x(), oldPosition.y());
            }
        } else {
            return newPosition;
        }
    }

    public void feedAnimal(Animal animalThatEats, int foodEnergy) {
        Vector2d position = animalThatEats.getPosition();
        if (tiles[position.y()][position.x()] == TileType.DIRT) {
            dirtFoodAmount--;
        } else {
            jungleFoodAmount--;
        }
        foodTiles.remove(animalThatEats.getPosition());
        animalThatEats.eat(foodEnergy);
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

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

    public void initializeDrawMap() {
        notifyObservers("");
    }

    public abstract String getName();

    //Metody z symulacji
    public void generateAnimals(int ANIMAL_STARTING_AMOUNT, int ANIMAL_STARTING_ENERGY, int ANIMAL_GENES_AMOUNT, boolean LOOPED_GENES_ACTIVE) {
        for (int i = 0; i < ANIMAL_STARTING_AMOUNT; i++) {
            int x = (int) (Math.random() * mapWidth);
            int y = (int) (Math.random() * mapHeight);
            Animal animal = new Animal(new Vector2d(x, y), ANIMAL_STARTING_ENERGY, ANIMAL_GENES_AMOUNT, LOOPED_GENES_ACTIVE);
            place(animal);
        }
    }

    public void clearDeadAnimals() {
        Map<Vector2d, List<Animal>> animalsCopy = generateDeepCopyOfAnimalsMap();

        for (Map.Entry<Vector2d, List<Animal>> entry : animalsCopy.entrySet()) {
            Vector2d position = entry.getKey();
            List<Animal> animalList = entry.getValue();

            for (int i = animalList.size() - 1; i >= 0; i--) {
                if (animalList.get(i).getEnergy() <= 0) {
                    deadAnimalCount++;
                    deadAnimalSumAge += animalList.get(i).getAge();
                    animalList.get(i).setDeathDate(day);
                    List<Integer> genesList = animalList.get(i).getGenes().getGenesList();
                    currentGenotypeCounts.put(genesList, currentGenotypeCounts.getOrDefault(genesList, 0) - 1);
                    animalList.remove(i);
                }
            }

            if (animalList.isEmpty()) {
                animals.remove(position);
            }
        }
    }

    public void moveAllAnimals(int ANIMAL_ENERGY_PER_MOVE) {

        Map<Vector2d, List<Animal>> animalsCopy = generateDeepCopyOfAnimalsMap();

        for (Map.Entry<Vector2d, List<Animal>> entry : animalsCopy.entrySet()) {
            List<Animal> animalList = entry.getValue();

            for (Animal animal : animalList) {
                move(animal, ANIMAL_ENERGY_PER_MOVE);
                animal.updateAge();
            }
        }
    }

    public void feedAnimals(int FOOD_ENERGY) {
        for (Map.Entry<Vector2d, List<Animal>> entry : animals.entrySet()) {
            Vector2d position = entry.getKey();

            if (foodTiles.containsKey(position)) {
                AbstractFood food = foodTiles.get(position);
                Animal animalThatEats = findTheStrongestAnimal(position);

                if (Objects.equals(food.toString(), "X"))
                    feedAnimal(animalThatEats, -FOOD_ENERGY);

                else
                    feedAnimal(animalThatEats, FOOD_ENERGY);
            }
        }
    }

    protected Animal findTheStrongestAnimal(Vector2d position) {
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
                             int ANIMAL_MIN_MUTATIONS, int ANIMAL_MAX_MUTATIONS) {
        Map<Vector2d, List<Animal>> animalsCopy = generateDeepCopyOfAnimalsMap();

        for (Map.Entry<Vector2d, List<Animal>> entry : animalsCopy.entrySet()) {
            List<Animal> animalList = entry.getValue();
            if (animalList.size() >= 2) {
                List<Animal> filteredAnimals = findAnimalsToBreed(animalList, ANIMAL_MIN_ENERGY_TO_REPRODUCE);
                if (filteredAnimals.size() >= 2) {
                    combineAnimalsAndSpawnChild(filteredAnimals.get(0), filteredAnimals.get(1),
                            ANIMAL_ENERGY_TO_REPRODUCE_COST, ANIMAL_GENES_AMOUNT, LOOPED_GENES_ACTIVE,
                            ANIMAL_MIN_MUTATIONS, ANIMAL_MAX_MUTATIONS);
                }
            }
        }
    }

    protected List<Animal> findAnimalsToBreed(List<Animal> animalCopyList, int ANIMAL_MIN_ENERGY_TO_REPRODUCE) {
        List<Animal> filteredAnimals = new ArrayList<>();

        for (Animal animal : animalCopyList) {
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

    protected void combineAnimalsAndSpawnChild(Animal strongerAnimal, Animal weakerAnimal,
                                               int ANIMAL_ENERGY_TO_REPRODUCE_COST,
                                               int ANIMAL_GENES_AMOUNT,
                                               boolean LOOPED_GENES_ACTIVE,
                                               int ANIMAL_MIN_MUTATIONS,
                                               int ANIMAL_MAX_MUTATIONS) {
        strongerAnimal.updateAnimalAfterBreeding(ANIMAL_ENERGY_TO_REPRODUCE_COST);
        weakerAnimal.updateAnimalAfterBreeding(ANIMAL_ENERGY_TO_REPRODUCE_COST);

        Animal child = new Animal(strongerAnimal.getPosition(), 2 * ANIMAL_ENERGY_TO_REPRODUCE_COST, ANIMAL_GENES_AMOUNT, LOOPED_GENES_ACTIVE,
                strongerAnimal, weakerAnimal, ANIMAL_MIN_MUTATIONS, ANIMAL_MAX_MUTATIONS);
        place(child);
    }

    private Map<Vector2d, List<Animal>> generateDeepCopyOfAnimalsMap() {
        Map<Vector2d, List<Animal>> animalsCopy = new HashMap<>();

        for (Map.Entry<Vector2d, List<Animal>> entry : animals.entrySet()) {
            Vector2d position = entry.getKey();
            List<Animal> originalAnimalList = entry.getValue();
            List<Animal> clonedAnimalList = new ArrayList<>(originalAnimalList);
            animalsCopy.put(position, clonedAnimalList);
        }
        return animalsCopy;
    }

    public boolean isEveryAnimalDead() {
        return animals.isEmpty();
    }

    public synchronized Set<Vector2d> getAllOccupiedPositions() {
        Set<Vector2d> occupiedPositions = new HashSet<>();

        synchronized (animals) {
            occupiedPositions.addAll(animals.keySet());
        }

        synchronized (foodTiles) {
            occupiedPositions.addAll(foodTiles.keySet());
        }

        return occupiedPositions;
    }


    public MapStatsInString getCurrentStats() {
        return new MapStatsInString(String.valueOf(day), String.valueOf(countCurrentAnimals()), String.valueOf(foodTiles.size()),
                                    String.valueOf(countFreeTilesAmount()), getMostFrequentGenotypeAsString(),
                                    String.valueOf(getAverageEnergyForLivingAnimals()), String.valueOf(getAverageLifespanForDeadAnimals()),
                                    String.valueOf(getAverageChildrenAmountForLivingAnimals()),
                                    String.valueOf(totalAnimalAmount));
    }

    private synchronized int countFreeTilesAmount() {
        Set<Vector2d> occupiedPositions = new HashSet<>();

        synchronized (animals) {
            occupiedPositions.addAll(animals.keySet());
        }

        synchronized (foodTiles) {
            occupiedPositions.addAll(foodTiles.keySet());
        }

        Set<Vector2d> copyOfOccupiedPositions = new HashSet<>(occupiedPositions);

        return mapWidth * mapHeight - copyOfOccupiedPositions.size();
    }


    private synchronized int countCurrentAnimals() {
        int currentAnimalCount = 0;

        synchronized (animals) {
            for (List<Animal> animalList : animals.values()) {
                if (animalList != null) {
                    currentAnimalCount += animalList.size();
                }
            }
        }

        return currentAnimalCount;
    }


    private synchronized String getMostFrequentGenotypeAsString() {
        List<Integer> mostFrequentGenotype = new ArrayList<>();
        int maxCount = 0;

        synchronized (currentGenotypeCounts) {
            for (Map.Entry<List<Integer>, Integer> entry : currentGenotypeCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    mostFrequentGenotype = entry.getKey();
                    maxCount = entry.getValue();
                }
            }
        }

        return mostFrequentGenotype + " x " + maxCount;
    }

    public synchronized List<Animal> getCurrentMostFrequentGenotypeAnimalList() {
        List<Integer> mostFrequentGenotype = new ArrayList<>();
        int maxCount = 0;

        synchronized (currentGenotypeCounts) {
            for (Map.Entry<List<Integer>, Integer> entry : currentGenotypeCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    mostFrequentGenotype = entry.getKey();
                    maxCount = entry.getValue();
                }
            }
        }

        List<Animal> animalListToReturn = new ArrayList<>();

        synchronized (animals) {
            for (List<Animal> animalsList : animals.values()) {
                if (animalsList != null) {
                    for (Animal animal : animalsList) {
                        if (animal.getGenes().getGenesList().equals(mostFrequentGenotype)) {
                            animalListToReturn.add(animal);
                        }
                    }
                }
            }
        }

        return animalListToReturn;
    }

    private synchronized double getAverageEnergyForLivingAnimals() {
        int energySum = 0;
        int animalCount = 0;

        synchronized (animals) {
            for (List<Animal> animalsList : animals.values()) {
                if (animalsList != null) {
                    for (Animal animal : animalsList) {
                        energySum += animal.getEnergy();
                        animalCount++;
                    }
                }
            }
        }

        double res = animalCount > 0 ? (double) energySum / animalCount : 0;
        res *= 100;
        res = Math.round(res);

        return res / 100;
    }


    private double getAverageLifespanForDeadAnimals() {
        double res = deadAnimalCount > 0 ? (double) deadAnimalSumAge / deadAnimalCount : 0;
        res *= 100;
        res = Math.round(res);

        return res / 100;
    }

    private synchronized double getAverageChildrenAmountForLivingAnimals() {
        int childrenSum = 0;
        int animalCount = 0;

        synchronized (animals) {
            for (List<Animal> animalsList : animals.values()) {
                if (animalsList != null) {
                    for (Animal animal : animalsList) {
                        childrenSum += animal.getChildrenAmount();
                        animalCount++;
                    }
                }
            }
        }

        double res = animalCount > 0 ? (double) childrenSum / animalCount : 0;
        res *= 100;
        res = Math.round(res);

        return res / 100;
    }
}
