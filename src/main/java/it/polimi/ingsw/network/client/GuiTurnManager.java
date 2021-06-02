package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.PhaseType;
import it.polimi.ingsw.model.TurnManager;
import it.polimi.ingsw.view.ClientStateViewer;
import it.polimi.ingsw.view.ViewInterface;

public class GuiTurnManager implements ClientTurnManagerInterface{

   private Client client;
   private PhaseType currentPhase;
   private String currentPlayer;
   private ViewInterface view;

   public GuiTurnManager(Client client, ViewInterface view) {
      this.currentPhase = PhaseType.SETUP_CHOOSING_RESOURCES;
      this.client = client;
      this.view = view;
   }

   @Override
   public void currentPhasePrint() {

   }

   @Override
   public String getCurrentPlayer() {
      return this.currentPlayer;
   }

   @Override
   public void setCurrentPlayer(String currentPlayer) {
      this.currentPlayer = currentPlayer;
   }

   @Override
   public boolean setStateIsPlayerChanged(TurnManager.TurnState newState) {
      this.currentPhase = newState.getPhase(); //set new phase

      if(!currentPlayer.equals(newState.getPlayer())) {
         this.currentPlayer = newState.getPlayer();
         return true;
      }
      return false;
   }
}

