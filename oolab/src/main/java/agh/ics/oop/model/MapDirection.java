package agh.ics.oop.model;

public enum MapDirection
{
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;

    public String toString()
    {
        return switch(this)
        {
            case EAST -> "E";
            case WEST -> "W";
            case NORTH -> "N";
            case SOUTH -> "S";
            case NORTH_EAST -> "NE";
            case NORTH_WEST -> "NW";
            case SOUTH_EAST -> "SE";
            case SOUTH_WEST -> "SW";
        };
    }

    public MapDirection next()
    {
        return switch(this)
        {
            case NORTH -> NORTH_EAST;
            case NORTH_EAST -> EAST;
            case EAST -> SOUTH_EAST;
            case SOUTH_EAST -> SOUTH;
            case SOUTH -> SOUTH_WEST;
            case SOUTH_WEST -> WEST;
            case WEST -> NORTH_WEST;
            case NORTH_WEST -> NORTH;
        };
    }

    public MapDirection previous()
    {
        return switch(this)
        {
            case NORTH_EAST -> NORTH;
            case EAST -> NORTH_EAST;
            case SOUTH_EAST -> EAST;
            case SOUTH -> SOUTH_EAST;
            case SOUTH_WEST -> SOUTH;
            case WEST -> SOUTH_WEST;
            case NORTH_WEST -> WEST;
            case NORTH -> NORTH_WEST;
        };
    }

    public MapDirection reverse(){
        return switch(this)
        {
            case NORTH_EAST -> SOUTH_WEST;
            case EAST -> WEST;
            case SOUTH_EAST -> NORTH_WEST;
            case SOUTH -> NORTH;
            case SOUTH_WEST -> NORTH_EAST;
            case WEST -> EAST;
            case NORTH_WEST -> SOUTH_EAST;
            case NORTH -> SOUTH;
        };
    }

    public Vector2d toUnitVector()
    {
        return switch(this)
        {
            case EAST -> new Vector2d(1,0);
            case WEST -> new Vector2d(-1,0);
            case NORTH -> new Vector2d(0,1);
            case SOUTH -> new Vector2d(0,-1);
            case NORTH_EAST -> new Vector2d(1,1);
            case NORTH_WEST -> new Vector2d(-1,1);
            case SOUTH_EAST -> new Vector2d(1,-1);
            case SOUTH_WEST -> new Vector2d(-1,-1);
        };
    }

    public static MapDirection generateRandomMapDirection(){
        int number = (int)(Math.random()*8);
        return switch(number)
        {
            case 0 -> EAST;
            case 1 -> WEST;
            case 2 -> NORTH;
            case 3 -> SOUTH;
            case 4 -> NORTH_EAST;
            case 5 -> NORTH_WEST;
            case 6 -> SOUTH_EAST;
            case 7 -> SOUTH_WEST;
            default -> NORTH;
        };
    }
}
