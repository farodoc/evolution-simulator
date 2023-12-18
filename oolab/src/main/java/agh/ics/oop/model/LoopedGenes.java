package agh.ics.oop.model;

public class LoopedGenes extends AbstractGenes{
    private boolean leftToRightGenes = true;
    public LoopedGenes(int genesAmount) {
        super(genesAmount);
    }

    public LoopedGenes(Animal strongerAnimal, Animal weakerAnimal, int ANIMAL_MIN_MUTATIONS, int ANIMAL_MAX_MUTATIONS) {
        super(strongerAnimal, weakerAnimal, ANIMAL_MIN_MUTATIONS, ANIMAL_MAX_MUTATIONS);
    }

    @Override
    protected final void updateGeneIndex(){
        if(leftToRightGenes){
            if(geneIndex == genes.size() - 1){
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
}
