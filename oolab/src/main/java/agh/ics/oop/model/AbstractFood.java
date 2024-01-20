package agh.ics.oop.model;

public abstract class AbstractFood implements WorldElement{
    protected final Vector2d position;

    public AbstractFood(Vector2d position) {this.position = position;}
    @Override
    public Vector2d getPosition(){return this.position;}
    @Override
    public boolean isAt(Vector2d position){return this.position.equals(position);}
}
