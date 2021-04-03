package it.polimi.ingsw.model.singleplayer;

import it.polimi.ingsw.model.DevelopCardDeck;
import it.polimi.ingsw.modeltest.tracktest.LorenzoTrack;

import java.util.List;

public interface ActionToken {

    void useToken(List<ActionToken> tokens, LorenzoTrack trackLorenzo, DevelopCardDeck deck);

}
