package agh.ics.oop.model;

public class StandardGenes extends AbstractGenes{
    public StandardGenes(int genesAmount) {
        super(genesAmount);
    }

    public StandardGenes(Animal strongerAnimal, Animal weakerAnimal, int ANIMAL_MIN_MUTATIONS, int ANIMAL_MAX_MUTATIONS) {
        super(strongerAnimal, weakerAnimal, ANIMAL_MIN_MUTATIONS, ANIMAL_MAX_MUTATIONS);
    }

    public String getName() {return "Default";}
}
