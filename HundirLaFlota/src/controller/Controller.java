package controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import exceptions.GameParseException;
import org.json.JSONObject;

import exceptions.CommandExecuteException;
import misc.Level;
import misc.Orientation;
import misc.Position;
import model.PlayerDTO;
import view.GameObserver;

public interface Controller {
    public void addObserver(GameObserver v);
    default public void run() {};
    public List<PlayerDTO> getListOfPlayers();
    public Level getLevel();
    public void setConfig(Level level, int realPlayers, int botPlayers, List<String> playerNames) throws CommandExecuteException;
    public int getCurrentPlayer();
    public void placeShip(int currentShip, Position pos, Orientation o) throws CommandExecuteException;;
    public void attackPos(int currentPlayer, Position position, int player) throws CommandExecuteException;
	public void setCurrentShip(int i);
	public default void attackBot() {return;}
	public default void save(String file) {};
	public default void load(String file) throws GameParseException, FileNotFoundException {};
	public default void changePhase() {}
    public default void reset() {};
	default public Socket getSocket() {return null;}
	public void getNotify();
	public boolean getStart();
	public default void sendName(String playerName) {}
	public default int getIndex() {return 0;}
	public default void parseConfi(JSONObject jsonInput) {}
	public default void receiveInfo() {}
}
