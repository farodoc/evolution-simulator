package agh.ics.oop.model.exceptions;

import agh.ics.oop.model.Vector2d;

import java.util.Vector;

public class PositionAlreadyOccupiedException extends Exception{
    public PositionAlreadyOccupiedException(Vector2d position){
        super("Position " + position + " is already occupied");
    }
}
