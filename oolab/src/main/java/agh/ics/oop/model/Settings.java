package agh.ics.oop.model;

public class Settings {
    private final String name;
    private AbstractWorldMap map;
    private final int mapWidth;
    private final int mapHeight;
    private final int animalStartingAmount;
    private final int animalStartingEnergy;
    private final int animalEnergyPerMove;
    private final int animalMinEnergyToReproduce;
    private final int animalEnergyToReproduce;
    private final int animalGenesAmount;
    private final AbstractGenes genes;
    private final int animalMinMutations;
    private final int animalMaxMutations;
    private final int foodStartingAmount;
    private final int foodGrowthPerDay;
    private final int foodEnergy;

    public Settings(String[] config) throws IllegalArgumentException{
        name = config[0];
        mapWidth = Integer.parseInt(config[1]);
        mapHeight = Integer.parseInt(config[2]);
        animalStartingAmount = Integer.parseInt(config[3]);
        animalStartingEnergy = Integer.parseInt(config[4]);
        animalEnergyPerMove = Integer.parseInt(config[5]);
        animalMinEnergyToReproduce = Integer.parseInt(config[6]);
        animalEnergyToReproduce = Integer.parseInt(config[7]);
        animalGenesAmount = Integer.parseInt(config[8]);
        animalMinMutations = Integer.parseInt(config[9]);
        animalMaxMutations = Integer.parseInt(config[10]);
        foodStartingAmount = Integer.parseInt(config[11]);
        foodGrowthPerDay = Integer.parseInt(config[12]);
        foodEnergy = Integer.parseInt(config[13]);

        checkConfigValues();

        switch (config[14]){
            case "Equator map" -> map = new EquatorMap(foodStartingAmount, mapWidth, mapHeight);
            case "Poison map" -> map = new PoisonMap(foodStartingAmount, mapWidth, mapHeight);
            default -> throw new IllegalArgumentException("Blad przy wyborze mapy");
        }

        switch (config[15]){
            case "Default" -> genes = new StandardGenes(animalGenesAmount);
            case "Looped" -> genes = new LoopedGenes(animalGenesAmount);
            default -> throw new IllegalArgumentException("Blad przy wyborze zachowania zwierzaka");
        }
    }

    public Settings(String[] config, AbstractWorldMap map) throws IllegalArgumentException{
        this(config);
        this.map = map;
    }

    private void checkConfigValues() throws IllegalArgumentException {
        if (mapWidth <= 0 || mapHeight <= 0 || animalStartingAmount <= 0 || animalStartingEnergy <= 0 || animalEnergyPerMove < 0 ||
                animalMinEnergyToReproduce <= 0 || animalEnergyToReproduce <= 0 || animalGenesAmount <= 0 || animalMinMutations < 0 ||
                animalMaxMutations < 0 || foodStartingAmount < 0 || foodGrowthPerDay < 0 || foodEnergy < 0) {
            throw new IllegalArgumentException("Blad: wartosci <=0");
        }

        if (foodStartingAmount > mapWidth * mapHeight) {
            throw new IllegalArgumentException("Za duzo jedzenia jak na taka mape");
        }

        if (animalEnergyToReproduce > animalMinEnergyToReproduce) {
            throw new IllegalArgumentException("Blad wartosci kosztu oraz minimalnej energii do reprodukcji");
        }

        if (animalMinMutations > animalMaxMutations || animalMaxMutations > animalGenesAmount) {
            throw new IllegalArgumentException("Blad wartosci minimalnych/maksymalnych mutacji");
        }
    }

    public String[] getAttributesAsArray() {
        return new String[]{
                name,
                String.valueOf(mapWidth),
                String.valueOf(mapHeight),
                String.valueOf(animalStartingAmount),
                String.valueOf(animalStartingEnergy),
                String.valueOf(animalEnergyPerMove),
                String.valueOf(animalMinEnergyToReproduce),
                String.valueOf(animalEnergyToReproduce),
                String.valueOf(animalGenesAmount),
                String.valueOf(animalMinMutations),
                String.valueOf(animalMaxMutations),
                String.valueOf(foodStartingAmount),
                String.valueOf(foodGrowthPerDay),
                String.valueOf(foodEnergy),
                map.getName(),
                genes.getName()
        };
    }


    public String getName() {
        return name;
    }

    public AbstractWorldMap getMap() {
        return map;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getAnimalStartingAmount() {
        return animalStartingAmount;
    }

    public int getAnimalStartingEnergy() {
        return animalStartingEnergy;
    }

    public int getAnimalEnergyPerMove() {
        return animalEnergyPerMove;
    }

    public int getAnimalMinEnergyToReproduce() {
        return animalMinEnergyToReproduce;
    }

    public int getAnimalEnergyToReproduce() {
        return animalEnergyToReproduce;
    }

    public int getAnimalGenesAmount() {
        return animalGenesAmount;
    }

    public boolean getIsLoopedGenes() {
        return genes.getName().equals("Looped");
    }

    public int getAnimalMinMutations() {
        return animalMinMutations;
    }

    public int getAnimalMaxMutations() {
        return animalMaxMutations;
    }

    public int getFoodStartingAmount() {
        return foodStartingAmount;
    }

    public int getFoodGrowthPerDay() {
        return foodGrowthPerDay;
    }

    public int getFoodEnergy() {
        return foodEnergy;
    }
}