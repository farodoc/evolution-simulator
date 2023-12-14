package agh.ics.oop;

import agh.ics.oop.model.*;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {
    @Test
    public void testSimulation() {
        //TEST 1 - CHECKING POSITION
        System.out.println("TEST 1 \n");
        List<MoveDirection> directions1 = OptionsParser.parse(new String[]{"f", "b", "r", "l", "f", "f", "r", "r", "f", "f", "f", "f", "f", "f", "f", "f"});
        List<Vector2d> positions1 = List.of(new Vector2d(2, 2), new Vector2d(3, 4));
        RectangularMap map1 = new RectangularMap(4, 4);
        Simulation simulation1 = new Simulation(positions1, directions1, map1);
        simulation1.run();
        List<Animal> animals1 = simulation1.getAnimals();
        Vector2d position1_1 = animals1.get(0).getPosition();
        Vector2d position1_2 = animals1.get(1).getPosition();

        assertEquals(new Vector2d(2, 0), position1_1);
        assertEquals(new Vector2d(3, 4), position1_2);

        //TEST 2 - CHECKING POSITION AND ORIENTATION
        System.out.println("TEST 2 \n");
        List<MoveDirection> directions2 = OptionsParser.parse(new String[]{"f", "b", "r", "l", "f", "b", "l", "f"});
        List<Vector2d> positions2 = List.of(new Vector2d(1, 0), new Vector2d(0, 0));
        RectangularMap map2 = new RectangularMap(5, 5);
        Simulation simulation2 = new Simulation(positions2, directions2, map2);
        simulation2.run();
        List<Animal> animals2 = simulation2.getAnimals();
        Vector2d position2_1 = animals2.get(0).getPosition();
        Vector2d position2_2 = animals2.get(1).getPosition();
        MapDirection orientation2_1 = animals2.get(0).getOrientation();
        MapDirection orientation2_2 = animals2.get(1).getOrientation();

        assertEquals(new Vector2d(2, 1), position2_1);
        assertEquals(new Vector2d(0, 0), position2_2);
        assertEquals(MapDirection.NORTH, orientation2_1);
        assertEquals(MapDirection.WEST, orientation2_2);

        //TEST 3 - SPAWNING 2 ANIMALS AT THE SAME POSITION
        System.out.println("TEST 3 \n");
        List<MoveDirection> directions3 = OptionsParser.parse(new String[]{"f", "b", "r", "l", "f", "b", "l", "f"});
        List<Vector2d> positions3 = List.of(new Vector2d(0, 0), new Vector2d(0, 0));
        RectangularMap map3 = new RectangularMap(5, 5);
        Simulation simulation3 = new Simulation(positions3, directions3, map3);
        simulation3.run();
        List<Animal> animals3 = simulation3.getAnimals();
        Vector2d position3_1 = animals3.get(0).getPosition();
        MapDirection orientation3_1 = animals3.get(0).getOrientation();

        assertEquals(new Vector2d(0, 0), position3_1);
        assertEquals(MapDirection.WEST, orientation3_1);
    }
}