package it.polimi.ingsw.model.market;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.modelexceptions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RedMarbleTest {

    @Test
    void addResourceTest() throws IncorrectResourceTypeException,
            LevelNotExistsException, NotEnoughSpaceException {
        InterfacePlayerBoard playerBoard = new PlayerBoard("test", new ArrayList<>(), null, null);

        MarketMarble marble1 = new RedMarble();

        int points0 = playerBoard.getTrack().getTrack()[0].getVictoryPoints(),
                points1 = 0,
                position = 0;
        for(int i = 0; i < playerBoard.getTrack().getTrack().length; i++)
            if(playerBoard.getTrack().getTrack()[i].getVictoryPoints() > points0) {
                points1 = playerBoard.getTrack().getTrack()[i].getVictoryPoints();
                position = i;
                break;
            }
        playerBoard.getTrack().moveForward(position - 1);
        assertEquals(points0, playerBoard.getTrack().calculateTrackScore());
        marble1.addResource(playerBoard, 0, null);
        assertEquals(points1, playerBoard.getTrack().calculateTrackScore());
       }

    @Test
    @SuppressWarnings({"Possible", "AssertBetweenInconvertibleTypes"})
    void testEqualsTest() throws AbuseOfFaithException {
        assertEquals(new RedMarble(), new RedMarble());
        assertNotEquals(new RedMarble(), new NormalMarble(ResourceType.SHIELD));
        assertNotEquals(new RedMarble(), new NormalMarble(ResourceType.STONE));
        assertNotEquals(new RedMarble(), new NormalMarble(ResourceType.GOLD));
        assertNotEquals(new RedMarble(), new NormalMarble(ResourceType.SERVANT));
        assertNotEquals(new RedMarble(), new WhiteMarble());
        assertNotEquals(new RedMarble(), new Object());
        assertNotEquals(new RedMarble(), null);
    }

}