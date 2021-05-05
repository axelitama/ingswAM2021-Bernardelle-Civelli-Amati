package it.polimi.ingsw.controller.action;

import it.polimi.ingsw.controller.controllerexception.InvalidActionException;
import it.polimi.ingsw.controller.controllerexception.NotAllowedActionException;
import it.polimi.ingsw.controller.controllerexception.WrongPlayerException;
import it.polimi.ingsw.model.IGameState;
import it.polimi.ingsw.model.PhaseType;
import it.polimi.ingsw.model.leadercard.LeaderCard;
import it.polimi.ingsw.model.modelexceptions.*;

public class ActivateLeaderAction extends Action {

    private Integer leaderCardIndex = null;

    @SuppressWarnings("unused") // It may be called using reflection during JSON deserialization
    private ActivateLeaderAction() {
        super(ActionType.ACTIVATE_LEADER);
    }

    public ActivateLeaderAction(int leaderCardIndex) {
        super(ActionType.ACTIVATE_LEADER);
        this.leaderCardIndex = leaderCardIndex;
    }

    public ActivateLeaderAction(String username, int leaderCardIndex) {
        super(ActionType.ACTIVATE_LEADER, username);
        this.leaderCardIndex = leaderCardIndex;
    }

    /**
     * Activate the specified leader card of the specified player.
     *
     * @param gameState the current state of this game
     * @return the next phase of this player's turn
     * @throws InvalidActionException this Action is not correctly initialized
     * @throws NotAllowedActionException this Action can't be performed in this phase of turn or game
     * @throws WrongPlayerException the player for which this action must be performed isn't the current player
     * @throws InvalidUsernameException the player for which this action must be performed doesn't exist in this game
     * @throws NotEnoughResourcesException the player doesn't have the requirements to activate this leader card
     * @throws InvalidLeaderCardException the specified leader card doesn't exist
     */
    @Override
    public PhaseType performAction(IGameState gameState)
            throws InvalidActionException, NotAllowedActionException, WrongPlayerException,
            InvalidUsernameException, NotEnoughResourcesException, InvalidLeaderCardException {
        if(!this.isActionValid())
            throw new InvalidActionException("This Action is not correctly initialized.");
        if(!super.isCurrentPlayer(gameState))
            throw new WrongPlayerException();
        if(!this.isActionAllowed(gameState))
            throw new NotAllowedActionException();
        if(gameState.getCurrentPhase() == PhaseType.PRODUCING)
            gameState.getGame().getPlayerBoard(super.getUsername()).enterFinalTurnPhase();

        LeaderCard leaderCard = gameState.getGame().getPlayerBoard(super.getUsername()).getLeaderCards().get(this.leaderCardIndex);
        gameState.getGame().getPlayerBoard(super.getUsername()).setActiveLeadercard(leaderCard);

        return gameState.getCurrentPhase() == PhaseType.PRODUCING ? PhaseType.FINAL : PhaseType.INITIAL;
    }

    private boolean isActionAllowed(IGameState gameState) {
        return gameState.getCurrentPhase().isValid(ActionType.ACTIVATE_LEADER);
    }

    private boolean isActionValid() {
        return super.getUsername() != null && this.leaderCardIndex != null;
    }

}