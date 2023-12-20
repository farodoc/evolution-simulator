package agh.ics.oop.model;

import java.util.Random;

public class PoisonMap extends AbstractWorldMap {
    private static final double POISON_PROBABILITY = 0.1;

    public PoisonMap(int FOOD_STARTING_AMOUNT, int mapWidth, int mapHeight) {
        super(mapWidth, mapHeight);
        generateTiles();
        generateFood(FOOD_STARTING_AMOUNT);
    }

    @Override
    public void generateFood(int howManyFoodToGenerate)
    {
        int cnt = 0;
        while(cnt < howManyFoodToGenerate){
            Vector2d newFoodPosition = generateNewFoodPosition();
            if(newFoodPosition == null) break;

            if(!foodTiles.containsKey(newFoodPosition))
            {
                if(Math.random() < POISON_PROBABILITY) //generate poisonedFruit
                    foodTiles.put(newFoodPosition, new PoisonedFruit(newFoodPosition));

                else foodTiles.put(newFoodPosition,new Grass(newFoodPosition));

                notifyObservers("Food generated at " + newFoodPosition);
                cnt++;
            }
        }
    }

    @Override
    protected void generateJungleTiles()
    {
        double jungleTilesAmount = mapHeight*mapWidth*0.2;
        int a = (int) Math.sqrt(jungleTilesAmount); //a = lengthOfSquare
        a = Math.min(a, mapHeight);
        a = Math.min(a, mapWidth);
        Random random = new Random();
        int startingY = random.nextInt(mapHeight-a+1);
        int startingX = random.nextInt(mapWidth-a+1);

        for(int y=startingY; y<startingY+a; y++)
        {
            for(int x=startingX; x<startingX+a; x++)
            {
                tiles[y][x] = TileType.JUNG;
            }
        }
    }
}
