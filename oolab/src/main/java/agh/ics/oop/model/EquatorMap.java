package agh.ics.oop.model;

public class EquatorMap extends AbstractWorldMap{

    public EquatorMap(int FOOD_STARTING_AMOUNT, int mapWidth, int mapHeight) {
        super(mapWidth, mapHeight);
        generateTiles();
        generateFood(FOOD_STARTING_AMOUNT);
    }

    @Override
    protected void generateJungleTiles(){
        int jungleTilesAmount = (int) (mapHeight*mapWidth*0.2);
        int jungleTilesCounter = 0;
        double probabilityForRow = 1.0;
        int equator = mapHeight/2;

        boolean generateUpper = true;
        int yModifier = 0;

        while(jungleTilesCounter < jungleTilesAmount){
            int x=0;
            while(x<mapWidth && jungleTilesCounter<jungleTilesAmount){
                if(Math.random() < probabilityForRow){
                    jungleTilesCounter++;
                    tiles[equator+yModifier][x] = TileType.JUNG;
                }
                x++;
            }

            if(generateUpper){
                yModifier += 1;
                yModifier *= (-1);
                generateUpper = false;
            }
            else{
                yModifier *= (-1);
                generateUpper = true;
            }

            if(!isInMap(equator, yModifier)){
                yModifier = 0;
                System.out.println(1);
            }

            probabilityForRow /= ((double) (mapHeight + 5) / mapHeight);
        }
    }

    @Override
    public void generateFood(int howManyFoodToGenerate){
        int cnt = 0;
        while(cnt < howManyFoodToGenerate){
            Vector2d newFoodPosition = generateNewFoodPosition();
            if(newFoodPosition == null) break;

            if(!foodTiles.containsKey(newFoodPosition))
            {
                foodTiles.put(newFoodPosition,new Grass(newFoodPosition));
                cnt++;
            }
        }
    }

    @Override
    public String getName() {return "Equator map";}
}