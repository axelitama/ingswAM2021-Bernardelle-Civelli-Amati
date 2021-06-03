package it.polimi.ingsw.model.singleplayer;

import it.polimi.ingsw.model.DevelopCardColor;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.modelexceptions.InvalidUsernameException;
import it.polimi.ingsw.model.modelexceptions.MaximumNumberOfPlayersException;
import it.polimi.ingsw.model.track.LorenzoTrack;
import it.polimi.ingsw.utility.ConfigParameters;
import it.polimi.ingsw.utility.GSON;
import it.polimi.ingsw.utility.Pair;
import it.polimi.ingsw.view.LorenzoViewInterface;

import java.io.IOException;
import java.util.*;

public class SinglePlayer extends Game {

   private final LinkedList<ActionToken> actionTokenStack; // don't change with List, it's required a LinkedList.
   private final LorenzoTrack lorenzoTrack;
   private boolean lorenzoWon = false;

   /**
    * Create a new single player game
    *
    * @param virtualView the observer of the model
    * @throws IOException if there are some problems loading the configuration files
    */
   public SinglePlayer(ModelObserver virtualView) throws IOException {
      super(virtualView);
      this.lorenzoTrack = GSON.lorenzoTrackParser();

      this.actionTokenStack = new LinkedList<>(Arrays.asList(
              new DiscardToken(DevelopCardColor.BLUE), new DiscardToken(DevelopCardColor.GREEN),
              new DiscardToken(DevelopCardColor.YELLOW), new DiscardToken(DevelopCardColor.PURPLE),
              new ShuffleToken(super.controller), new StepForwardToken(), new StepForwardToken()
      ));

      Collections.shuffle(this.actionTokenStack);
   }

   /**
    * Adds the player in this game
    *
    * @param username username of the new player
    * @throws IOException if there are some problems loading the configuration files
    * @throws MaximumNumberOfPlayersException there are already a player in this game
    */
   @Override
   public void addPlayer(String username) throws IOException, MaximumNumberOfPlayersException {
      if(super.getOrderedPlayers().size() == 0)
         super.addPlayer(username);
      else
         throw new MaximumNumberOfPlayersException(1);
   }

   /**
    * Perform Lorenzo's action and return the same player
    *
    * @param currentPlayer username of the current player
    * @return username of next connected player (the same player)
    * @throws InvalidUsernameException if the specified player doesn't exist
    */
   @Override
   public String nextConnectedPlayer(String currentPlayer) throws InvalidUsernameException {
      // TODO test, non sono sicuro che il metodo SUPER funzioni correttamente con un solo player
      // This statement is first due to throws the exception if the username is wrong

      String player = super.nextConnectedPlayer(currentPlayer);
      if(endGame){
         handleEndGame(currentPlayer);
         return null;
      }
      ActionToken token = this.actionTokenStack.remove();
      token.useToken(this.actionTokenStack, this.lorenzoTrack, super.developCardDeck);
      this.actionTokenStack.addLast(token);
      if(endGame){
         handleEndGame("");
         return null;
      }
      return player;
   }

   private void handleEndGame(String winner) {
      //if()
   }
}

