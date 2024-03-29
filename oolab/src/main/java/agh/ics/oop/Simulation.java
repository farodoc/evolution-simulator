package agh.ics.oop;

import agh.ics.oop.model.*;
import java.io.FileWriter;
import java.io.IOException;

public class Simulation implements Runnable {
    private final Settings s;
    private final AbstractWorldMap map;
    private boolean isActive = true;
    private static int simulationCount = 1;
    private static final String CSV_FILE_PREFIX = "src/main/resources/stats/savedStats";
    private final String CSV_FILE;
    public boolean getIsActive() {
        return isActive;
    }

    public void changeState() {
        isActive = !isActive;
    }

    public Simulation(Settings s) {
        this.s = s;

        if(s.isDefaultMap()){
            this.map = new EquatorMap(s.getFoodStartingAmount(), s.getMapWidth(), s.getMapHeight());
        } else {
            this.map = new PoisonMap(s.getFoodStartingAmount(), s.getMapWidth(), s.getMapHeight());
        }

        this.CSV_FILE = CSV_FILE_PREFIX + simulationCount + ".csv";
        simulationCount++;
        generateAnimals();
    }

    private void generateAnimals() {
        map.generateAnimals(s.getAnimalStartingAmount(), s.getAnimalStartingEnergy(),
                s.getAnimalGenesAmount(), s.isDefaultGenes());
    }

    public void run() {
        try {
            saveStatsTitleToFile();
            saveStatsToFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        map.initializeDrawMap();
        freezeSimulation();
        map.nextDay();

        while (true) {
            if (!isActive) {
                freezeSimulation();
                continue;
            }

            clearDeadAnimals();
            if (map.isEveryAnimalDead()) break;
            moveAllAnimals();
            feedAnimals();
            breedAnimals();
            spawnNewFood();
            map.initializeDrawMap();

            if (s.getIsSaveStats()) {
                try {
                    saveStatsToFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            freezeSimulation();
            map.nextDay();
        }
        map.initializeDrawMap();
        System.out.println("END OF SIMULATION - EVERY ANIMAL IS DEAD!");
    }

    private void freezeSimulation() {
        try {
            Thread.sleep(s.getRefreshTime());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void breedAnimals() {
        map.breedAnimals(s.getAnimalMinEnergyToReproduce(), s.getAnimalEnergyToReproduce(),
                s.getAnimalGenesAmount(), s.isDefaultGenes(), s.getAnimalMinMutations(),
                s.getAnimalMaxMutations());
    }

    private void feedAnimals() {
        map.feedAnimals(s.getFoodEnergy());
    }

    private void moveAllAnimals() {
        map.moveAllAnimals(s.getAnimalEnergyPerMove());
    }

    private void clearDeadAnimals() {
        map.clearDeadAnimals();
    }

    private void spawnNewFood() {
        map.generateFood(s.getFoodGrowthPerDay());
    }

    public void subscribe(MapChangeListener observer) {
        map.addObserver(observer);
    }

    public AbstractWorldMap getMap() {
        return this.map;
    }

    private void saveStatsTitleToFile() throws IOException {
        FileWriter writer = new FileWriter(CSV_FILE);

        String title = "Day;Current animal amount;Overall animal amount;Current plants amount;Free tiles amount;" +
                "Most popular genotype;Average energy level;Average lifespan of dead animals;Average children amount";

        writer.write(title);
        writer.flush();
        writer.close();
    }

    private void saveStatsToFile() throws IOException {
        MapStatsInString mapStats = map.getCurrentStats();

        FileWriter writer = new FileWriter(CSV_FILE, true);

        String line = mapStats.day() + ";" +
                mapStats.currentAnimalCount() + ";" +
                mapStats.foodTileCount() + ";" +
                mapStats.freeTileCount() + ";" +
                mapStats.mostFrequentGenotype() + ";" +
                mapStats.averageEnergy() + ";" +
                mapStats.averageLifespan() + ";" +
                mapStats.averageChildrenAmount() + ";" +
                mapStats.totalAnimalCount() + "\n";

        writer.write(line);

        writer.flush();
        writer.close();
    }
}