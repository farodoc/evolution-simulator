package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.List;

public class Animal implements WorldElement{
    private MapDirection orientation;
    private Vector2d position;
    private int energy;
    private List<Integer> genes = new ArrayList<>();
    private int geneIndex = 0;
    private boolean leftToRightGenes = true;
    private int genesAmount;

    public Animal(Vector2d position, int energy, int genesAmount)
    {
        this.position = position;
        this.orientation = MapDirection.NORTH;
        this.energy = energy;
        this.genesAmount = genesAmount;
        generateGenesOnStart();
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

    public void move(MoveValidator validator){
        int direction = genes.get(geneIndex);
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

        this.position = validator.getNewPositionForAnimal(this);
        updateGeneIndex();
    }
}