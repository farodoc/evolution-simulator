package agh.ics.oop;

import agh.ics.oop.model.*;

import java.util.*;

public class Simulation implements Runnable{
    private static final boolean LOOPED_GENES_ACTIVE = false;
    private static final int FOOD_GROWTH_PER_DAY = 5;
    private static final int FOOD_ENERGY = 20;
    private static final int ANIMAL_STARTING_AMOUNT = 6;
    private static final int ANIMAL_STARTING_ENERGY = 80;
    private static final int ANIMAL_GENES_AMOUNT = 10;
    private static final int ANIMAL_ENERGY_PER_MOVE = 5;
    private static final int ANIMAL_MIN_ENERGY_TO_REPRODUCE = 15;
    private static final int ANIMAL_ENERGY_TO_REPRODUCE = 5;
    private static final int ANIMAL_MIN_MUTATIONS = 0;
    private static final int ANIMAL_MAX_MUTATIONS = 2;
    private final AbstractWorldMap map;
    private final List<Animal> animals;

    public Simulation(AbstractWorldMap map) {
        this.animals = new ArrayList<>();
        this.map = map;
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
        strongerAnimal.updateAnimalAfterBreeding(ANIMAL_ENERGY_TO_REPRODUCE);
        weakerAnimal.updateAnimalAfterBreeding(ANIMAL_ENERGY_TO_REPRODUCE);

        Animal child = new Animal(strongerAnimal.getPosition(), 2 * ANIMAL_ENERGY_TO_REPRODUCE, ANIMAL_GENES_AMOUNT, LOOPED_GENES_ACTIVE,
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
