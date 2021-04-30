package it.polimi.ingsw.network.action;

public enum ActionType {

   CHOSE_RESOURCES,
   PRODUCE,
   BASE_PRODUCE,
   LEADER_PRODUCE,
   SHOP_MARKET,
   BUY_CARD,
   DISCARD_LEADER,
   END_TURN,
   ACTIVATE_LEADER,
   INSERT_MARBLE,
   CHOOSE_WHITE_LEADER;

   /**
    * static factory method that constructs enum by string
    *
    * @param value string to create the enum
    * @return a new enumeration
    */
   public static ActionType fromValue(String value) {
      for (ActionType actionType : values()) {
         if (actionType.name().equals(value)) {
            return actionType;
         }
      }
      throw new IllegalArgumentException("invalid string value passed: " + value);
   }

}