package agh.ics.oop.model;

public class Settings {
    private final String name;
    private final AbstractWorldMap map;
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

    public Settings(String configName, String[] config) throws Exception{
        name = configName;
        mapWidth = Integer.parseInt(config[0]);
        mapHeight = Integer.parseInt(config[1]);
        animalStartingAmount = Integer.parseInt(config[2]);
        animalStartingEnergy = Integer.parseInt(config[3]);
        animalEnergyPerMove = Integer.parseInt(config[4]);
        animalMinEnergyToReproduce = Integer.parseInt(config[5]);
        animalEnergyToReproduce = Integer.parseInt(config[6]);
        animalGenesAmount = Integer.parseInt(config[7]);
        animalMinMutations = Integer.parseInt(config[8]);
        animalMaxMutations = Integer.parseInt(config[9]);
        foodStartingAmount = Integer.parseInt(config[10]);
        foodGrowthPerDay = Integer.parseInt(config[11]);
        foodEnergy = Integer.parseInt(config[12]);

        checkConfigValues();

        switch (config[13]){
            case "Equator map" -> map = new EquatorMap(foodStartingAmount, mapWidth, mapHeight);
            case "Poison map" -> map = new PoisonMap(foodStartingAmount, mapWidth, mapHeight);
            default -> throw new Exception("Blad przy wyborze mapy");
        }

        switch (config[14]){
            case "Default" -> genes = new StandardGenes(animalGenesAmount);
            case "Looped" -> genes = new LoopedGenes(animalGenesAmount);
            default -> throw new Exception("Blad przy wyborze zachowania zwierzaka");
        }
    }

    private void checkConfigValues() throws Exception {
        if (mapWidth <= 0 || mapHeight <= 0 || animalStartingAmount <= 0 || animalStartingEnergy <= 0 || animalEnergyPerMove < 0 ||
                animalMinEnergyToReproduce <= 0 || animalEnergyToReproduce <= 0 || animalGenesAmount <= 0 || animalMinMutations < 0 ||
                animalMaxMutations < 0 || foodStartingAmount < 0 || foodGrowthPerDay < 0 || foodEnergy < 0) {
            throw new Exception("Blad: wartosci <=0");
        }

        if (foodStartingAmount > mapWidth * mapHeight) {
            throw new Exception("Za duzo jedzenia jak na taka mape");
        }

        if (animalEnergyToReproduce > animalMinEnergyToReproduce) {
            throw new Exception("Blad wartosci kosztu oraz minimalnej energii do reprodukcji");
        }

        if (animalMinMutations > animalMaxMutations || animalMaxMutations > animalGenesAmount) {
            throw new Exception("Blad wartosci minimalnych/maksymalnych mutacji");
        }
    }
}
