package view;

import exceptions.GameException;
import misc.Position;
import model.GameDTO;
import model.PlayerDTO;

public interface GameObserver {
	default void update(GameDTO game) {};
	default void handleException(GameException ge) {};
	default void endScreen(PlayerDTO player) {}
	default String[] getInput(String str) { return null; }
	default void onPlacedShip(int currentPlayer, PlayerDTO currentPlayerDTO) {};
	default void onAttack(PlayerDTO player, int playerIndex, Position pos) {};
	default void onDefeatedPlayer (int player) {};
	default void onSunkShip (String currentPlayerName, String attackedPlayerName) {};
	default void onChangedPlayer(int newCurrentPlayer, boolean real) {};
	default boolean cargar() {return false;};
	default void onPhaseChange() {};//notifica cuando se pasa de la fase placing a la fase attacking
    default void onSavedGame(String file) {};
}



