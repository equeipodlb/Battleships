package tests;

import exceptions.CommandExecuteException;
import exceptions.GameParseException;
import misc.Level;
import misc.Orientation;
import misc.Position;
import model.Game;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigTest {
    private Game game;
    private final String TESTS_PATH = "src/tests/saveTests/";
    private final String save = TESTS_PATH + "testSave";
    private final String corruptedSave = TESTS_PATH + "corruptedTestSave";

    @BeforeEach
    public void setUp() throws CommandExecuteException {
        game = Game.getInstance();
        game.init(Level.EASY, 2, 0, new String[]{"Player1", "Player2"});
        for (int i = 0; i < Level.EASY.getNumberOfShips(); ++i){
            game.placeShip(i, new Position(0, 2*i), Orientation.HORIZONTAL);
        }
        game.setCurrentPlayer(1);
        for (int i = 0; i < Level.EASY.getNumberOfShips(); ++i){
            game.placeShip(i, new Position(0, 2*i), Orientation.HORIZONTAL);
        }
        game.notifyPhaseChange();
    }

    @AfterEach
    public void reset() {
        game.reset();
    }

    @Test
    @DisplayName("Saving a game should work")
    public void testSaveGame() {
        game.save(save);
    }

    @Test
    @DisplayName("Loading a game should work")
    public void testLoadGame() throws FileNotFoundException, GameParseException {
        InputStream is = new FileInputStream(new File(save + ".json"));
        JSONObject jsonInput = new JSONObject(new JSONTokener(is));
        game.load(jsonInput);
    }

    @Test
    @DisplayName("Loading a corrupted game should throw an exception")
    public void testLoadCorruptedGame() throws FileNotFoundException {
        InputStream is = new FileInputStream(new File(corruptedSave + ".json")); // error in line 3
        JSONObject jsonInput = new JSONObject(new JSONTokener(is));
        assertThrows(GameParseException.class, () -> game.load(jsonInput), "A GameParseException should be thrown");
    }
}
