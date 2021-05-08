package it.polimi.ingsw.model.market;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.modelexceptions.*;

public class NormalMarble extends MarketMarble{

    private ResourceType resource = null;

//    @SuppressWarnings("unused") // It may be called using reflection during JSON deserialization
//    private NormalMarble() {
//        super(MarbleColor.NORMAL);
//    }

    public NormalMarble(ResourceType resource) throws AbuseOfFaithException {
        super((resource == null) ? null : resource.getColor());
        if(resource == null)
            throw new NullPointerException();
        if(resource == ResourceType.FAITH)
            throw new AbuseOfFaithException();
        this.resource = resource;
    }

    /**
     * Adds the resources owed to the player due to this marble
     *
     * @param playerBoard the player board of the player to whom the resources belong
     * @throws NotEnoughSpaceException there isn't enough space in the warehouse to add this resource
     */
    @Override
    public void addResource(InterfacePlayerBoard playerBoard)
            throws NotEnoughSpaceException {
        try {
            playerBoard.getWarehouse().addResource(this.resource);
        } catch (AbuseOfFaithException e) {
            // This code should never be executed
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(this.getClass() != obj.getClass())
            return false;
        return this.resource == ((NormalMarble) obj).resource;
    }

}
