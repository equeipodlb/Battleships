package tests;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import exceptions.CommandExecuteException;
import misc.Level;
import misc.Orientation;
import misc.Position;
import model.Game;
import model.Ship;

public class PlacingTest {
	    private Game game;
	    private Ship ship;

	    @BeforeEach
	    public void setUp() throws CommandExecuteException {
	        game = Game.getInstance();
            game.init(Level.EASY, 2, 0, new String[]{"Player1", "Player2"});
	    }

        @AfterEach
        public void reset() {
            game.reset();
        }

        @Test
        @DisplayName("Placing a ship out of bounds should throw an exception")
        public void testPlaceShipOutOfBounds() {
            assertThrows(CommandExecuteException.class, () -> game.placeShip(0, new Position(-1, 28), Orientation.HORIZONTAL), "A CommandExecuteException should be thrown");
        }

	    @Test
	    @DisplayName("Placing two ships next to each other should not be possible")
	    public void testPlaceAdjacentShips() throws CommandExecuteException { //los barcos de easy son 1, 2, 3
	        game.placeShip(0, new Position(0, 0), Orientation.HORIZONTAL);
            assertThrows(CommandExecuteException.class, () -> game.placeShip(1, new Position(0, 1), Orientation.HORIZONTAL), "A CommandExecuteException should be thrown");
	    }
}
