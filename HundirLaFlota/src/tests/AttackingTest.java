package tests;

import exceptions.CommandExecuteException;
import misc.Level;
import misc.Position;
import model.Game;
import model.Player;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AttackingTest {
    Game game;
    Player player = new Player("a", Level.EASY);

    @BeforeEach
    public void setUp() throws CommandExecuteException {
        game = Game.getInstance();
        game.init(Level.EASY, 2, 0, new String[]{"Player1", "Player2"});
        game.setCurrentPlayer(player);
    }

    @AfterEach
    public void reset() {
        game.reset();
    }

    @Test
    @DisplayName("Shooting a defeated player should throw an exception")
    public void testShootingDefeatedPlayer() {
        Player pAux = new Player("b", Level.EASY);
        pAux.setLost(true);
        assertThrows(CommandExecuteException.class, () -> game.attackShip(pAux, new Position(0, 0)), "A CommandExecuteException should be throw");
    }

    @Test
    @DisplayName("Shooting yourself should throw an exception")
    public void testShootingYourself() {
        assertThrows(CommandExecuteException.class, () -> game.attackShip(player, new Position(0, 0)), "A CommandExecuteException should be thrown");
    }
}