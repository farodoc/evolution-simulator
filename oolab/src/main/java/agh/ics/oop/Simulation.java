package agh.ics.oop;

import agh.ics.oop.model.*;

import java.util.*;

public class Simulation implements Runnable{

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

    public Simulation(AbstractWorldMap map, int ANIMAL_STARTING_AMOUNT,
                      int ANIMAL_STARTING_ENERGY, int ANIMAL_ENERGY_PER_MOVE,
                      int ANIMAL_MIN_ENERGY_TO_REPRODUCE, int ANIMAL_ENERGY_TO_REPRODUCE_COST,
                      int ANIMAL_GENES_AMOUNT, boolean LOOPED_GENES_ACTIVE,
                      int ANIMAL_MIN_MUTATIONS, int ANIMAL_MAX_MUTATIONS,
                      int FOOD_GROWTH_PER_DAY, int FOOD_ENERGY)
    {
        this.animals = new ArrayList<>();
        this.map = map;
        this.ANIMAL_STARTING_AMOUNT = ANIMAL_STARTING_AMOUNT;
        this.ANIMAL_STARTING_ENERGY = ANIMAL_STARTING_ENERGY;
        this.ANIMAL_ENERGY_PER_MOVE = ANIMAL_ENERGY_PER_MOVE;
        this.ANIMAL_MIN_ENERGY_TO_REPRODUCE = ANIMAL_MIN_ENERGY_TO_REPRODUCE;
        this.ANIMAL_ENERGY_TO_REPRODUCE_COST = ANIMAL_ENERGY_TO_REPRODUCE_COST;
        this.ANIMAL_GENES_AMOUNT = ANIMAL_GENES_AMOUNT;
        this.LOOPED_GENES_ACTIVE = LOOPED_GENES_ACTIVE;
        this.ANIMAL_MIN_MUTATIONS = ANIMAL_MIN_MUTATIONS;
        this.ANIMAL_MAX_MUTATIONS = ANIMAL_MAX_MUTATIONS;
        this.FOOD_GROWTH_PER_DAY = FOOD_GROWTH_PER_DAY;
        this.FOOD_ENERGY = FOOD_ENERGY;
        generateAnimals();
    }

    private void generateAnimals(){
        int mapWidth = map.getMapWidth();
        int mapHeight = map.getMapHeight();

        for(int i = 0; i < ANIMAL_STARTING_AMOUNT; i++){
            int x = (int)(Math.random() * mapWidth);
            int y = (int)(Math.random() * mapHeight);
            Animal animal = new Animal(new Vector2d(x, y), ANIMAL_STARTING_ENERGY, ANIMAL_GENES_AMOUNT, LOOPED_GENES_ACTIVE);
            map.place(animal);
            animals.add(animal);
        }
    }

    public void run(){
        freezeSimulation();
        while (true){
            clearDeadAnimals();
            if(!animals.isEmpty()) moveAllAnimals(); else break;
            feedAnimals();
            breedAnimals();
            spawnNewFood();
            freezeSimulation();
        }
        spawnNewFood();
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
        HashSet<Vector2d> bredPositions = new HashSet<>();
        List<Animal> animalsCopy = new ArrayList<>(animals);

        for(Animal animal: animalsCopy){
            if(!bredPositions.contains(animal.getPosition())) {
                List<Animal> filteredAnimals = findAnimalsToBreed(animal.getPosition());
                int pairs = filteredAnimals.size() / 2;
                int animalIndex = 0;

                for (int i = 0; i < pairs; i++) {
                    combineAnimalsAndSpawnChild(filteredAnimals.get(animalIndex), filteredAnimals.get(animalIndex + 1));
                    animalIndex += 2;
                }
                bredPositions.add(animal.getPosition());
            }
        }
    }

    private List<Animal> findAnimalsToBreed(Vector2d position){
        List<Animal> filteredAnimals = new ArrayList<>();

        for (Animal animal : animals) {
            if (animal.getPosition().equals(position) && animal.getEnergy() >= ANIMAL_MIN_ENERGY_TO_REPRODUCE) {
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

    private void combineAnimalsAndSpawnChild(Animal strongerAnimal, Animal weakerAnimal){
        strongerAnimal.updateAnimalAfterBreeding(ANIMAL_ENERGY_TO_REPRODUCE_COST);
        weakerAnimal.updateAnimalAfterBreeding(ANIMAL_ENERGY_TO_REPRODUCE_COST);

        Animal child = new Animal(strongerAnimal.getPosition(), 2 * ANIMAL_ENERGY_TO_REPRODUCE_COST, ANIMAL_GENES_AMOUNT, LOOPED_GENES_ACTIVE,
                strongerAnimal, weakerAnimal, ANIMAL_MIN_MUTATIONS, ANIMAL_MAX_MUTATIONS);
        map.place(child);
        animals.add(child);
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
                .thenComparingInt(Animal::getChildrenAmount)
                .reversed();

        filteredAnimals.sort(animalComparator);

        return filteredAnimals.get(0);
    }
    private void moveAllAnimals(){
        for(Animal animal : animals){
            map.move(animal, ANIMAL_ENERGY_PER_MOVE);
            animal.updateAge();
        }
    }

    private void clearDeadAnimals(){
        for(int i = animals.size()-1; i >= 0; i--){
            if(animals.get(i).getEnergy() <= 0){
                map.removeAnimal(animals.get(i));
                animals.remove(i);
            }
        }
    }

    private void spawnNewFood()
    {
        map.generateFood(FOOD_GROWTH_PER_DAY);
    }
}
