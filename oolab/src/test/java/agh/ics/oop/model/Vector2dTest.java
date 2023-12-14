package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Vector2dTest
{
    @Test
    public void testEquals()
    {
        Vector2d v1 = new Vector2d(1, 2);
        Vector2d v2 = new Vector2d(1, 2);
        Vector2d v3 = new Vector2d(3, 4);

        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
        assertFalse(v2.equals(v3));
    }

    @Test
    public void testToString()
    {
        Vector2d v = new Vector2d(1, 2);
        assertEquals(v.toString(), "(1,2)");
    }

    @Test
    public void testPrecedes()
    {
        Vector2d v1 = new Vector2d(1, 2);
        Vector2d v2 = new Vector2d(3, 4);
        Vector2d v3 = new Vector2d(1, 4);
        Vector2d v4 = new Vector2d(1, 2);

        assertTrue(v1.precedes(v2));
        assertFalse(v2.precedes(v1));
        assertTrue(v1.precedes(v3));
        assertTrue(v1.precedes(v1));
    }

    @Test
    public void testFollows()
    {
        Vector2d v1 = new Vector2d(1, 2);
        Vector2d v2 = new Vector2d(3, 4);
        Vector2d v3 = new Vector2d(1, 4);
        Vector2d v4 = new Vector2d(1, 4);

        assertFalse(v1.follows(v2));
        assertTrue(v2.follows(v1));
        assertFalse(v1.follows(v3));
        assertTrue(v3.follows(v4));
    }

    @Test
    public void testAdd()
    {
        Vector2d v1 = new Vector2d(1, 2);
        Vector2d v2 = new Vector2d(3, 4);

        Vector2d result = v1.add(v2);
        assertEquals(result, new Vector2d(4, 6));
    }

    @Test
    public void testSubtract()
    {
        Vector2d v1 = new Vector2d(3, 5);
        Vector2d v2 = new Vector2d(1, 2);

        Vector2d result1 = v1.subtract(v2);
        Vector2d result2 = v2.subtract(v1);

        assertEquals(result1, new Vector2d(2, 3));
        assertEquals(result2, new Vector2d(-2, -3));
    }

    @Test
    public void testUpperRight()
    {
        Vector2d v1 = new Vector2d(1, 3);
        Vector2d v2 = new Vector2d(3, 1);

        Vector2d result = v1.upperRight(v2);
        assertEquals(result, new Vector2d(3, 3));
    }

    @Test
    public void testLowerLeft()
    {
        Vector2d v1 = new Vector2d(1, 3);
        Vector2d v2 = new Vector2d(3, 1);

        Vector2d result = v1.lowerLeft(v2);
        assertEquals(result, new Vector2d(1, 1));
    }

    @Test
    public void testOpposite()
    {
        Vector2d v1 = new Vector2d(1, -2);
        Vector2d v2 = new Vector2d(0, 1);

        Vector2d result1 = v1.opposite();
        Vector2d result2 = v2.opposite();

        assertEquals(result1, new Vector2d(-1, 2));
        assertEquals(result2, new Vector2d(0, -1));
    }
}
