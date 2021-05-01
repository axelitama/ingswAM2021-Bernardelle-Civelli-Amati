package it.polimi.ingsw.network.action;

import it.polimi.ingsw.model.IGameState;
import it.polimi.ingsw.model.PhaseType;
import it.polimi.ingsw.model.ResourceType;
import it.polimi.ingsw.model.modelexceptions.*;

import java.util.Map;

public class ChooseResourcesAction extends Action {

    @SuppressWarnings({"UnusedDeclaration", "MismatchedQueryAndUpdateOfCollection"}) // Because the field value is assigned using reflection
    private Map<ResourceType, Integer> resources;

    @Override
    public PhaseType performAction(IGameState gameState) throws InvalidActionException,
            InvalidUsernameException, NegativeQuantityException, WrongResourceNumberException,
            AbuseOfFaithException, NotEnoughSpaceException {
        if(!super.checkValid(gameState))
            throw new InvalidActionException();
        if(this.resources.values().stream().anyMatch(i -> i < 0))
            throw new NegativeQuantityException();
        if(gameState.getGame().initialResources(super.username)
                != this.resources.values().stream().reduce(0, Integer::sum))
            throw new WrongResourceNumberException(
                    gameState.getGame().initialResources(super.username),
                    this.resources.values().stream().reduce(0, Integer::sum));
        for(ResourceType resource: this.resources.keySet())
            for(int i = 0; i < this.resources.get(resource); i++)
                gameState.getGame().getPlayerBoard(super.username).getWarehouse().addResource(resource);
        return PhaseType.SETUP_DISCARDLEADER;
    }

}
