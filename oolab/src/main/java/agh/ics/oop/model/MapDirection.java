package agh.ics.oop.model;

public enum MapDirection
{
    NORTH,
    SOUTH,
    EAST,
    WEST;

    public String toString()
    {
        return switch(this)
        {
            case EAST -> "Wschód";
            case WEST -> "Zachód";
            case NORTH -> "Północ";
            case SOUTH -> "Południe";
        };
    }

    public MapDirection next()
    {
        return switch(this)
        {
            case EAST -> SOUTH;
            case WEST -> NORTH;
            case NORTH -> EAST;
            case SOUTH -> WEST;
        };
    }

    public MapDirection previous()
    {
        return switch(this)
        {
            case EAST -> NORTH;
            case WEST -> SOUTH;
            case NORTH -> WEST;
            case SOUTH -> EAST;
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
        };
    }
}
