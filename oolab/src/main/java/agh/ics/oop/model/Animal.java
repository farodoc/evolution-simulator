package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Animal implements WorldElement{
    private static int overallID = 0;
    private final int ID;
    private MapDirection orientation;
    private Vector2d position;
    private AbstractGenes genes;
    private int energy;
    private int age = 0;
    private int childrenAmount = 0;
    private int descendantAmount = 0;
    private int maxEnergy;
    private final int genesAmount;
    private int plantsEaten = 0;
    private Integer deathDate = null;
    private final List<Animal> parents = new ArrayList<>(2);
    public Animal(Vector2d position, int startingEnergy, int genesAmount, boolean loopedGenesActive){
        ID = overallID++;
        this.position = position;
        this.genesAmount = genesAmount;
        orientation = MapDirection.generateRandomMapDirection();
        energy = startingEnergy;
        maxEnergy = (int) (1.33*energy);

        if(loopedGenesActive) genes = new LoopedGenes(genesAmount);
        else                  genes = new StandardGenes(genesAmount);
    }

    public Animal(Vector2d position, int energy, int genesAmount, boolean loopedGenesActive, Animal parent1, Animal parent2, int animalMinMutations, int animalMaxMutations){
        this(position, energy, genesAmount, loopedGenesActive);
        parents.add(0, parent1);
        parents.add(1, parent2);
        maxEnergy = parent1.getMaxEnergy();

        if(loopedGenesActive) genes = new LoopedGenes(parent1, parent2, animalMinMutations, animalMaxMutations);
        else                  genes = new StandardGenes(parent1, parent2, animalMinMutations, animalMaxMutations);

        updateParentsRecursion(new ArrayList<>());
    }

    @Override
    public Vector2d getPosition(){
        return this.position;
    }
    public MapDirection getOrientation(){
        return this.orientation;
    }
    public void setOrientation(MapDirection orientation){
        this.orientation = orientation;
    }
    @Override
    public String toString() {
        return "A";
    }
    @Override
    public boolean isAt(Vector2d position){
        return this.position.equals(position);
    }

    public void move(AbstractWorldMap map, int ANIMAL_ENERGY_PER_MOVE, Map<Vector2d, AbstractFood> foodTiles){
        if(this.energy - ANIMAL_ENERGY_PER_MOVE >= 0){
            this.energy -= ANIMAL_ENERGY_PER_MOVE;
        }
        else{
            this.energy = 0;
            return;
        }

        int direction = genes.getActiveGeneAndUpdateGene();
        changeOrientation(direction);

        Vector2d possiblePositionWithPoisonedFruit = map.getNewPositionForAnimal(this);
        AbstractFood food = foodTiles.getOrDefault(possiblePositionWithPoisonedFruit, new Grass(new Vector2d(-69,-69)));
        if("X".equals(food.toString()) && Math.random()< 0.2){
            int newDirection = (1 + (int)(Math.random()*7));
            changeOrientation(newDirection);
            possiblePositionWithPoisonedFruit = map.getNewPositionForAnimal(this);
        }

        this.position = possiblePositionWithPoisonedFruit;
    }

    private void changeOrientation(int direction) {
        switch (direction){
            case 0 -> {}
            case 1 -> this.orientation = this.orientation.next();
            case 2 -> this.orientation = this.orientation.next().next();
            case 3 -> this.orientation = this.orientation.reverse().previous();
            case 4 -> this.orientation = this.orientation.reverse();
            case 5 -> this.orientation = this.orientation.reverse().next();
            case 6 -> this.orientation = this.orientation.previous().previous();
            case 7 -> this.orientation = this.orientation.previous();
        }
    }

    public int getEnergy() {return energy;}
    public int getMaxEnergy() {return maxEnergy;}
    public int getAge() {return age;}
    public int getChildrenAmount() {return childrenAmount;}
    public int getDescendantAmount() {return descendantAmount;}
    public int getGenesAmount() {return genesAmount;}
    public AbstractGenes getGenes() {return genes;}
    public int getPlantsEaten() {return plantsEaten;}
    public void updateAge(){
        this.age++;
    }
    public void setDeathDate(int deathDate) {this.deathDate = deathDate;}
    public String getDeathDate() {return deathDate == null ? "N/A" : String.valueOf(deathDate);}
    private void updateDescendantAmount(){this.descendantAmount++;}
    public void eat(int energy){
        this.energy = Math.min(maxEnergy, this.energy + energy);
        plantsEaten++;
    }
    private void updateParentsRecursion(List<Animal> visitedAnimals){
        if(parents.isEmpty())
            return;

        Animal parent1 = parents.get(0);
        if(!visitedAnimals.contains(parent1)){
            parent1.updateDescendantAmount();
            visitedAnimals.add(parent1);
            parent1.updateParentsRecursion(visitedAnimals);
        }

        Animal parent2 = parents.get(1);
        if(!visitedAnimals.contains(parent2)){
            parent2.updateDescendantAmount();
            visitedAnimals.add(parent2);
            parent2.updateParentsRecursion(visitedAnimals);
        }
    }

    public void updateAnimalAfterBreeding(int ANIMAL_ENERGY_TO_REPRODUCE){
        this.childrenAmount++;
        this.energy -= ANIMAL_ENERGY_TO_REPRODUCE;
    }

    public String[] getAnimalStats() {
        String[] stats = new String[10];
        stats[0] = String.valueOf(ID);
        stats[1] = position.toString();
        stats[2] = String.valueOf(genes.getGenesList());
        stats[3] = String.valueOf(genes.getActiveGene());
        stats[4] = String.valueOf(energy);
        stats[5] = String.valueOf(plantsEaten);
        stats[6] = String.valueOf(childrenAmount);
        stats[7] = String.valueOf(descendantAmount);
        stats[8] = String.valueOf(age);
        stats[9] = getDeathDate();

        return stats;
    }
}