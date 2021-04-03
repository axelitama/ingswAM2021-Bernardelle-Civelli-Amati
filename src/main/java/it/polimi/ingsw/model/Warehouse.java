package it.polimi.ingsw.model;

import it.polimi.ingsw.model.modelexceptions.*;
import it.polimi.ingsw.utility.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Warehouse {

    private final int NUMBER_OF_WAREHOUSE_LEVELS = 3;

    private final Pair<ResourceType, Integer>[] levels;
    private final List<Pair<ResourceType, Integer>> leaderLevels;

    @SuppressWarnings("unchecked")
    public Warehouse() {
        this.levels = new Pair[NUMBER_OF_WAREHOUSE_LEVELS];
        this.leaderLevels = new ArrayList<>();
    }

    public int numberOfWarehouseLevels() {
        return this.NUMBER_OF_WAREHOUSE_LEVELS;
    }

    public int numberOfLeaderCardsLevels() {
        return this.leaderLevels.size();
    }

    public int numberOfAllLevels() {
        return this.NUMBER_OF_WAREHOUSE_LEVELS + this.leaderLevels.size();
    }


    /**
     * Create a new special level in warehouse for a leader card of type storage
     *
     * @param resourceType the resource type of the storage leader card to activate
     * @return number of the level created
     */
    public int addLeaderCardLevel(ResourceType resourceType) {
        Pair<ResourceType, Integer> pair = new Pair<>(resourceType, 0);
        this.leaderLevels.add(pair);
        return this.leaderLevels.lastIndexOf(pair);
    }

    /**
     * Add some resources in a specified level of warehouse
     *
     * @param resource the type of resource to add
     * @param level the level of warehouse in which to add resources
     * @param quantity the number of resources to add
     * @throws IncorrectResourceTypeException this resource type can't be added in this level of warehouse
     * @throws NotEnoughSpaceException there is not enough space on this level to add this amount of resources
     * @throws AbuseOfFaithException resource type is faith, faith can't be added into warehouse
     * @throws LevelNotExistsException this level doesn't exist
     */
    public void addResources(ResourceType resource, int level, int quantity)
            throws IncorrectResourceTypeException, NotEnoughSpaceException,
            AbuseOfFaithException, LevelNotExistsException, NegativeQuantityException {
        if(quantity < 0)
            throw new NegativeQuantityException();
        if(resource == null)
            throw new NullPointerException();
        if(quantity == 0)
            return;
        if(resource == ResourceType.FAITH)
            throw new AbuseOfFaithException("Adding faith to warehouse is not allowed");

        // Normal level
        if(level >= 0 && level < this.numberOfWarehouseLevels()) {
            if(this.levels[level] == null) {
                if(quantity > this.numberOfWarehouseLevels() - level)
                    throw new NotEnoughSpaceException("In level " + level + " of warehouse there is not enough space");
                for(int i = 0; i < this.numberOfWarehouseLevels(); i++)
                    if(i != level && this.levels[i] != null && this.levels[i].getKey() == resource)
                        throw new IncorrectResourceTypeException(
                                "The resource type " + resource + " is already present in another warehouse level");
                this.levels[level] = new Pair<>(resource, quantity);
            } else if(this.levels[level].getKey() == resource) {
                if (this.levels[level].getValue() + quantity > this.numberOfWarehouseLevels() - level)
                    throw new NotEnoughSpaceException("In level " + level + " of warehouse there is not enough space");
                this.levels[level] = new Pair<>(resource, this.levels[level].getValue() + quantity);
            } else
                throw new IncorrectResourceTypeException(
                    "In level " + level + " of warehouse there is already another type of resource");
            return;
        }

        // Leader card level
        if(level >= this.numberOfWarehouseLevels() && level < this.numberOfAllLevels()) {
            int specialLevel = level - this.numberOfWarehouseLevels();
            if(this.leaderLevels.get(specialLevel).getKey() != resource)
                throw new IncorrectResourceTypeException(
                        "The level " + specialLevel
                                + " is a leader card level that can't contain this type of resources");
            if(this.leaderLevels.get(specialLevel).getValue() + quantity > 2)
                throw new NotEnoughSpaceException(
                        "In leader card level " + specialLevel + " there is not enough space");
            this.leaderLevels.set(
                    specialLevel, new Pair<>(resource, this.leaderLevels.get(specialLevel).getValue() + quantity));
            return;
        }

        throw new LevelNotExistsException();
    }

    /**
     * Remove some resources from a specified level of warehouse
     *
     * @param resource the type of resource to remove
     * @param level the level of warehouse from which to remove resources
     * @param quantity the number of resources to remove
     * @throws NotEnoughResourcesException there are not enough resources on this level to remove this amount of them
     * @throws IncorrectResourceTypeException In this level there is another type of resources
     * @throws LevelNotExistsException this level doesn't exist
     */
    public void removeResources(ResourceType resource, int level, int quantity)
            throws NotEnoughResourcesException, IncorrectResourceTypeException,
            LevelNotExistsException, NegativeQuantityException {
        if(quantity < 0)
            throw new NegativeQuantityException();
        if(resource == null)
            throw new NullPointerException();
        if(quantity == 0)
            return;

        // Normal level
        if(level >= 0 && level < this.numberOfWarehouseLevels()) {
            if(this.levels[level] == null)
                throw new NotEnoughResourcesException("The level " + level + " of warehouse is empty");
            else if(this.levels[level].getKey() != resource)
                throw new IncorrectResourceTypeException(
                        "In level " + level + " of warehouse there is another type of resource");
            else if(this.levels[level].getValue() < quantity )
                throw new NotEnoughResourcesException(
                        "In level " + level + " of warehouse there aren't enough resources");
            else if(this.levels[level].getValue() > quantity )
                this.levels[level] = new Pair<>(resource, this.levels[level].getValue() - quantity);
            else if(this.levels[level].getValue() == quantity )
                this.levels[level] = null;
            return;
        }

        // Leader card level
        if(level >= this.numberOfWarehouseLevels() && level < this.numberOfAllLevels()) {
            int specialLevel = level - this.numberOfWarehouseLevels();
            if(this.leaderLevels.get(specialLevel).getKey() != resource)
                throw new IncorrectResourceTypeException(
                        "In the leader card level " + specialLevel + " there is another type of resource");
            if(this.leaderLevels.get(specialLevel).getValue() < quantity)
                throw new NotEnoughResourcesException(
                        "In the leader card level " + specialLevel + " there aren't enough resources");
            this.leaderLevels.set(
                    specialLevel, new Pair<>(resource, this.leaderLevels.get(specialLevel).getValue() - quantity));
            return;
        }

        throw new LevelNotExistsException();
    }

    /**
     * Swap the contained resources of two level
     *
     * @param level1 one level to swap
     * @param level2 the other level to swap
     * @throws NotEnoughSpaceException one of these level too small to contain the number of resources contained in the other
     * @throws LevelNotExistsException one or both of these levels doesn't exist
     * @throws IncorrectResourceTypeException the resource type of a leader card level is incompatible with the resource type of the other level
     */
    public void swapLevels(int level1, int level2) throws NotEnoughSpaceException, LevelNotExistsException,
            IncorrectResourceTypeException {

        // Both normal levels
        if(0 <= level1 && level1 <= this.numberOfWarehouseLevels() - 1
                && 0 <= level2 && level2 <= this.numberOfWarehouseLevels() - 1) {
            if(this.levels[level1] != null
                    && this.levels[level1].getValue() > this.numberOfWarehouseLevels() - level2
                    || this.levels[level2] != null
                    && this.levels[level2].getValue() > this.numberOfWarehouseLevels() - level1)
                throw new NotEnoughSpaceException();
            Pair<ResourceType, Integer> swap = this.levels[level1];
            this.levels[level1] = this.levels[level2];
            this.levels[level2] = swap;
            return;
        }

        // Both leader card levels
        if(this.numberOfWarehouseLevels() <= level1 && level1 < this.numberOfAllLevels()
                && this.numberOfWarehouseLevels() <= level2 && level2 < this.numberOfAllLevels()) {
            int specialLevel1 = level1 - this.numberOfWarehouseLevels(),
                    specialLevel2 = level2 - this.numberOfWarehouseLevels();
            if(this.leaderLevels.get(specialLevel1).getKey() != this.leaderLevels.get(specialLevel2).getKey())
                throw new IncorrectResourceTypeException(
                        "These two leader card levels must contains different types of resources");
            Pair<ResourceType, Integer> swap = this.levels[specialLevel1];
            this.levels[specialLevel1] = this.levels[specialLevel2];
            this.levels[specialLevel2] = swap;
            return;
        }

        // One normal level and one leader card level

        if(0 <= level1 && level1 <= this.numberOfWarehouseLevels() - 1
                && this.numberOfWarehouseLevels() <= level2 && level2 < this.numberOfAllLevels()
                || this.numberOfWarehouseLevels() <= level1 && level1 < this.numberOfAllLevels()
                && 0 <= level2 && level2 <= this.numberOfWarehouseLevels() - 1) {
            int normalLevel,
                    specialLevel;
            if(0 <= level1 && level1 <= this.numberOfWarehouseLevels() - 1) {
                normalLevel = level1;
                specialLevel = level2;
            } else {
                normalLevel = level2;
                specialLevel = level1;
            }
            if(this.levels[normalLevel].getKey() != this.leaderLevels.get(specialLevel).getKey())
                throw new IncorrectResourceTypeException(
                        "The resource type contained in the normal level is incompatible with this leader card level");
            if(this.levels[normalLevel].getValue() > 2)
                throw new NotEnoughSpaceException();
            if(this.leaderLevels.get(specialLevel).getValue() > this.numberOfWarehouseLevels() - normalLevel)
                throw new NotEnoughSpaceException();
            Pair<ResourceType, Integer> swap = this.levels[specialLevel];
            this.levels[specialLevel] = this.levels[normalLevel];
            this.levels[normalLevel] = swap;
        }

        throw new LevelNotExistsException();
    }

    /**
     * Move some resources from one level to another
     *
     * @param sourceLevel the level from which to take resources
     * @param destinationLevel the level in which to put resources
     * @param quantity the number of resources to move
     */
    public void moveResource(int sourceLevel, int destinationLevel, int quantity) {}

    /**
     * Returns the total amount of the specified resources contained in the warehouse
     *
     * @param resource the resource type
     * @return the number of contained resources of this type
     */
    public int getNumberOf(ResourceType resource) {
        return Arrays.stream(this.levels).filter(Objects::nonNull).filter( (i) -> i.getKey() == resource )
                    .map(Pair::getValue).reduce(Integer::sum).orElse(0)
                +
                this.leaderLevels.stream().filter(Objects::nonNull).filter( (i) -> i.getKey() == resource )
                    .map(Pair::getValue).reduce(Integer::sum).orElse(0);
    }

    /**
     *
     * @return total number of resources contained in the warehouse
     */
    public int totalResources() {
        return Arrays.stream(this.levels).filter(Objects::nonNull).map(Pair::getValue)
                    .reduce(Integer::sum).orElse(0)
                +
                this.leaderLevels.stream().filter(Objects::nonNull).map(Pair::getValue)
                    .reduce(Integer::sum).orElse(0);
    }
}