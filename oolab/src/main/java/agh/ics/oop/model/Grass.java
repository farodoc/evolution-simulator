package agh.ics.oop.model;

public class Grass implements WorldElement{
    private final Vector2d position;

    public Grass(Vector2d position) {
        this.position = position;
    }

    @Override
    public Vector2d getPosition(){
        return this.position;
    }

    public String toString(){
        return "*";
    }

    @Override
    public boolean isAt(Vector2d position){
        return this.position.equals(position);
    }
}