package it.polimi.ingsw.view;


import it.polimi.ingsw.controller.action.Action;
import it.polimi.ingsw.network.messages.Message;

import java.util.List;

public interface ViewInterface {
  public void displaySetup();
  public void displayLogin();
  public void displaySetupFailure();
  public void displayDisconnected();
  public void displayFailedLogin();
  public void displayLoginSuccessful();
  public void displayLobbyCreated();
  public void displayOtherUserJoined(Message msg);
  public void displayYouJoined();
  public void displayPlayersNumberChoice();
  public void displayWaiting();
  public void displayServerDown();
  public void displayGameAlreadyStarted();
  public void displayReconnection();
  public void displayGameStarted();
  public void displayRecievedLeadercards();
  public void displayMarketSetup();
}
