package it.polimi.ingsw.view;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.ResourceType;
import it.polimi.ingsw.model.Warehouse;
import it.polimi.ingsw.model.track.Track;
import it.polimi.ingsw.utility.GSON;
import it.polimi.ingsw.utility.Pair;

import java.lang.reflect.Type;
import java.util.*;

public class SimplePlayerState {

   private final int NUMBER_OF_NORMAL_LEVELS = 3;
   private final int MAX_SPECIAL_LEVELS = 2;

   private int trackPosition;
   private boolean[] vaticanFlipped;
   private  List<Integer> notActiveLederCards; //identified by ID
   private  List<Integer> activeLeaderCards;   //identified by ID
   private final Map<ResourceType, Integer> chest;
   private final Map<ResourceType, Integer> tempChest;
   private final Pair<ResourceType, Integer>[] storageLevels;
   private final List<Integer>[] cardSlots;


   public SimplePlayerState() {
      this.trackPosition = 0;
      this.chest = new HashMap<>();
      for (ResourceType resType : ResourceType.values())
         chest.put(resType, 0);

      this.tempChest = new HashMap<>();
      this.storageLevels = new Pair[5];
      for(int i=0; i<5; i++)
         storageLevels[i] = new Pair(null, null);

      this.cardSlots = new List[3];
      for(int i=0; i<3; i++)
         cardSlots[i] = new ArrayList<>();

      this.vaticanFlipped = new boolean[3];
      for(int i=0; i<3; i++)
         vaticanFlipped[i] = false;

      this.activeLeaderCards = new ArrayList<>();

      this.notActiveLederCards = new ArrayList<>();
      for(int i=0; i<2; i++)
         notActiveLederCards.add(0);
      //adds 2 placeholder card with a special ID that doesn't match any real card
   }

   //-----------SETUP-------------------------------------------
   public void setupLeaderCard(String payload){
      Type token = new TypeToken<List<Integer>>(){}.getType();
      this.notActiveLederCards = GSON.getGsonBuilder().fromJson(payload, token);

   }
   //-----------------------------------------------------------


   //----------UPDATE----------
   public void warehouseUpdate(String payload) {
      Warehouse.WarehouseUpdate update = GSON.getGsonBuilder().fromJson(payload, Warehouse.WarehouseUpdate.class);

      ResourceType resource = update.getResourceType();

      //normalLevels
      if(update.getLevel()<3) {
         //controllo se la risorsa é presente
         for (int i = 0; i < storageLevels.length; i++) {

            if (storageLevels[i].getKey() != null) {
               if (storageLevels[i].getKey().equals(resource)) {
                  if (i == update.getLevel()) {
                     storageLevels[i] = new Pair<>(resource, update.getQuantity());
                     return;
                  } else {
                     Pair<ResourceType, Integer> temp = new Pair<>(storageLevels[update.getLevel()].getKey(), storageLevels[update.getLevel()].getValue());
                     storageLevels[update.getLevel()] = new Pair<>(resource, update.getQuantity());
                     storageLevels[i] = new Pair<>(temp.getKey(), temp.getValue());
                     return;
                  }
               }
            }
         }
      }
         //se non presente creo
         storageLevels[update.getLevel()] = new Pair<>(resource, update.getQuantity());

   }

   public void trackUpdate(String payload) {
      int newPosition = GSON.getGsonBuilder().fromJson(payload, Integer.class);
      this.trackPosition = newPosition;
      //System.out.println(newPosition);
   }

   public void vaticanReportUpdate(String payload) {
      Track.VaticanReport update = GSON.getGsonBuilder().fromJson(payload, Track.VaticanReport.class);
      int zone = update.getZone();
      boolean flip = update.isActive();

      vaticanFlipped[zone] = flip;
   }

   public void chestUpdate(String payload) { //serve?
      Type token = new TypeToken<Pair<ResourceType, Integer>>(){}.getType();
      Pair<ResourceType, Integer> pair = GSON.getGsonBuilder().fromJson(payload, token);
      ResourceType resource = pair.getKey();
      int quantity = pair.getValue();

      this.chest.put(resource, quantity);
   }

   public void tempChestUpdate(String payload) {
      Type token = new TypeToken<Pair<ResourceType, Integer>>(){}.getType();
      Pair<ResourceType, Integer> pair = GSON.getGsonBuilder().fromJson(payload, token);
      ResourceType resource = pair.getKey();
      int quantity = pair.getValue();

      this.tempChest.put(resource, quantity);
   }

   public void cardSlotUpdate(String payload) {
      Type token = new TypeToken<Pair<Integer, Integer>>(){}.getType();
      Pair<Integer, Integer> pair = GSON.getGsonBuilder().fromJson(payload, token);
      int devCardID = pair.getKey();
      int slot = pair.getValue();

      cardSlots[slot].add(devCardID);
   }

   public void activatedLeaderUpdate(String payload) {
      this.activeLeaderCards.add(Integer.parseInt(payload));
      this.notActiveLederCards.remove(Integer.valueOf(payload)); //potrebbe non andare a causa dell'indice
   }


   /**
    * delete a leadercard from the SimplePlayerState
    *
    * @param indexOfLeaderToDiscard
    */
   public void discardLeader(int indexOfLeaderToDiscard) {
         notActiveLederCards.remove(indexOfLeaderToDiscard);
   }
   //----------------------------------------------------------


   //----------GETTERS-----------------------------------------

   /**
    * return non-active leader cards present in this simplemodel
    * NOTE: can return null
    * @return non-active leader cards present in this simplemodel
    */
   public List<Integer> getNotActiveLeaderCards() {
      return new ArrayList<>(notActiveLederCards);
   }

   public Map<ResourceType, Integer> getChest() {
      return new HashMap<>(chest);
   }

   public Pair<ResourceType, Integer>[] getWarehouseLevels() {
      Pair<ResourceType, Integer>[] warehouseLevels = new Pair[3];
      for(int i=0; i<3; i++)
         warehouseLevels[i] = new Pair<>(storageLevels[i]);
      return warehouseLevels;
   }

   public List<Pair<ResourceType, Integer>> getLeaderLevels() {

      List<Pair<ResourceType, Integer>> tempList = new ArrayList<>();
      for(int i=3; i<5; i++)
         if(storageLevels[i].getKey() != null)
            tempList.add(storageLevels[i]);
      return tempList;
   }

   public int getTrackPosition() {
      return trackPosition;
   }

   public List<Integer>[] getCardSlots() {
      List<Integer>[] tempCardSlots = new List[3];
      for(int i=0; i<3; i++)
         tempCardSlots[i] = new ArrayList<>(cardSlots[i]);
      return tempCardSlots;
   }

   public boolean[] getVaticanFlipped() {
      return vaticanFlipped;
   }

   /**
    * return active leader cards present in this simplemodel
    *
    * @return active leader cards present in this simplemodel
    */
   public List<Integer> getActiveLeaders(){
      return new ArrayList<>(this.activeLeaderCards);
   }

   //----------------------------------------------------------


   //----------UTILITY-----------------------------------------
   public void mergeTempChest() {
      for(Map.Entry<ResourceType, Integer> entry : tempChest.entrySet()){
         chest.put(entry.getKey(), chest.containsKey(entry.getKey()) ? chest.get(entry.getKey()) + entry.getValue() : entry.getValue());
      }
      this.tempChest.clear();
   }

//   public boolean isBaseProductionActivatable() { //secondo me non serve
//      boolean isvalid = false;
//      int quantity = 0;
//      for(Map.Entry<ResourceType, Integer> entry : chest.entrySet())
//         quantity = entry.getValue();
//      for(Pair<ResourceType, Integer> p : warehouseLevels) {
//         if(p.getValue()!=null)
//            quantity = p.getValue();
//      }
//      for(Pair<ResourceType, Integer> p : leaderLevels) {
//         if(p.getValue()!=null)
//            quantity = p.getValue();
//      }
//      if(quantity>1)
//         isvalid = true;
//      return isvalid;
//   }

//   public Map<ResourceType, Integer> throwableResources() {
//      Map<ResourceType, Integer> resources = new HashMap<>();
//      for(Map.Entry<ResourceType, Integer> entry : chest.entrySet())
//         resources.put(entry.getKey(), entry.getValue());
//      for(Pair<ResourceType, Integer> p : warehouseLevels) {
//         if(p.getValue()!=null)
//            resources.put(p.getKey(), resources.containsKey(p.getKey()) ? resources.get(p.getKey()) + p.getValue() : p.getValue());
//      }
//      for(Pair<ResourceType, Integer> p : leaderLevels) {
//         if(p.getValue()!=null)
//            resources.put(p.getKey(), resources.containsKey(p.getKey()) ? resources.get(p.getKey()) + p.getValue() : p.getValue());
//      }
//
//      return resources;
//   }
   //----------------------------------------------------------
}
