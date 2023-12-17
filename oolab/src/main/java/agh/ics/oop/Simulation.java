package agh.ics.oop;

import agh.ics.oop.model.*;

import java.util.*;

public class Simulation implements Runnable{
    private static final int FOOD_STARTING_AMOUNT = 10;
    private static final int FOOD_GROWTH_PER_DAY = 7;
    private static final int FOOD_ENERGY = 5;
    private static final int ANIMAL_STARTING_AMOUNT = 10;
    private static final int ANIMAL_STARTING_ENERGY = 9;
    private static final int ANIMAL_GENES_AMOUNT = 10;
    private static final int ANIMAL_ENERGY_PER_MOVE = 1;
    private static final int ANIMAL_MIN_ENERGY_TO_REPRODUCE = 5;
    private static final int ANIMAL_ENERGY_TO_REPRODUCE = 2;
    private DarvinsMap map;
    private List<Animal> animals;


    List<Animal> getAnimals(){
        return Collections.unmodifiableList(this.animals);
    }
    public Simulation(List<Vector2d> positions, DarvinsMap map) {
        this.animals = new ArrayList<>();
        this.map = map;

        for(Vector2d position : positions){
            Animal animal = new Animal(position, ANIMAL_STARTING_ENERGY, ANIMAL_GENES_AMOUNT);
            map.place(animal);
            animals.add(animal);
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
            if(!animals.isEmpty()) moveAllAnimals();
            else break;
            feedAnimals();
            breedAnimals();
            spawnNewFood();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("KONIEC SYMULACJI - ZWIERZETA WYGINELY!");
    }

    private void breedAnimals() {
    }

    private void feedAnimals()
    {
        Map<Vector2d, AbstractFood> foodTiles;
        for(Animal animal: animals)
        {
            foodTiles = map.getFoodTiles();
            if(foodTiles.containsKey(animal.getPosition()))
            {
                AbstractFood food = foodTiles.get(animal.getPosition());
                Animal animalThatEats = conflictManager(animal.getPosition());

                if(Objects.equals(food.toString(), "X"))
                    map.feedAnimal(animalThatEats, -FOOD_ENERGY);
                else
                    map.feedAnimal(animalThatEats, FOOD_ENERGY);
            }
        }
    }

    private Animal conflictManager(Vector2d position)
    {
        List<Animal> filteredAnimals = new ArrayList<>();

        for (Animal animal : animals) {
            if (animal.getPosition().equals(position)) {
                filteredAnimals.add(animal);
            }
        }

        Comparator<Animal> animalComparator = Comparator
                .comparingInt(Animal::getEnergy)
                .thenComparingInt(Animal::getAge)
                .thenComparingInt(Animal::getChildrenAmount);

        Collections.sort(filteredAnimals, animalComparator);

        return filteredAnimals.get(0);
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

    private void spawnNewFood()
    {
        map.generateFood(FOOD_GROWTH_PER_DAY);
    }
}
