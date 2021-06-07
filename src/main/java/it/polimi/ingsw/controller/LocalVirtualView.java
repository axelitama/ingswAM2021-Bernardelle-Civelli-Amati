package it.polimi.ingsw.controller;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.track.Track;
import it.polimi.ingsw.network.client.Client;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.utility.GSON;
import it.polimi.ingsw.utility.Pair;
import it.polimi.ingsw.view.ClientModelUpdaterInterface;
import it.polimi.ingsw.view.LorenzoViewInterface;

import java.lang.reflect.Type;


/**
 * to instanciate instead of NetworkViretualView
 * calls method directly on ClientModelUpdaterInterface
 */
public class LocalVirtualView implements ModelObserver {
   private ClientModelUpdaterInterface state;
   private String username;
   private LorenzoViewInterface view;

   public LocalVirtualView(ClientModelUpdaterInterface state, String username, LorenzoViewInterface view) {
      this.state = state;
      this.username = username;
      this.view = view;
   }


   @Override
   public void chestUpdate(String stateUpdate) {
      state.chestUpdate(username, stateUpdate);
   }

   @Override
   public void warehouseUpdate(Warehouse.WarehouseUpdate stateUpdate) {
      state.warehouseUpdate(username, stateUpdate);
   }

   @Override
   public void leaderUpdate(String stateUpdate) {
      state.leaderUpdate(username, stateUpdate);
   }

   @Override
   public void leaderSetupUpdate(String username, String stateUpdate) {
      state.leaderSetup(username, stateUpdate);
   }

   @Override
   public void marketUpdate(String stateUpdate) {
      state.marketUpdate(stateUpdate);
   }

   @Override
   public void marketSetupUpdate(String stateUpdate) {
      state.marketSetup(stateUpdate);
   }

   @Override
   public void cardSlotUpdate(String stateUpdate) {
      state.cardSlotUpdate(username, stateUpdate);
   }

   @Override
   public void trackUpdate(String username, String stateUpdate) {
      state.trackUpdate(username, stateUpdate);
   }

   @Override
   public void vaticanUpdate(String username, String stateUpdate) {
      state.vaticanUpdate(username, stateUpdate);
   }

   @Override
   public void devDeckUpdate(String stateUpdate) {
      state.devDeckUpdate(stateUpdate);
   }

   @Override
   public void devDeckSetup(String stateUpdate) {
      state.devDeckSetup(stateUpdate);
   }

   @Override
   public void tempChestUpdate(String stateUpdate) {
      state.tempChestUpdate(username, stateUpdate);
   }

   @Override
   public void discardedLeaderUpdate(String stateUpdate) {
      state.discardedLeaderUpdate(username, stateUpdate);
   }

   @Override
   public void endGameUpdate(String stateUpdate) {
      view.displayGameEnded(stateUpdate);
   }

   @Override
   public void lorenzoTrackUpdate(String stateUpdate) {
      state.lorenzoTrackUpdate(stateUpdate);
      view.displayLorenzoMoved();
   }

   @Override
   public void lorenzoShuffleUpdate() {
      state.lorenzoShuffleUpdate();
      view.displayLorenzoShuffled();
   }

   @Override
   public void lorenzoDevDeckUpdate(String stateUpdate) {
      state.devDeckUpdate(stateUpdate);
      view.displayLorenzoDiscarded(stateUpdate);
   }

   @Override
   public void chestMergeUpdate() {
      state.chestMergeUpdate(username);
   }
}
