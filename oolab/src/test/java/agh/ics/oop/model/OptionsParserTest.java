package agh.ics.oop.model;

import agh.ics.oop.OptionsParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OptionsParserTest
{
    @Test
    public void testParse()
    {
        //valid directions, correct result
        String[] input1 = {"f", "l", "l", "b"};
        List<MoveDirection> result1 = List.of(MoveDirection.FORWARD,MoveDirection.LEFT,MoveDirection.LEFT,MoveDirection.BACKWARD);
        assertEquals(result1, OptionsParser.parse(input1));

        //invalid directions
        String[] input2 = {"x", "y", "z"};
        List<MoveDirection> result2 = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, ()-> OptionsParser.parse(input2));

        //mixed directions
        String[] input3 = {"x", "b", "y", "f", "r", "z"};
        List<MoveDirection> result3 = List.of(MoveDirection.BACKWARD,MoveDirection.FORWARD,MoveDirection.RIGHT);
        assertThrows(IllegalArgumentException.class, ()-> OptionsParser.parse(input3));
    }
}
