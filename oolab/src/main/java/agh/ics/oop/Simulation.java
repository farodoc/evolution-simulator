package agh.ics.oop;

import agh.ics.oop.model.*;

public class Simulation implements Runnable{
    private final Settings s;
    private final AbstractWorldMap map;


    public Simulation(Settings s)
    {
        this.s = s;
        this.map = s.getMap();
        generateAnimals();
    }

    private void generateAnimals(){
        map.generateAnimals(s.getAnimalStartingAmount(), s.getAnimalStartingEnergy(),
                s.getAnimalGenesAmount(), s.getIsLoopedGenes());
    }

    public void run(){
        map.initializeDrawMap();
        freezeSimulation();
        while (true){
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
        map.generateFood(s.getFoodGrowthPerDay());
    }
}