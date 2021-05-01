package it.polimi.ingsw.network.action;

import it.polimi.ingsw.model.IGameState;
import it.polimi.ingsw.model.PhaseType;
import it.polimi.ingsw.model.modelexceptions.*;

public class BuyDevelopCardAction extends Action {

    @SuppressWarnings("UnusedDeclaration") // Because fields' value is assigned using reflection
    private int row,
            column,
            cardSlotIndex;

    @Override
    public PhaseType performAction(IGameState gameState) throws InvalidActionException,
            InvalidUsernameException, NotBuyableException, InvalidCardPlacementException,
            InvalidDevelopCardException {
        if(!super.checkValid(gameState))
            throw new InvalidActionException();
        gameState.getGame().getPlayerBoard(super.username).addDevelopCard(row, column, cardSlotIndex);
        return PhaseType.FINAL;
    }

}
