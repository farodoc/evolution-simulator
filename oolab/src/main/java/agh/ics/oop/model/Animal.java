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
    @Override
    public String toString() {
        return switch (this.orientation){
            case NORTH -> "^";
            case SOUTH -> "v";
            case EAST -> ">";
            case WEST -> "<";
        };
    }
    @Override
    public boolean isAt(Vector2d position){
        return this.position.equals(position);
    }

    public void move(MoveDirection direction, MoveValidator validator){
        switch (direction){
            case LEFT -> this.orientation = this.orientation.previous();
            case RIGHT -> this.orientation = this.orientation.next();
            case FORWARD -> {
                Vector2d newPosition = this.position.add(this.orientation.toUnitVector());
                if(validator.canMoveTo(newPosition)){
                    this.position = newPosition;
                }
            }
            case BACKWARD -> {
                Vector2d newPosition = this.position.subtract(this.orientation.toUnitVector());
                if(validator.canMoveTo(newPosition)){
                    this.position = newPosition;
                }
            }
        }
    }
}