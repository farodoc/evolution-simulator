package agh.ics.oop;

import agh.ics.oop.model.*;
import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simulation implements Runnable{
    private List<Animal> animals;
    private List<MoveDirection> moves;
    private int currentAnimalIndex;
    private WorldMap map;

    List<Animal> getAnimals(){
        return Collections.unmodifiableList(this.animals);
    }
    public Simulation(List<Vector2d> positions, List<MoveDirection> moves, WorldMap map) {
        this.moves = moves;
        this.animals = new ArrayList<>();
        this.map = map;

        for(Vector2d position : positions){
            Animal animal = new Animal(position);
            try {
                map.place(animal);
                animals.add(animal);
            }
            catch (PositionAlreadyOccupiedException ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    public void run(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for(MoveDirection move : moves){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Animal currentAnimal = animals.get(currentAnimalIndex);
            map.move(currentAnimal, move);
            currentAnimalIndex = (currentAnimalIndex + 1) % animals.size();
        }
    }
}
