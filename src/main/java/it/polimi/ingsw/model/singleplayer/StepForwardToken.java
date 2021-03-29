package it.polimi.ingsw.model.singleplayer;

import it.polimi.ingsw.model.DevelopCardDeck;
import it.polimi.ingsw.model.track.LorenzoTrack;

import java.util.List;

public class StepForwardToken implements ActionToken {

    public StepForwardToken() {}

    @Override
    public void useToken(List<ActionToken> tokens, LorenzoTrack trackLorenzo, DevelopCardDeck deck) {
        trackLorenzo.moveForward(2);
    }

}