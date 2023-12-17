package agh.ics.oop;

import agh.ics.oop.model.*;

import java.util.*;

public class Simulation implements Runnable{
    //private static final int FOOD_STARTING_AMOUNT = 10;
    private static final int FOOD_GROWTH_PER_DAY = 1;
    private static final int FOOD_ENERGY = 5;
    private static final int ANIMAL_STARTING_AMOUNT = 1;
    private static final int ANIMAL_STARTING_ENERGY = 35;
    private static final int ANIMAL_GENES_AMOUNT = 10;
    private static final int ANIMAL_ENERGY_PER_MOVE = 1;
    private static final int ANIMAL_MIN_ENERGY_TO_REPRODUCE = 5;
    private static final int ANIMAL_ENERGY_TO_REPRODUCE = 2;
    private static final int ANIMAL_MIN_MUTATIONS = 0;
    private static final int ANIMAL_MAX_MUTATIONS = 2;
    private DarvinMap map;
    private List<Animal> animals;


    List<Animal> getAnimals(){
        return Collections.unmodifiableList(this.animals);
    }
    public Simulation(DarvinMap map) {
        this.animals = new ArrayList<>();
        this.map = map;
        generateAnimals();
    }

    private void generateAnimals(){
        int mapSize = map.getMapSize();
        for(int i = 0; i < ANIMAL_STARTING_AMOUNT; i++){
            int x = (int)(Math.random() * mapSize);
            int y = (int)(Math.random() * mapSize);
            Animal animal = new Animal(new Vector2d(x, y), ANIMAL_STARTING_ENERGY, ANIMAL_GENES_AMOUNT);
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
            if(!animals.isEmpty()) moveAllAnimals(); else break;
            feedAnimals();
            breedAnimals();//nie dziala
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            spawnNewFood();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("KONIEC SYMULACJI - ZWIERZETA WYGINELY!");
    }

    private void breedAnimals(Vector2d position) {
        List<Animal> filteredAnimals = new ArrayList<>();

        for (Animal animal : animals) {
            if (animal.getPosition().equals(position) && animal.getEnergy() >= ANIMAL_MIN_ENERGY_TO_REPRODUCE) {
                filteredAnimals.add(animal);
            }
        }

        Comparator<Animal> animalComparator = Comparator
                .comparingInt(Animal::getEnergy)
                .thenComparingInt(Animal::getAge)
                .thenComparingInt(Animal::getChildrenAmount);

        Collections.sort(filteredAnimals, animalComparator);

        int pairs = filteredAnimals.size() / 2;
        int animalIndex = 0;

        for(int i = 0; i < pairs; i++){
            combineAnimals(filteredAnimals.get(animalIndex), filteredAnimals.get(animalIndex + 1));
            animalIndex += 2;
        }
    }

    private Animal combineAnimals(Animal animal1, Animal animal2){
        List<Integer> newGenes = combineGenes(animal1, animal2);
        animal1.setEnergy(animal1.getEnergy() - ANIMAL_ENERGY_TO_REPRODUCE);
        animal2.setEnergy(animal1.getEnergy() - ANIMAL_ENERGY_TO_REPRODUCE);
        Animal child = new Animal(animal1.getPosition(), ANIMAL_ENERGY_TO_REPRODUCE, ANIMAL_GENES_AMOUNT, newGenes);
        map.place(child);
        animals.add(child);
        return child;
    }

    private List<Integer> combineGenes(Animal strongerAnimal, Animal weakerAnimal){
        List<Integer> newGenes = new ArrayList<>();
        int strongerGenesAmount = (int)(strongerAnimal.getEnergy()/(double)(strongerAnimal.getEnergy() + weakerAnimal.getEnergy())) * ANIMAL_GENES_AMOUNT;
        int weakerGenesAmount = ANIMAL_GENES_AMOUNT - strongerGenesAmount;
        boolean drawnLeftSide = Math.random() < 0.5;
        if(drawnLeftSide){
            for(int i = 0; i < strongerGenesAmount; i++){
                newGenes.add(strongerAnimal.getGenes().get(i));
            }
            for(int i = strongerGenesAmount; i < ANIMAL_GENES_AMOUNT; i++){
                newGenes.add(weakerAnimal.getGenes().get(i));
            }
        }
        else{
            for(int i = 0; i < weakerGenesAmount; i++){
                newGenes.add(weakerAnimal.getGenes().get(i));
            }
            for(int i = weakerGenesAmount; i < ANIMAL_GENES_AMOUNT; i++){
                newGenes.add(strongerAnimal.getGenes().get(i));
            }
        }
        switchRandomGenes(newGenes);
        return newGenes;
    }

    private void switchRandomGenes(List<Integer> genes){
        Random random = new Random();
        int genesToSwitchAmount = ANIMAL_MIN_MUTATIONS + random.nextInt(ANIMAL_MAX_MUTATIONS - ANIMAL_MIN_MUTATIONS + 1);
        List<Integer> genesPositions = new ArrayList<>();

        for(int i = 0; i < ANIMAL_GENES_AMOUNT; i++){
            genesPositions.add(i);
        }

        Collections.shuffle(genesPositions);

        for(int i = 0; i < genesToSwitchAmount; i++){
            genes.set(i, (genes.get(i) + random.nextInt(1, ANIMAL_GENES_AMOUNT)) % ANIMAL_GENES_AMOUNT);
        }
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
