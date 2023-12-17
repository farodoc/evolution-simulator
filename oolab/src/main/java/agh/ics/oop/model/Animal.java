package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Animal implements WorldElement{
    private MapDirection orientation;
    private Vector2d position;
    private int energy;
    private int age;
    private int childrenAmount;
    private int MAX_ENERGY = 100;
    private List<Integer> genes = new ArrayList<>();
    private int geneIndex = 0;
    private boolean leftToRightGenes = true;
    private int genesAmount;

    public Animal(Vector2d position, int energy, int genesAmount)
    {
        this.position = position;
        this.orientation = MapDirection.generateRandomMapDirection();
        this.energy = energy;
        this.genesAmount = genesAmount;
        generateGenesOnStart();
    }

    public Animal(Vector2d position, int energy, int genesAmount, List<Integer> genes)
    {
        this(position, energy, genesAmount);
        this.genes = genes;
    }

    private void generateGenesOnStart(){
        for(int i = 0; i < genesAmount; i++){
            int newGene = (int)(Math.random() * 8);
            genes.add(newGene);
        }
    }

    private void updateGeneIndex(){
        if(leftToRightGenes){
            if(geneIndex == genesAmount - 1){
                leftToRightGenes = false;
            }
            else{
                geneIndex++;
            }
        }
        else{
            if(geneIndex == 0){
                leftToRightGenes = true;
            }
            else{
                geneIndex--;
            }
        }
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

    public void move(DarvinMap map, int ANIMAL_ENERGY_PER_MOVE, Map<Vector2d, AbstractFood> foodTiles){
        if(this.energy - ANIMAL_ENERGY_PER_MOVE >= 0){
            this.energy -= ANIMAL_ENERGY_PER_MOVE;
        }
        else{
            this.energy = 0;
            return;
        }

        int direction = genes.get(geneIndex);
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
        updateGeneIndex();
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


    public int getEnergy(){
        return this.energy;
    }
    public void setEnergy(int energy){ this.energy = energy; }
    public int getAge() {return age;}
    public int getChildrenAmount() {return childrenAmount;}

    public void eat(int energy){
        this.energy = Math.min(MAX_ENERGY, this.energy + energy);
    }

    public void updateAge(){
        this.age++;
    }

    public List<Integer> getGenes(){
        return this.genes;
    }
}