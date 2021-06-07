package it.polimi.ingsw.model;
import it.polimi.ingsw.model.modelObservables.ChestMergeObservable;
import it.polimi.ingsw.model.modelObservables.ChestUpdateObservable;
import it.polimi.ingsw.model.modelObservables.TempChestObservable;
import it.polimi.ingsw.model.modelexceptions.AbuseOfFaithException;
import it.polimi.ingsw.model.modelexceptions.NegativeQuantityException;
import it.polimi.ingsw.model.modelexceptions.NotEnoughResourcesException;

import java.util.HashMap;
import java.util.Map;

public class Chest implements ChestUpdateObservable, TempChestObservable, ChestMergeObservable {
  private final Map<ResourceType, Integer> resources;
  private final Map<ResourceType, Integer> tempResourcesMap;

  private transient ModelObserver controller = null;

  public Chest(){
    resources = new HashMap<>();
    tempResourcesMap = new HashMap<>();
  }

  public static class ChestUpdate{
    private final ResourceType resourceType;
    private final int quantity;

    public ChestUpdate(ResourceType resourceType, int quantity) {
      this.resourceType = resourceType;
      this.quantity = quantity;
    }

    public ResourceType getResourceType() {
      return resourceType;
    }

    public int getQuantity() {
      return quantity;
    }


  }

  /**
   *  add resources to the chest
   *  NOTE: the newly added resources will be available to remove only after calling {@link #endOfTurnMapsMerge()}
   *
   * @param resource the resource to be added
   * @param quantity how many resource to add
   * @throws AbuseOfFaithException when trying to add ResourceType.FAITH
   */
  public void addResources(ResourceType resource, int quantity) throws AbuseOfFaithException, NegativeQuantityException {
    if(resource == ResourceType.FAITH)
      throw new AbuseOfFaithException("Adding faith to chest is not allowed");
      //if the key is not present adds a new element to the map with value quantity
    if(quantity < 0)
      throw new NegativeQuantityException("you are adding a negative quantity of a resource, that's not allowed");

    tempResourcesMap.compute(resource, (k,v) -> (v==null) ? quantity : v + quantity);
    notifyTempChestChange(new ChestUpdate(resource, tempResourcesMap.get(resource)));
  }

  /**
   * removes resources from the chest
   *
   * @param resource the resource to be removed
   * @param quantity how many resource to remove
   * @throws NotEnoughResourcesException if you try to remove more resources than you have
   */
  public void removeResources(ResourceType resource, int quantity) throws NotEnoughResourcesException, NegativeQuantityException {
    if(quantity < 0)
      throw new NegativeQuantityException();

    if(quantity == 0 || resource == ResourceType.FAITH)
      return;

    if(resources.containsKey(resource)) {
      if (resources.get(resource) - quantity < 0)
        throw new NotEnoughResourcesException("you are trying to remove more resources than you have");

      if(resources.get(resource) == quantity) {
        resources.remove(resource);
        notifyChestUpdate(new ChestUpdate(resource, 0));
      }
      else {
        resources.replace(resource, resources.get(resource) - quantity);
        notifyChestUpdate(new ChestUpdate(resource, resources.get(resource)));
      }
    }
    else
      throw new NotEnoughResourcesException("you have 0 of the specified resource");
  }

  /**
   * the number of total resources contained (no distinction by type)
   *
   * @return the number of total resources as an int
   */
  public int totalNumberOfResources(){
    return resources.values().stream().reduce(0, Integer::sum);
  }

  /**
   * get the number of resources of the specified type contained in the chest
   * NOTE: to make this method include newly added resources you need to call {@link #endOfTurnMapsMerge()}
   *
   * @param resource the Resource type of which you want to know how many there are in the chest
   * @return the number of resources of the specified type
   */
  public int getNumberOf(ResourceType resource){
    if(resource == null || !resources.containsKey(resource))
      return 0;
    return resources.get(resource);
  }

  /**
   * makes newly added resources available for removal
   */
  public void endOfTurnMapsMerge(){
    for(Map.Entry<ResourceType, Integer> entry : tempResourcesMap.entrySet()) {
        resources.put(entry.getKey(), resources.containsKey(entry.getKey()) ? resources.get(entry.getKey()) + entry.getValue() : entry.getValue());
    }
    notifyChestMerge();
    this.tempResourcesMap.clear();
  }

  public void setController(ModelObserver controller) {
    this.controller = controller;
  }

  @Override
  public void notifyChestUpdate(Chest.ChestUpdate msg) {
    if (controller != null)
      controller.chestUpdate(msg);
  }

  @Override
  public void notifyTempChestChange(Chest.ChestUpdate msg) {
    if (controller != null)
      controller.tempChestUpdate(msg);
  }

  @Override
  public void notifyChestMerge() {
    if (controller != null)
      controller.chestMergeUpdate();
  }
}