package tests;

import static org.junit.jupiter.api.Assertions.assertThrows;

import exceptions.CommandExecuteException;
import misc.Level;
import model.Game;
import model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class RegisterOfPlayersTest {
	Game game;

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
    @DisplayName("Registering two players with the same name")
    public void testSameNames() {
    	   assertThrows(CommandExecuteException.class, () -> game.addPlayer(new Player("Player1")), "A CommandExecuteException should be thrown");
    }
}
