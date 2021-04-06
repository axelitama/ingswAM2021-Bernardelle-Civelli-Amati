package it.polimi.ingsw.model.market;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.modelexceptions.*;

public class WhiteMarble extends MarketMarble{

    public WhiteMarble(){}

    /**
     * Adds the resources owed to the player due to this marble
     *
     * @param playerBoard the player board of the player to whom the resources belong
     * @param warehouseLevel the warehouse level at which to add resources
     * @param onWhiteMarble the resource type in which convert this white marble
     * @throws IncorrectResourceTypeException the type of resources corresponding to this marble can't be added to this level of warehouse because it's occupied by another resource type
     * @throws NotEnoughSpaceException in this level of warehouse there isn't enough space
     * @throws LevelNotExistsException this level of warehouse doesn't exists
     */
    @Override
    public void addResource(InterfacePlayerBoard playerBoard, int warehouseLevel, ResourceType onWhiteMarble)
            throws IncorrectResourceTypeException, NotEnoughSpaceException, LevelNotExistsException {
        if(onWhiteMarble == null)
            return;
        if(onWhiteMarble == ResourceType.FAITH)
            playerBoard.getTrack().moveForward(1);
        try {
            playerBoard.getWarehouse().addResources(onWhiteMarble, warehouseLevel, 1);
        } catch (AbuseOfFaithException | NegativeQuantityException ignored) {}
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass() == obj.getClass();
    }
}
