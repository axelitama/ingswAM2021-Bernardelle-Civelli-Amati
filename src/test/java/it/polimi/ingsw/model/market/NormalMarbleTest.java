package it.polimi.ingsw.model.market;

import it.polimi.ingsw.model.InterfacePlayerBoard;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.ResourceType;
import it.polimi.ingsw.model.modelexceptions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class NormalMarbleTest {

    @Test
    void addResource() throws AbuseOfFaithException, IncorrectResourceTypeException, NegativeQuantityException,
            LevelNotExistsException, NotEnoughSpaceException {
        InterfacePlayerBoard playerBoard = new PlayerBoard("test", new ArrayList<>(), null, null);

        assertThrows(AbuseOfFaithException.class, () -> new NormalMarble(ResourceType.FAITH));

        MarketMarble marble1 = new NormalMarble(ResourceType.GOLD);
        playerBoard.getWarehouse().addResources(ResourceType.GOLD, 0, 1);
        marble1.addResource(playerBoard, 0, null);
        assertEquals(playerBoard.getWarehouse().getNumberOf(ResourceType.GOLD), 2);

        MarketMarble marble2 = new NormalMarble(ResourceType.SERVANT);
        marble2.addResource(playerBoard, 1, null);
        assertEquals(playerBoard.getWarehouse().getNumberOf(ResourceType.SERVANT), 1);
    }

    @Test
    @SuppressWarnings({"Possible", "AssertBetweenInconvertibleTypes"})
    void testEquals() throws AbuseOfFaithException {
        assertEquals(new NormalMarble(ResourceType.SHIELD), new NormalMarble(ResourceType.SHIELD));
        assertNotEquals(new NormalMarble(ResourceType.GOLD), new NormalMarble(ResourceType.SHIELD));
        assertNotEquals(new NormalMarble(ResourceType.SHIELD), new NormalMarble(ResourceType.STONE));
        assertNotEquals(new NormalMarble(ResourceType.SHIELD), new RedMarble());
        assertNotEquals(new NormalMarble(ResourceType.SHIELD), new WhiteMarble());
    }
}