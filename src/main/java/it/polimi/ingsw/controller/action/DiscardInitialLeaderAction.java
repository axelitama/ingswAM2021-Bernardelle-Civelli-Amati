package it.polimi.ingsw.controller.action;

import it.polimi.ingsw.controller.controllerexception.InvalidActionException;
import it.polimi.ingsw.controller.controllerexception.NotAllowedActionException;
import it.polimi.ingsw.controller.controllerexception.WrongPlayerException;
import it.polimi.ingsw.model.IGameState;
import it.polimi.ingsw.model.PhaseType;
import it.polimi.ingsw.model.modelexceptions.InvalidLeaderCardException;
import it.polimi.ingsw.model.modelexceptions.InvalidUsernameException;
import it.polimi.ingsw.model.modelexceptions.LeaderIsActiveException;

public class DiscardInitialLeaderAction extends Action {

    private Integer leaderCardIndex1 = null;
    private Integer leaderCardIndex2 = null;

    @SuppressWarnings("unused") // It may be called using reflection during JSON deserialization
    private DiscardInitialLeaderAction() {
        super(ActionType.SETUP_DISCARD_LEADERS);
    }

    public DiscardInitialLeaderAction(int leaderCardIndex1, int leaderCardIndex2) {
        super(ActionType.SETUP_DISCARD_LEADERS);
        this.leaderCardIndex1 = leaderCardIndex1;
        this.leaderCardIndex2 = leaderCardIndex2;
    }

    public DiscardInitialLeaderAction(String username, int leaderCardIndex1, int leaderCardIndex2) {
        super(ActionType.SETUP_DISCARD_LEADERS, username);
        this.leaderCardIndex1 = leaderCardIndex1;
        this.leaderCardIndex2 = leaderCardIndex2;
    }

    /**
     * Discard the specified leader card for the player.
     *
     * @param gameState the current state of this game
     * @return the next phase of this player's turn
     * @throws InvalidActionException this Action is not correctly initialized
     * @throws NotAllowedActionException this Action can't be performed in this phase of turn or game
     * @throws WrongPlayerException the player for which this action must be performed isn't the current player
     * @throws InvalidUsernameException the player for which this action must be performed doesn't exist in this game
     * @throws InvalidLeaderCardException the specified leader card doesn't exist
     * @throws LeaderIsActiveException the specified leader is already active
     */
    @Override
    public PhaseType performAction(IGameState gameState)
            throws InvalidActionException, NotAllowedActionException, WrongPlayerException,
            InvalidUsernameException, InvalidLeaderCardException, LeaderIsActiveException {
        if(!this.isActionValid())
            throw new InvalidActionException("This Action is not correctly initialized.");
        if(!super.isCurrentPlayer(gameState))
            throw new WrongPlayerException();
        if(!this.isActionAllowed(gameState))
            throw new NotAllowedActionException();
        gameState.getGame().getPlayerBoard(super.getUsername()).discardLeaderAtBegin(this.leaderCardIndex1, this.leaderCardIndex2);
        return PhaseType.END_SETUP;
    }

    private boolean isActionAllowed(IGameState gameState) {
        return gameState.getCurrentPhase().isValid(ActionType.SETUP_DISCARD_LEADERS);
    }

    private boolean isActionValid() {
        return super.getUsername() != null && this.leaderCardIndex1 != null && this.leaderCardIndex2 != null;
    }

}