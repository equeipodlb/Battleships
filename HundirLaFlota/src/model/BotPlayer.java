package model;

import misc.Cell;
import misc.Level;

import misc.Orientation;
import misc.Position;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import exceptions.CommandExecuteException;

public class BotPlayer extends Player {
    Random randomNumber = new Random();
    Level level;
    Game game;
    int xRem, yRem;
    boolean placed;
    Position lastHit;
    Integer lastPlayer;
    Queue<Position> possibleShots;
    ArrayList<Position> strat2Positions;

    public BotPlayer(String id, Level level, Game game) {
        super(id, level);
        this.real = false;
        this.level = level;
        this.game = game;
        this.xRem = 0;
        this.yRem = 0;
        this.placed = false;
        this.possibleShots = new LinkedList<Position>();
        this.strat2Positions = new ArrayList<Position>();
        for (int i = 0; i < 4; ++i)
            strat2Positions.add(new Position(0, 0));
    }

    /**
     * This method randomly places all this bot's ships. Exceptions of type {@link CommandExecuteException} are caught but ignored,
     * so the bot's can keep trying to place their ships if the position they've chosen is not valid.
     *
     * @return {@code true} to indicate that the ships have been placed.
     */
    public boolean place() {
        while (!this.allShipsPlaced()) {
            int x = randomNumber.nextInt(level.getDimX());
            int y = randomNumber.nextInt(level.getDimY());
            boolean o = randomNumber.nextBoolean();
            Orientation orientation;
            orientation = (!o) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            try {
                game.placeShip(new Position(x, y), orientation);
            } catch (CommandExecuteException cee) {
                //la captura pero no hace nada, para que siga
            }
        }
        return true;
    }

    /**
     * This method performs the strategy for the EASY level bots. It shoots randomly.
     */
    private void botAttack1() { //aleatorio
        int x = randomNumber.nextInt(level.getDimX());
        int y = randomNumber.nextInt(level.getDimY());
        int p = randomNumber.nextInt(game.getNumberOfPlayers());
        while (!game.checkPlayer(p)) {
            p = randomNumber.nextInt(game.getNumberOfPlayers());
        }
        Position pos = new Position(x, y);
        while (!game.checkPosition(pos) || !game.botAttack(p, pos)) {
            x = randomNumber.nextInt(level.getDimX());
            y = randomNumber.nextInt(level.getDimY());
            pos = new Position(x, y);
        }
        if (game.getPlayer(p).hasBeenDefeated()) game.getPlayer(p).setLost(true);
    }

    /**
     * This method performs the strategy for the HARD level bots. It shoots at all positions from left to right and from top to bottom.
     */
    private void botAttack2() { //de arriba izq a abajo derecha
        int p = randomNumber.nextInt(game.getNumberOfPlayers());
        while (!game.checkPlayer(p)) p = randomNumber.nextInt(game.getNumberOfPlayers());
        int x = strat2Positions.get(p).getX();
        int y = strat2Positions.get(p).getY();
        Position pos = new Position(x, y);
        while (!game.botAttack(p, pos)) { //no hace falta checkPos porque siempre son pos del tablero
            if (x < game.getDimX() - 1) x++;
            else if (y < game.getDimY() - 1) {
                x = 0;
                y++;
            }
            pos = new Position(x, y);
        }
        if (x < game.getDimX() - 1) x++;
        else if (y < game.getDimY() - 1) {
            x = 0;
            y++;
        }
        if (game.getPlayer(p).hasBeenDefeated()) game.getPlayer(p).setLost(true);
    }

    /**
     * This method performs the strategy for the INSANE level bots. It shoots at random but when a ship is hit its adjacent
     * positions are saved into a queue so the chances of sinking the ship are greater.
     */
    private void botAttack3() {
        if (possibleShots.isEmpty()) {
            int x = randomNumber.nextInt(level.getDimX());
            int y = randomNumber.nextInt(level.getDimY());
            int p = randomNumber.nextInt(game.getNumberOfPlayers());
            while (!game.checkPlayer(p)) p = randomNumber.nextInt(game.getNumberOfPlayers());
            while (!shot(p, new Position(x, y))) {
                x = randomNumber.nextInt(level.getDimX());
                y = randomNumber.nextInt(level.getDimY());
            }
            if (game.getPlayer(p).checkHit(new Position(x, y))) {
                addAdjacent(x, y);
            }
            if (game.getPlayer(p).hasBeenDefeated()) game.getPlayer(p).setLost(true);
        } else {
            Position attack = possibleShots.poll();
            shot(lastPlayer, attack);
            int x = attack.getX();
            int y = attack.getY();
            if (game.getPlayer(lastPlayer).checkHit(attack)) {
                addAdjacent(x, y);
            }
            if (game.getPlayer(lastPlayer).hasBeenDefeated()) game.getPlayer(lastPlayer).setLost(true);
        }
    }

    /**
     * This method adds to {@link #possibleShots} the adjacent positions to the position {@code (x, y)}.
     *
     * @param x an {@code int} representing the {@code x} coordinate of the position.
     * @param y an {@code int} representing the {@code y} coordinate of the position.
     */
    private void addAdjacent(int x, int y) {
        Position pos = new Position(x + 1, y);
        if (game.checkPosition(pos) && !game.getPlayer(lastPlayer).checkMissOrHit(pos))
            possibleShots.add(pos);
        pos = new Position(x - 1, y);
        if (game.checkPosition(pos) && !game.getPlayer(lastPlayer).checkMissOrHit(pos))
            possibleShots.add(pos);
        pos = new Position(x, y + 1);
        if (game.checkPosition(pos) && !game.getPlayer(lastPlayer).checkMissOrHit(pos))
            possibleShots.add(pos);
        pos = new Position(x, y - 1);
        if (game.checkPosition(pos) && !game.getPlayer(lastPlayer).checkMissOrHit(pos))
            possibleShots.add(pos);
    }

    /**
     * This method is a combination of {@link Game#checkPosition(Position)} and {@link Game#botAttack(int, Position)}.
     * It is used by {@link #botAttack3()}.
     *
     * @param player the {@code Player} that will be checked.
     * @param pos    the {@code Position} that will be checked.
     * @return {@code true} if attacking was possible, {@code false} otherwise.
     */
    private boolean shot(int player, Position pos) {
        if (!game.checkPosition(pos) || !game.botAttack(player, pos)) return false;
        else {
            if (game.getPlayer(player).observableBoard[pos.getX()][pos.getY()] == Cell.HIT) {
                lastHit = pos;
                lastPlayer = player;
            }
            return true;
        }
    }

    /**
     * This method chooses the correct strategy for a {@code BotPlayer} depending on the {@link Level} difficulty.
     *
     * @return {@code true} to indicate that the attack has been performed correctly.
     */
    public boolean attack() {//utilizar el entero de la dificultad para saber a que estrategia llamar
        switch (this.level) {
            case EASY:
                this.botAttack1();
                break;
            case HARD:
                this.botAttack2();
                break;
            case INSANE:
                this.botAttack3();
                break;
        }
        return true;
    }
}


