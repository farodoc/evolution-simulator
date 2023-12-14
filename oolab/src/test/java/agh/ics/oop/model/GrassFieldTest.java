package agh.ics.oop.model;

import agh.ics.oop.model.exceptions.PositionAlreadyOccupiedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrassFieldTest {

    @Test
    void canMoveTo() throws PositionAlreadyOccupiedException {
        GrassField map = new GrassField(10);
        Animal animal = new Animal(new Vector2d(4, 4));
        map.place(animal);

        assertTrue(map.canMoveTo(new Vector2d(2, 2)));
        assertTrue(map.canMoveTo(new Vector2d(21, 37)));
        assertFalse(map.canMoveTo(new Vector2d(4, 4)));
    }

    @Test
    void place() throws PositionAlreadyOccupiedException {
        GrassField map = new GrassField(10);
        Animal animal1 = new Animal(new Vector2d(4, 4));
        Animal animal2 = new Animal(new Vector2d(2, 4));
        Animal animal3 = new Animal(new Vector2d(4, 4));
        Animal animal4 = new Animal(new Vector2d(10, 41));
        map.place(animal1);
        map.place(animal2);
        map.place(animal4);

        assertThrows(PositionAlreadyOccupiedException.class, ()->map.place(animal3));
        assertEquals(3, map.getAnimals().size());
    }

    @Test
    void isOccupied() throws PositionAlreadyOccupiedException {
        GrassField map = new GrassField(0);
        Animal animal = new Animal(new Vector2d(4, 4));
        map.place(animal);

        assertTrue(map.isOccupied(animal.getPosition()));
        assertFalse(map.isOccupied(new Vector2d(2, 2)));
    }

    @Test
    void objectAt() throws PositionAlreadyOccupiedException {
        GrassField map = new GrassField(0);
        Animal animal = new Animal(new Vector2d(4, 4));
        map.place(animal);

        assertEquals(animal, map.objectAt(animal.getPosition()));
        assertNull(map.objectAt(new Vector2d(2, 2)));
    }

    @Test
    void move() throws PositionAlreadyOccupiedException {
        GrassField map = new GrassField(10);
        Animal animal1 = new Animal(new Vector2d(4, 4));
        Vector2d newPosition1 = new Vector2d(4,5);
        Vector2d newPosition2 = new Vector2d(4,4);
        map.place(animal1);
        map.move(animal1, MoveDirection.FORWARD);
        assertTrue(map.isOccupied(newPosition1));
        map.move(animal1, MoveDirection.BACKWARD);
        assertTrue(map.isOccupied(newPosition2));
    }
}