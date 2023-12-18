package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class AbstractGenes {
    protected List<Integer> genes = new ArrayList<>();
    protected int geneIndex = 0;

    public AbstractGenes(int genesAmount){
        generateGenesOnStart(genesAmount);
    }

    public AbstractGenes(Animal strongerAnimal, Animal weakerAnimal, int ANIMAL_MIN_MUTATIONS, int ANIMAL_MAX_MUTATIONS){
        this.genes = combineGenes(strongerAnimal, weakerAnimal);
        switchRandomGenes(genes, ANIMAL_MIN_MUTATIONS, ANIMAL_MAX_MUTATIONS);
    }

    protected void generateGenesOnStart(int genesAmount){
        for(int i = 0; i < genesAmount; i++){
            int newGene = (int)(Math.random() * 8);
            genes.add(newGene);
        }
    }

    protected void updateGeneIndex(){
        geneIndex = (geneIndex + 1) % genes.size();
    }

    public int getActiveGene(){
        int resultGene = genes.get(geneIndex);
        updateGeneIndex();
        return resultGene;
    }

    public List<Integer> getGenesList(){
        return genes;
    }

    protected List<Integer> combineGenes(Animal strongerAnimal, Animal weakerAnimal){
        List<Integer> strongerGenes = strongerAnimal.getGenes().getGenesList();
        List<Integer> weakerGenes = weakerAnimal.getGenes().getGenesList();
        List<Integer> newGenes = new ArrayList<>();
        int genesAmount = strongerAnimal.getGenesAmount();
        int strongerGenesAmount = (int)(strongerAnimal.getEnergy()/(double)(strongerAnimal.getEnergy() + weakerAnimal.getEnergy())) * genesAmount;
        int weakerGenesAmount = genesAmount - strongerGenesAmount;
        boolean drawnLeftSide = Math.random() < 0.5;

        if(drawnLeftSide){
            for(int i = 0; i < strongerGenesAmount; i++){
                newGenes.add(strongerGenes.get(i));
            }
            for(int i = strongerGenesAmount; i < genesAmount; i++){
                newGenes.add(weakerGenes.get(i));
            }
        }
        else{
            for(int i = 0; i < weakerGenesAmount; i++){
                newGenes.add(weakerGenes.get(i));
            }
            for(int i = weakerGenesAmount; i < genesAmount; i++){
                newGenes.add(strongerGenes.get(i));
            }
        }

        return newGenes;
    }

    protected void switchRandomGenes(List<Integer> genes, int ANIMAL_MIN_MUTATIONS, int ANIMAL_MAX_MUTATIONS){
        int genesAmount = genes.size();
        Random random = new Random();
        int genesToSwitchAmount = ANIMAL_MIN_MUTATIONS + random.nextInt(ANIMAL_MAX_MUTATIONS - ANIMAL_MIN_MUTATIONS + 1);
        List<Integer> genesPositions = new ArrayList<>();

        for(int i = 0; i < genesAmount; i++){
            genesPositions.add(i);
        }

        Collections.shuffle(genesPositions);

        for(int i = 0; i < genesToSwitchAmount; i++){
            genes.set(i, (genes.get(i) + random.nextInt(1, genesAmount)) % genesAmount);
        }
    }
}
