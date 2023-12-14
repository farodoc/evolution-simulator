package agh.ics.oop.model;

public class RectangularMap extends AbstractWorldMap{
    private final Vector2d BOTTOM_LEFT_CORNER;
    private final Vector2d TOP_RIGHT_CORNER;
    public RectangularMap(int width, int height){
        super();
        BOTTOM_LEFT_CORNER = new Vector2d(0,0);
        TOP_RIGHT_CORNER = new Vector2d(width - 1, height - 1);
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return super.canMoveTo(position) && position.follows(BOTTOM_LEFT_CORNER) && position.precedes(TOP_RIGHT_CORNER);
    }

    @Override
    public Boundary getCurrentBounds(){
        return new Boundary(BOTTOM_LEFT_CORNER, TOP_RIGHT_CORNER);
    }
}