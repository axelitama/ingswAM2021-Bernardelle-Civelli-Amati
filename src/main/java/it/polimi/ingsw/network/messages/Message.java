package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.action.Action;
import it.polimi.ingsw.utility.GSON;

/**
 * Represents a message between client and server in network
 */
public class Message {
   //TODO aggiugere receiver e setReceiver
   //provare a vedere se deserializzando LoginMessage posso impostare questi attributi private
   private String username = null; //username del Client mittente,
   private MessageType messageType;
   private String payload = null; //usiamo il payload in questo modo:
   //-da Server a Client per 1)notificare gli phaseUpdate -> Oggetti serializzati 2)messaggi di servizio;
   //-da Client a Server per contenere le Action in formato Json


   public Message(MessageType messageType) {
      this.messageType = messageType;
   }

   public Message(String username, MessageType messageType) {
      this.username = username;
      this.messageType = messageType;
   }

   public Message(MessageType messageType, String payload) {
      this.messageType = messageType;
      this.payload = payload;
   }

   public Message(MessageType messageType, Object object) {
      this.messageType = messageType;
      this.payload = GSON.getGsonBuilder().toJson(object);
   }

   public Message(String username, MessageType messageType, String payload) {
      this.username = username;
      this.messageType = messageType;
      this.payload = payload;
   }

   public Message(String username, MessageType messageType, Object object) {
      this.messageType = messageType;
      this.username = username;
      this.payload = GSON.getGsonBuilder().toJson(object);
   }

   public MessageType getMessageType() {
      return messageType;
   }

   /**
    * returns message payload as a String
    * @return the message payload String
    */
   public String getPayload() { return payload; }

   /**
    * returns message payload as a java object of the specified type
    * @param myType the type of the returned object
    * @param <T>
    * @return
    */
   public <T> T getPayloadByType(Class<T> myType){
      return GSON.getGsonBuilder().fromJson(payload, myType);
   }


   public Action getAction() { return GSON.buildAction(payload); } //se registriamo i sottotipi di Action sul builder probabilmente deserializza lui correttamente sulla sottoclasse

   public void setUsername(String usr) {
      this.username = usr;
   }

   public String getUsername() {
      return username;
   }
}