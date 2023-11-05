package model;

import misc.Level;
import misc.Position;

import java.util.ArrayList;
import java.util.List;

public class GameDTO {
    private final List<PlayerDTO> players;
    private final Level level;
    private final PlayerDTO currentPlayer;

    public GameDTO(List<Player> players, Level level, int currentPlayerIndex) {
        this.players = new ArrayList<>();
        for (Player p : players) this.players.add(p.getDTO());
        this.level = level;
        this.currentPlayer = this.players.get(currentPlayerIndex);
    }

    public PlayerDTO getCurrentPlayer() {
        return currentPlayer;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public boolean isCurrentPlayer(int p) {
        return (players.get(p).equals(currentPlayer));
    }

    public int getDimX() {
        return level.getDimX();
    }

    public int getDimY() {
        return level.getDimY();
    }

    public String getPlayerID(int p) {
        return players.get(p).getID();
    }

    public String getPositionToString(int p, Position position) {
        return this.players.get(p).getPositionToString(position);
    }

    public boolean checkMiss(int player, Position position) {
        return players.get(player).checkMiss(position);
    }
}
