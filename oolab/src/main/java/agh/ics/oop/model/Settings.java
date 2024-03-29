package agh.ics.oop.model;

public class Settings {
    private final String name;
    private final boolean isDefaultMap;
    private final boolean isDefaultGenes;
    private final int mapWidth;
    private final int mapHeight;
    private final int animalStartingAmount;
    private final int animalStartingEnergy;
    private final int animalEnergyPerMove;
    private final int animalMinEnergyToReproduce;
    private final int animalEnergyToReproduce;
    private final int animalGenesAmount;
    private final int animalMinMutations;
    private final int animalMaxMutations;
    private final int foodStartingAmount;
    private final int foodGrowthPerDay;
    private final int foodEnergy;
    private final int refreshTime;
    private final boolean saveStats;

    public Settings(String[] config){
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
        isDefaultMap = Boolean.parseBoolean(config[14]);
        isDefaultGenes = Boolean.parseBoolean(config[15]);
        refreshTime = Integer.parseInt(config[16]);
        saveStats = Boolean.parseBoolean(config[17]);
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
                String.valueOf(isDefaultMap),
                String.valueOf(isDefaultGenes),
                String.valueOf(refreshTime),
                String.valueOf(saveStats)
        };
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public boolean getIsSaveStats() {return saveStats;}
    public String getName() {return name;}
    public boolean isDefaultMap() {return isDefaultMap;}
    public int getAnimalStartingAmount() {return animalStartingAmount;}
    public int getAnimalStartingEnergy() {return animalStartingEnergy;}
    public int getAnimalEnergyPerMove() {return animalEnergyPerMove;}
    public int getAnimalMinEnergyToReproduce() {return animalMinEnergyToReproduce;}
    public int getAnimalEnergyToReproduce() {return animalEnergyToReproduce;}
    public int getAnimalGenesAmount() {return animalGenesAmount;}
    public boolean isDefaultGenes() {return isDefaultGenes;}
    public int getAnimalMinMutations() {return animalMinMutations;}
    public int getAnimalMaxMutations() {return animalMaxMutations;}
    public int getFoodGrowthPerDay() {return foodGrowthPerDay;}
    public int getFoodEnergy() {return foodEnergy;}
    public int getRefreshTime(){return refreshTime;}
    public int getFoodStartingAmount(){return foodStartingAmount;}
}