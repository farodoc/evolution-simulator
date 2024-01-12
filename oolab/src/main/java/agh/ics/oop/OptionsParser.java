package agh.ics.oop;

import agh.ics.oop.model.MoveDirection;

import java.util.ArrayList;
import java.util.List;


public class OptionsParser {
    public static List<MoveDirection> parse(String[] args) {
        List<MoveDirection> directions = new ArrayList<>();
        for (String arg : args) {
            switch (arg) {
                case "0" -> directions.add(MoveDirection.FORWARD);
                case "1" -> directions.add(MoveDirection.FORWARD_RIGHT);
                case "2" -> directions.add(MoveDirection.RIGHT);
                case "3" -> directions.add(MoveDirection.BACKWARD_RIGHT);
                case "4" -> directions.add(MoveDirection.BACKWARD);
                case "5" -> directions.add(MoveDirection.BACKWARD_LEFT);
                case "6" -> directions.add(MoveDirection.LEFT);
                case "7" -> directions.add(MoveDirection.FORWARD_LEFT);
                default -> throw new IllegalArgumentException(arg + " is not legal move specification");
            }
        }
        return directions;
    }
}