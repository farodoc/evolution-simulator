package agh.ics.oop;

import agh.ics.oop.model.*;

import java.util.*;

public class Simulation implements Runnable{
    private final Settings s;
    private boolean isOver = false;

    public void endSimulation(){
        isOver = true;
    }
    private final int ANIMAL_STARTING_AMOUNT;
    private final int ANIMAL_STARTING_ENERGY;
    private final int ANIMAL_ENERGY_PER_MOVE;

    private final int ANIMAL_MIN_ENERGY_TO_REPRODUCE;
    private final int ANIMAL_ENERGY_TO_REPRODUCE_COST;

    private final int ANIMAL_GENES_AMOUNT;
    private final boolean LOOPED_GENES_ACTIVE;

    private final int ANIMAL_MIN_MUTATIONS;
    private final int ANIMAL_MAX_MUTATIONS;

    private final int FOOD_GROWTH_PER_DAY;
    private final int FOOD_ENERGY;

    private final AbstractWorldMap map;
    private final List<Animal> animals;

    public Simulation(Settings s)
    {
        this.s = s;
        this.animals = new ArrayList<>();
        this.map = s.getMap();
        this.ANIMAL_STARTING_AMOUNT = s.getAnimalStartingAmount();
        this.ANIMAL_STARTING_ENERGY = s.getAnimalStartingEnergy();
        this.ANIMAL_ENERGY_PER_MOVE = s.getAnimalEnergyPerMove();
        this.ANIMAL_MIN_ENERGY_TO_REPRODUCE = s.getAnimalMinEnergyToReproduce();
        this.ANIMAL_ENERGY_TO_REPRODUCE_COST = s.getAnimalEnergyToReproduce();
        this.ANIMAL_GENES_AMOUNT = s.getAnimalGenesAmount();
        this.LOOPED_GENES_ACTIVE = s.getIsLoopedGenes();
        this.ANIMAL_MIN_MUTATIONS = s.getAnimalMinMutations();
        this.ANIMAL_MAX_MUTATIONS = s.getAnimalMaxMutations();
        this.FOOD_GROWTH_PER_DAY = s.getFoodGrowthPerDay();
        this.FOOD_ENERGY = s.getFoodEnergy();
        generateAnimals();
    }

    private void generateAnimals(){
        map.generateAnimals(s.getAnimalStartingAmount(), s.getAnimalStartingEnergy(),
                s.getAnimalGenesAmount(), s.getIsLoopedGenes());
    }

    public void run(){
        map.initializeDrawMap();
        freezeSimulation();
        while (!isOver){
            clearDeadAnimals();
            if(map.isEveryAnimalDead())
                break;
            moveAllAnimals();
            feedAnimals();
            breedAnimals();
            spawnNewFood();
            freezeSimulation();
            map.initializeDrawMap();
        }
        map.initializeDrawMap();
        System.out.println("END OF SIMULATION - EVERY ANIMAL IS DEAD!");
    }

    private void freezeSimulation(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void breedAnimals() {
        map.breedAnimals(s.getAnimalMinEnergyToReproduce(), s.getAnimalEnergyToReproduce(),
                s.getAnimalGenesAmount(), s.getIsLoopedGenes(), s.getAnimalMinMutations(),
                s.getAnimalMaxMutations());
    }

    private void feedAnimals(){
        map.feedAnimals(s.getFoodEnergy());
    }

    private void moveAllAnimals(){
        map.moveAllAnimals(s.getAnimalEnergyPerMove());
    }

    private void clearDeadAnimals(){
        map.clearDeadAnimals();
    }

    private void spawnNewFood(){
        map.generateFood(FOOD_GROWTH_PER_DAY);
    }
}