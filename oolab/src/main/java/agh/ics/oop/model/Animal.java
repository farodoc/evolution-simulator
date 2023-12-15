package agh.ics.oop.model;

public class Animal implements WorldElement{
    private MapDirection orientation;
    private Vector2d position;

    public Animal(Vector2d position)
    {
        this.position = position;
        this.orientation = MapDirection.NORTH;
    }

    public Animal()
    {
        this(new Vector2d(2,2));
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

    public void move(MoveDirection direction, MoveValidator validator){
        switch (direction){
            case FORWARD -> {}
            case FORWARD_RIGHT -> this.orientation = this.orientation.next();
            case RIGHT -> this.orientation = this.orientation.next().next();
            case BACKWARD_RIGHT -> this.orientation = this.orientation.reverse().previous();
            case BACKWARD -> this.orientation = this.orientation.reverse();
            case BACKWARD_LEFT -> this.orientation = this.orientation.reverse().next();
            case LEFT -> this.orientation = this.orientation.previous().previous();
            case FORWARD_LEFT -> this.orientation = this.orientation.previous();
        }

        this.position = validator.getNewPositionForAnimal(this);
    }
}