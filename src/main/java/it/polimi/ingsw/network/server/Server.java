package it.polimi.ingsw.network.server;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.TurnManager;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.model.singleplayer.SinglePlayer;
import it.polimi.ingsw.network.messages.ErrorType;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.utility.ConfigParameters;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class accepts connection to a client and assign the client handling
 * to the ServerClientHandler class via Thread.
 */
public class Server {

   public static final int MIN_PORT_NUMBER = 1024;
   public static final int MAX_PORT_NUMBER = 65535;
   private static final int SINGLE_PLAYER_NUMBER = 1;
   private static final int MAX_MULTIPLAYER_NUMBER = 4;
   private final Map<String, ServerClientHandler> usernameToClientHandler = new HashMap<>(); //l'opposto non serve perchè ho la get username sul clientHandler
   private final List<ServerClientHandler> clients = new ArrayList<>(); //contiene tutti i client che si sono connessi al server, se uno lascia prima che la paritta inizia lo tolgo
   //private final List<String> connectedAndDisconnectedClients = new ArrayList<>();// utente aggiunto a questa lista appena viene creato il socket, non ha ancora un username assegnato
   // contains all the clients, no matter if they are connected
   private boolean gameRunning = false;
   private int playersNumber = 0;
   private TurnManager turnManager;
   // loggedplayers() e clients sono diverse solo se qualcuno levva durante la partita


   public static void main(String[] args) {
      int serverPortNumber;

      if (args.length == 1) {
         serverPortNumber = Integer.parseInt(args[0]);
         System.out.println("Server started on port: " + serverPortNumber);
      } else if(ConfigParameters.TESTING) {
         serverPortNumber = 6754;
         System.out.println("starting server in TESTING configuration on port " + serverPortNumber);
      }
      else{
         Scanner in = new Scanner(System.in);
         System.out.print("Set server port number: ");
         serverPortNumber = integerInputValidation(in, MIN_PORT_NUMBER, MAX_PORT_NUMBER);
      }

      ServerSocket socket;
      try {
         socket = new ServerSocket(serverPortNumber);
         System.out.println("Waiting for players to connect...");
      } catch (IOException e) {
         System.out.println("ERROR: Impossible to open server socket");
         System.exit(1);
         return;
      }

      Server server = new Server();
      while (true) {
         try {
            /* accepts connections; for every connection accepted,
             * create a new Thread executing a ClientHandler */
            Socket clientSocket = socket.accept();
            //clientSocket.setSoTimeout(ConfigParameters.SERVER_TIMEOUT); // NEEDED TO REALIZE THAT A CLIENT HAS DISCONNECTED UNCLEANLY
            ServerClientHandler clientHandler = new ServerClientHandler(clientSocket, server);
            new Thread(clientHandler).start();
         } catch (IOException e) {
            System.out.println("ERROR: Connection dropped");
         }
      }
   }

   //manages other players connection
   public synchronized void lobbySetup(Message message){
      String username = message.getUsername();

      if(playersNumber == 0){ //executed only for the first player to connect
         if(message.getPayload() == null)
            return; // pensare se mandare messaggio di errore
         int tempPlayerNum = Integer.parseInt(message.getPayload());
         if (tempPlayerNum < SINGLE_PLAYER_NUMBER || tempPlayerNum > MAX_MULTIPLAYER_NUMBER) {
            sendToClient(new Message(username, MessageType.ERROR, ErrorType.INVALID_NUMBER_OF_PLAYERS));
            return;
         }
         playersNumber = tempPlayerNum;
         sendToClient(new Message(username, MessageType.LOBBY_CREATED));
      }
      else
      {
         List<String> tmpConnectdPlyrs = loggedPlayers();
         tmpConnectdPlyrs.remove(username); // to tell everybody except the player that connected
         for(String user: tmpConnectdPlyrs)
            sendToClient(new Message(user, MessageType.OTHER_USER_JOINED, Integer.toString(NumberOfRemainingLobbySlots())));

         sendToClient(new Message(username, MessageType.YOU_JOINED, Integer.toString(NumberOfRemainingLobbySlots()))); //sent to the player that joined

         if (loggedPlayers().size() == playersNumber) {
            start();
         }
      }
   }

   public synchronized void handleLogin(Message message, ServerClientHandler clientHandler){
      String username = message.getUsername();
      boolean isReconnecting = isTaken(username) && gameRunning &&
              !usernameToClientHandler.get(message.getUsername()).isConnected();

      if(isReconnecting) {
         handleClientReconnection(message, clientHandler);
         return;
      }

      if(gameRunning){
         clientHandler.sendMessage(new Message(MessageType.ERROR, ErrorType.GAME_ALREADY_STARTED));
         handleClientDisconnection(clientHandler);
         return;
      }

      if(username == null || username.equals("")) {
         clientHandler.sendMessage(new Message(MessageType.ERROR, ErrorType.INVALID_LOGIN_USERNAME));
         return;
      }

      if (isTaken(username))
         clientHandler.sendMessage(new Message(MessageType.ERROR, ErrorType.INVALID_LOGIN_USERNAME));
      else{
         if(isFirst()) {
            succesfulLogin(username, clientHandler);
            clientHandler.sendMessage(new Message(MessageType.NUMBER_OF_PLAYERS));
         } else if(playersNumber == 0) {        //il player non è il primo a loggarsi ma il numero di giocatori è ancora 0 -> significa che qualcuno sta creando la lobby
            clientHandler.sendMessage(new Message(MessageType.WAIT_FOR_LOBBY_CREATION, "A player is creating the lobby, try again in a few seconds"));
         } else {
            succesfulLogin(username, clientHandler);
            lobbySetup(message);
         }
      }

   }

   private void succesfulLogin(String username, ServerClientHandler clientHandler) {
      clientHandler.setUsername(username);
      clientHandler.setLogged(true);
      usernameToClientHandler.put(username, clientHandler);
      clientHandler.sendMessage(new Message(username, MessageType.LOGIN_SUCCESSFUL));
   }

   private void handleClientReconnection(Message message,ServerClientHandler newClientHandler) {
      ServerClientHandler oldClientHandler = usernameToClientHandler.get(message.getUsername());
      clients.remove(oldClientHandler);
      usernameToClientHandler.remove(oldClientHandler.getUsername());
      addClient(newClientHandler);
      usernameToClientHandler.put(newClientHandler.getUsername(), newClientHandler);
      newClientHandler.sendMessage(new Message(MessageType.RECONNECTED));
      // TODO mandargli lo stato
   }

   private int NumberOfRemainingLobbySlots() {
      return playersNumber - loggedPlayers().size();
   }

   private void start() {
      Game game = null;
      List<String> playersInOrder = null;

      Controller controller = new Controller(this);
      // problema: al game server turnmanager per fare gli update e a TurnManager serve game
      // altro problema: mando gli update del modello prima del messaggio startGame
      try {
         if(playersNumber == 1)
            game = new SinglePlayer(controller);
         if(playersNumber > 1)
            game = new Game(controller);
      }catch(IOException | JsonSyntaxException e){ //TODO controllare se viene lanciata la JsonSyntaxException
         //TODO bisogna chiudere la partita (disconnetto tutti i client 1 per volta dicendo Errore nei file di configurazione del gioco)
         sendToClient(new Message(MessageType.GENERIC_MESSAGE));
      }

      try {
         this.turnManager = new TurnManager(game, loggedPlayers());
         playersInOrder = turnManager.startGame();
      }catch (IOException e) {
         System.out.println("playerboard constructor probably has a problem");
         e.printStackTrace();
      }
      sendToClient(new Message(MessageType.GAME_STARTED, playersInOrder));
      gameRunning = true;
   }


   public void serverSingleUpdate(Message message){
      message.setUsername(turnManager.getCurrentPlayer());
      sendToClient(message);
   }


   public void serverBroadcastUpdate(Message message){
      message.setUsername(turnManager.getCurrentPlayer());
      sendBroadcast(message);
   }

   /**
    * Adds a client to the connectedClients list
    *
    * @param client the {@link ServerClientHandler} to be added
    */
   public synchronized void addClient(ServerClientHandler client){
      clients.add(client);
   }

   public boolean isTaken(String username){
      for(ServerClientHandler s : clients)
         if(username.equals(s.getUsername()))
            return true;

      return false;
   }

   /**
    * sends the message to client set as username in the message. if username is null send broadcast
    * @param message message to send
    */
   public void sendToClient(Message message) { //quando entro qua i messaggi a cui serve l'username lo hanno, gli altri no

      String username = message.getUsername();
      if(username == null){              //messaggi senza username vengono inviati a tutti
         for(ServerClientHandler s : clients)
            s.sendMessage(message);
         return;
      }
      else
         usernameToClientHandler.get(username).sendMessage(message);

   }

   /**
    * always send broadcast message
    * @param message message to send
    */
   public void sendBroadcast(Message message) {
      for(ServerClientHandler s : clients)
         s.sendMessage(message);
   }

   private static int integerInputValidation(Scanner in, int minPortNumber, int maxPortNumber) {
      boolean error = false;
      int input = 0;
      try {
         input = Integer.parseInt(in.nextLine());
      }catch(NumberFormatException e){
         error = true;
      }

      while(error || input < minPortNumber || input > maxPortNumber){
         error = false;
         System.out.print("input must be between " + minPortNumber + " and " + maxPortNumber + ". try again: ");
         try {
            input = Integer.parseInt(in.nextLine());
         }catch(NumberFormatException e){
            error = true;
         }
      }
      return input;
   }

   public TurnManager getTurnManager() {
      return turnManager;
   }

   private synchronized boolean isFirst() {
      return loggedPlayers().isEmpty();
   }

   public void deleteClient(ServerClientHandler client) {
      clients.remove(client); //se si disconentte prima di essersi loggato lo elimino e me lo dimentico
      usernameToClientHandler.remove(client.getUsername());
   }

   public synchronized List<String> loggedPlayers(){ //if a player disconnects he can be still a logged player
      return clients.stream()
              .filter(ServerClientHandler::isLogged)
              .map(ServerClientHandler::getUsername)
              .collect(Collectors.toList());
   }

   public synchronized List<ServerClientHandler> connectedPlayers(){
      return clients.stream()
              .filter(ServerClientHandler::isConnected)
              .collect(Collectors.toList());
   }

   public boolean isGameRunning() {
      return gameRunning;
   }

   protected void handleClientDisconnection(ServerClientHandler disconnectedClient) { // TODO metterlo nel server
      if(!gameRunning) //if the game isn't started delete the player from the list and forget about him
         deleteClient(disconnectedClient);

      if(disconnectedClient.isLogged())
         sendBroadcast(new Message(disconnectedClient.getUsername(), MessageType.DISCONNECTED));
      //TODO devo dire al controller che il player si è disconnesso per fargli saltare il turno (cioè basta che quando tocca lui il controller dice subito agli altri che lui passa il turno)

      disconnectedClient.closeSocket();

      disconnectedClient.setConnected(false); //needed to handle reconnection
   }
}
