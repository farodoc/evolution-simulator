package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Animal implements WorldElement{
    private MapDirection orientation;
    private Vector2d position;
    private AbstractGenes genes;
    private int energy;
    private int age = 0;
    private int childrenAmount = 0;
    private int descendantAmount = 0;
    private final static int MAX_ENERGY = 100;
    private final int genesAmount;
    private final List<Animal> parents = new ArrayList<>(2);
    public Animal(Vector2d position, int startingEnergy, int genesAmount, boolean loopedGenesActive)
    {
        this.position = position;
        this.genesAmount = genesAmount;
        orientation = MapDirection.generateRandomMapDirection();
        energy = startingEnergy;

        if(loopedGenesActive) genes = new LoopedGenes(genesAmount);
        else                  genes = new StandardGenes(genesAmount);
    }

    public Animal(Vector2d position, int energy, int genesAmount, boolean loopedGenesActive, Animal parent1, Animal parent2, int animalMinMutations, int animalMaxMutations)
    {
        this(position, energy, genesAmount, loopedGenesActive);
        parents.add(0, parent1);
        parents.add(1, parent2);

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

        int direction = genes.getActiveGene();
        changeOrientation(direction);

        Vector2d possiblePositionWithPoisonedFruit = map.getNewPositionForAnimal(this);
        AbstractFood food = foodTiles.getOrDefault(possiblePositionWithPoisonedFruit, new Grass(new Vector2d(-69,-69)));
        if("X".equals(food.toString()) && Math.random()< 0.2)
        {
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
    public int getMaxEnergy() {return MAX_ENERGY;}
    public int getAge() {return age;}
    public int getChildrenAmount() {return childrenAmount;}
    public int getDescendantAmount() {return descendantAmount;}
    public int getGenesAmount() {return genesAmount;}
    public AbstractGenes getGenes() {return genes;}
    public void eat(int energy){
        this.energy = Math.min(MAX_ENERGY, this.energy + energy);
    }
    public void updateAge(){
        this.age++;
    }
    public void updateDescendantAmount(){this.descendantAmount++;}

    private void updateParentsRecursion(List<Animal> visitedAnimals)
    {
        if(parents.isEmpty())
            return;

        Animal parent1 = parents.get(0);
        if(!visitedAnimals.contains(parent1))
        {
            parent1.updateDescendantAmount();
            visitedAnimals.add(parent1);
            parent1.updateParentsRecursion(visitedAnimals);
        }

        Animal parent2 = parents.get(1);
        if(!visitedAnimals.contains(parent2))
        {
            parent2.updateDescendantAmount();
            visitedAnimals.add(parent2);
            parent2.updateParentsRecursion(visitedAnimals);
        }

    }
    public void updateAnimalAfterBreeding(int ANIMAL_ENERGY_TO_REPRODUCE)
    {
        this.childrenAmount++;
        this.energy -= ANIMAL_ENERGY_TO_REPRODUCE;
        updateParentsRecursion(new ArrayList<>());

    }

}