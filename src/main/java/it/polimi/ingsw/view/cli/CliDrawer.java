package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.ResourceType;
import it.polimi.ingsw.model.market.MarbleColor;
import it.polimi.ingsw.utility.ConfigParameters;
import it.polimi.ingsw.utility.Pair;
import it.polimi.ingsw.view.SimpleGameState;
import it.polimi.ingsw.view.SimplePlayerState;

import java.util.HashMap;
import java.util.Map;

public class CliDrawer {

  private final int PLAYERBOARD_LENGTH = 120;
  private final int PLAYERBOARD_HEIGHT = 16;
  private final int MARKET_LENGTH = 11;
  private final int MARKET_HEIGHT = 5;
  private final int MAX_DISPLAYABLE_LENGTH = 200;
  private final int MAX_DISPLAYABLE_HEIGHT = 20;
  private final Color marginColor = Color.RESET;
  private final String[][] canvas;

  private SimpleGameState gameState;
  private Map<String, SimplePlayerState> playerState;

  public CliDrawer(SimpleGameState gameState, Map<String, SimplePlayerState> playerState) {
    this.gameState = gameState;
    this.playerState = playerState;
    this.canvas = new String[MAX_DISPLAYABLE_HEIGHT][MAX_DISPLAYABLE_LENGTH];

    for (int i=0; i<MAX_DISPLAYABLE_HEIGHT; i++)
      for (int j=0; j<MAX_DISPLAYABLE_LENGTH; j++)
        canvas[i][j] = " ";
  }

  //public interface
  public void displayPlainCanvas(){
    placeHereOnCanvas(0,0, canvas);
    displayCanvas();
  }

  public void displayDefaultCanvas(String username) {
    placeHereOnCanvas(0,0, buildMargins(PLAYERBOARD_HEIGHT, PLAYERBOARD_LENGTH));
    setUsernameOnCanvas(username);
    buildWarehouse(username);
    buildChest(username);
    buildAndSetMarket();
    displayCanvas();
  }

  //TODO finire con margin e slide
  public void marketDisplay() {
    MarbleColor[][] market = gameState.getMarket();
    for(int i=0; i<market.length; i++) {
      for (int j = 0; j < market[i].length; j++)
        System.out.print(market[i][j] + " " + Color.RESET.escape());
      System.out.println();
    }
  }




  //private methods
  private void displayCanvas() {
    for (int i = 0; i< MAX_DISPLAYABLE_HEIGHT; i++) {
      for (int j = 0; j < MAX_DISPLAYABLE_LENGTH; j++)
        System.out.print(canvas[i][j]);
      System.out.println();
    }
  }

  private String[][] buildMargins(int row_dim, int col_dim){
    int c, r;
    String[][] margins = new String[row_dim][col_dim];

    margins[0][0] = "╔";
    for (c = 1; c < col_dim - 1; c++)
      margins[0][c] = "═";
    margins[0][c] = "╗";

    for (r = 1; r < row_dim - 1; r++) {
      margins[r][0] = "║";
      for (c = 1; c < col_dim - 1; c++)
        margins[r][c] = " ";
      margins[r][c] = "║";
    }

    margins[r][0] = "╚";
    for (c = 1; c < col_dim - 1; c++)
      margins[r][c] = "═";
    margins[r][c] = "╝";
    return margins;
  }

  private void placeHereOnCanvas(int r, int c, String[][] placeMe) {
    int startRow = r, startCol = c;
    for(int i=0 ; i<placeMe.length; i++, startRow++) {
      startCol = c;
      for (int j=0; j < placeMe[0].length; j++, startCol++)
        canvas[startRow][startCol] = placeMe[i][j];
    }
  }

  private void setUsernameOnCanvas(String username) {
    int row=1, col=3;
    for(char c : username.toCharArray()) {
      canvas[row][col] = Character.toString(c);
      col += 1;
    }

    for(char c : "'s playerboard".toCharArray()) {
      canvas[row][col] = Character.toString(c);
      col += 1;
    }
  }

  private void buildWarehouse(String username) {
    Pair<ResourceType, Integer>[] warehouse = playerState.get(username).getWarehouseLevels();
    for(int i=0; i<warehouse.length; i++) {
      skeletonWarehouse(i);
      fillWarehouse(i, warehouse[i].getKey(), (warehouse[i].getKey() == null) ? 0 : warehouse[i].getValue());
    }
  }

  private void buildChest(String username) {
    ResourceType[] resources = new ResourceType[]{ResourceType.GOLD, ResourceType.SERVANT, ResourceType.SHIELD, ResourceType.STONE};
    Map<ResourceType, Integer> chest = playerState.get(username).getChest();
    placeHereOnCanvas( 11, 3, skeletonChest());

    for(ResourceType r : resources) {
      if(!chest.containsKey(r))
        fillChest(r, 0);
      else
        fillChest(r, chest.get(r));
    }
  }

  private void skeletonWarehouse(int level) {
    int row=level+5, col=3;

    for(char c : "WAREHOUSE".toCharArray()) {
      canvas[4][col] = Character.toString(c);
      col ++;
    }

    col = 3;
    canvas[row][col] = "[";
    col++;
    for (int i=0; i<level; i++, col++) {
      canvas[row][col] = " ";
      col ++;
      canvas[row][col] = "|";
    }
    canvas[row][col] = " ";
    col++;
    canvas[row][col] = ("]");
  }

  private String[][] skeletonChest() {
    int rows = 4, columns = 9;
    int col = 3;
    for(char c : "CHEST".toCharArray()) {
      canvas[10][col] = Character.toString(c);
      col ++;
    }

    String[][] chest = new String[rows][columns];
    chest[0][0] = "\u25A0";
    chest[0][columns-1] = "\u25A0";
    chest[rows-1][0] = "\u25A0";
    chest[rows-1][columns-1] = "\u25A0";

    for (int r = 1; r < rows-1; r++) {
      chest[r][0] = "║";
      for (int c = 1; c < columns-1; c++) {
        if(c == 2 || c == 5)
          chest[r][c] = "0";
        else
          chest[r][c] = " ";
      }
      chest[r][columns-1] = "║";
    }

    for (int c = 1; c < columns-1; c++) {
      chest[0][c] = "\u25A0";
      chest[rows-1][c] = "\u25A0";
    }

    return chest;
  }

  private void fillWarehouse(int level, ResourceType resource, int quantity) {
    int row = level+5;

    for(int col=4; quantity>0; col+=2, quantity--)
      canvas[row][col] = resource.toString();
  }

  private void fillChest(ResourceType resource, int quantity) {
    switch (resource){
      case GOLD:
        canvas[12][5] = Integer.toString(quantity);
        canvas[12][6] = resource.toString();
        break;
      case STONE:
        canvas[12][8] = Integer.toString(quantity);
        canvas[12][9] = resource.toString();
        break;
      case SHIELD:
        canvas[13][5] = Integer.toString(quantity);
        canvas[13][6] = resource.toString();
        break;
      case SERVANT:
        canvas[13][8] = Integer.toString(quantity);
        canvas[13][9] = resource.toString();
        break;
    }
  }

  private void buildAndSetMarket() {
    MarbleColor[][] marketColor = gameState.getMarket();
    String[][] market = buildMargins(MARKET_HEIGHT, MARKET_LENGTH);
    String[][] marketAndSlide = new String[MARKET_HEIGHT+2][MARKET_LENGTH];

    int a=0, b;
    for(int i=1; a<marketColor.length; i++) {
      b=0;
      for (int j=1; b<marketColor[a].length; j++) {
        if(j%2==0) {
          market[i][j] = marketColor[a][b].toString();
          b++;
        }
      }
      a++;
    }

    for(int c=0; c<marketAndSlide[0].length; c++) {
      marketAndSlide[MARKET_HEIGHT][c] = " ";
      marketAndSlide[MARKET_HEIGHT+1][c] = "\u203E";
    }

    marketAndSlide[MARKET_HEIGHT][MARKET_LENGTH-1] = gameState.getSlide().toString();

    for(int i=0; i<market.length; i++)
      for(int j=0; j<market[i].length; j++)
        marketAndSlide[i][j] = market[i][j];

    int col=0;
    for(char c : "slide".toCharArray()) {
      marketAndSlide[MARKET_HEIGHT][col] = Character.toString(c);
      col ++;
    }
    col = col + 2;
    marketAndSlide[MARKET_HEIGHT][col] = ConfigParameters.arrowCharacter;

    col=PLAYERBOARD_LENGTH+7;
    for(char c : "MARKET".toCharArray()) {
      canvas[1][col] = Character.toString(c);
      col ++;
    }

    col = PLAYERBOARD_LENGTH+9;
    int row = 4;
    for(int i=0; i<4; i++) {
      canvas[2][col] = Integer.toString(i+1);
      if(i<3)
        canvas[row][PLAYERBOARD_LENGTH+7+MARKET_LENGTH+1] = Integer.toString(i+1);
      col += 2;
      row++;
    }

    placeHereOnCanvas(3,PLAYERBOARD_LENGTH+7, marketAndSlide);
  }

















  private static int MAX_COLUMN_TILES = 20;
  private static int MAX_ROW_TILES = 5;





  public void printCard(int devCardId) {

    String[][] innerOfCard = cardMargin();

    //DevelopCard dev = deck.getCard(1); //1 is the id
    //dev.getRequirements
//    for(ResourceType r : dev.getRequirements.getKey()) {
//      innerOfCard[3][5]
//    }
//    innerOfCard[3][4] = ;
    innerOfCard[2][4] = "⚫";
    innerOfCard[2][5] = "v";
    innerOfCard[2][6] = "p";

    innerOfCard[3][5] = "→";
    innerOfCard[3][6] = "⚫";
    printCard(innerOfCard);
  }

  private String[][] cardMargin() {
    int c, r;
    String[][] margin = new String[MAX_ROW_TILES][MAX_COLUMN_TILES];

    margin[0][0] = "╔";
    for (c = 1; c < MAX_COLUMN_TILES - 1; c++)
      margin[0][c] = "═";
    margin[0][c] = "╗";

    for (r = 1; r < MAX_ROW_TILES - 1; r++) {
      margin[r][0] = "║";
      for (c = 1; c < MAX_COLUMN_TILES - 1; c++)
        margin[r][c] = " ";
      margin[r][c] = "║";
    }

    margin[r][0] = "╚";
    for (c = 1; c < MAX_COLUMN_TILES - 1; c++)
      margin[r][c] = "═";
    margin[r][c] = "╝";

    return margin;
  }

  private void printCard(String[][] card) {
    for (int r = 0; r < MAX_ROW_TILES; r++) {
      for (int c = 0; c < MAX_COLUMN_TILES; c++)

        //System.out.print(marginColor.escape() + card[r][c] + resetColor.escape());
      System.out.println();
    }
  }
}