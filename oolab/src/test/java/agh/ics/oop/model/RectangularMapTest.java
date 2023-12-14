package agh.ics.oop.model;

import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RectangularMapTest {
    RectangularMap map = new RectangularMap(4,6);
    Animal animal1 = new Animal(new Vector2d(2,2));
    Animal animal2 = new Animal(new Vector2d(2,3));
    Animal animal3 = new Animal(new Vector2d(0,0));
    Animal animal4 = new Animal(new Vector2d(-1,2));
    Animal animal5 = new Animal(new Vector2d(2,2));
    @Test
    void canMoveTo() throws PositionAlreadyOccupiedException {
        assertTrue(map.canMoveTo(animal1.getPosition()));
        assertFalse(map.canMoveTo(animal4.getPosition()));
        assertTrue(map.canMoveTo(animal3.getPosition()));
        map.place(animal1);
        assertFalse(map.canMoveTo(animal5.getPosition()));
    }

    @Test
    void place() throws PositionAlreadyOccupiedException {
        map.place(animal1);
        assertThrows(PositionAlreadyOccupiedException.class, ()->map.place(animal5));
    }

    @Test
    void move() throws PositionAlreadyOccupiedException {
        map.place(animal1);
        map.move(animal1, MoveDirection.FORWARD);
        assertTrue(map.isOccupied(animal2.getPosition()));
        map.move(animal1, MoveDirection.BACKWARD);
        assertTrue(map.isOccupied(animal5.getPosition()));
    }

    @Test
    void isOccupied() throws PositionAlreadyOccupiedException {
        map.place(animal1);
        assertTrue(map.isOccupied(new Vector2d(2,2)));
        assertFalse(map.isOccupied(new Vector2d(2,3)));
        assertThrows(PositionAlreadyOccupiedException.class, ()->map.place(animal4));
        assertFalse(map.isOccupied(animal4.getPosition()));
    }

    @Test
    void objectAt() throws PositionAlreadyOccupiedException {
        map.place(animal1);
        assertEquals(map.objectAt(animal1.getPosition()), animal1);
        assertNull(map.objectAt(animal2.getPosition()));
    }
}