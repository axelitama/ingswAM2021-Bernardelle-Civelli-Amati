package it.polimi.ingsw.network.client;

import it.polimi.ingsw.model.TurnManager;
import it.polimi.ingsw.network.messages.Message;

public class LocalConnector implements MessageHandler {

   TurnManager turnManager;

   @Override
   public void handleServerConnection() {}

   @Override
   public void stop() {}

   @Override
   public void sendToServer(Message msg) {
      turnManager.handleAction(msg.getAction());
   }
}