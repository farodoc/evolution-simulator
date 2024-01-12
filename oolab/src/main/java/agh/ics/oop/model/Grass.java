package agh.ics.oop.model;

public class Grass extends AbstractFood{
    public Grass(Vector2d position) {
        super(position);
    }

    public String toString(){
        return "*";
    }
}