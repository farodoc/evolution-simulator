package agh.ics.oop;

import agh.ics.oop.model.*;
import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simulation implements Runnable{
    private static final int STARTING_FOOD_AMOUNT = 10;
    private static final int FOOD_GROWTH_PER_DAY = 2;
    private static final int ENERGY_FROM_FOOD = 5;
    private static final int ANIMAL_STARTING_AMOUNT = 10;
    private static final int ANIMAL_STARTING_ENERGY = 1;
    private static final int ANIMAL_GENES_AMOUNT = 1;
    private static final int ANIMAL_ENERGY_PER_MOVE = 1;
    private static final int ANIMAL_MIN_ENERGY_TO_REPRODUCE = 5;
    private static final int ANIMAL_ENERGY_TO_REPRODUCE = 2;
    private List<Animal> animals;
    private WorldMap map;

    List<Animal> getAnimals(){
        return Collections.unmodifiableList(this.animals);
    }
    public Simulation(List<Vector2d> positions, WorldMap map) {
        this.animals = new ArrayList<>();
        this.map = map;

        for(Vector2d position : positions){
            Animal animal = new Animal(position, ANIMAL_STARTING_ENERGY, ANIMAL_GENES_AMOUNT);
            try {
                map.place(animal);
                animals.add(animal);
            }
            catch (PositionAlreadyOccupiedException ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    public void run(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while (true){
            clearDeadAnimals();
            if(!animals.isEmpty()){
                moveAllAnimals();
            }
            else {
                break;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void moveAllAnimals(){
        for(Animal animal : animals){
            map.move(animal, ANIMAL_ENERGY_PER_MOVE);
        }
    }

    private void clearDeadAnimals(){
        for(int i = 0; i < animals.size(); i++){
            if(animals.get(i).getEnergy() <= 0){
                animals.remove(i);
            }
        }
    }


}
