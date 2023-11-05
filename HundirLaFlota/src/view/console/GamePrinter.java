package view.console;

import controller.ControllerConsole;
import exceptions.GameException;
import misc.Position;
import model.GameDTO;
import model.PlayerDTO;
import utils.MyStringUtils;
import view.GameObserver;

import java.util.Scanner;

public class GamePrinter implements GameObserver {
    final String space = " ";
    private final Scanner scanner = new Scanner(System.in);
    ControllerConsole controller;

    public GamePrinter(ControllerConsole controller) {
        this.controller = controller;
        this.controller.addObserver(this);
    }

    public static void clearConsole() {
        try {
            String operatingSystem = System.getProperty("os.name");
            if (operatingSystem.contains("Windows")) {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();

                startProcess.waitFor();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String boardToString(GameDTO game, int player, int numCols, int numRows, boolean hidden) {
        int cellSize = 7;
        int marginSize = 2;
        String vDelimiter = "|";
        String hDelimiter = "-";
        String intersect = space;
        String vIntersect = space;
        String hIntersect = "-";
        String corner = space;
        String cellDelimiter = MyStringUtils.repeat(hDelimiter, cellSize);

        String rowDelimiter = vIntersect + MyStringUtils.repeat(cellDelimiter + intersect, numCols - 1) + cellDelimiter
                + vIntersect;
        String hEdge = corner + MyStringUtils.repeat(cellDelimiter + hIntersect, numCols - 1) + cellDelimiter + corner;

        String margin = MyStringUtils.repeat(space, marginSize);
        String lineEdge = String.format("%n%s%s%n", margin, hEdge);
        String lineDelimiter = String.format("%n%s%s%n", margin, rowDelimiter);

        StringBuilder str = new StringBuilder();
        str.append(game.getPlayerID(player));
        str.append(lineEdge);

        for (int i = 0; i < numRows; i++) {
            str.append(margin).append(vDelimiter);
            for (int j = 0; j < numCols; j++) {
                String aux;
                if (game.checkMiss(player, new Position(j, i)))
                    aux = "X";
                else
                    aux = game.getPositionToString(player, new Position(j, i));

                if (hidden && aux.equals("0"))
                    aux = " ";

                str.append(MyStringUtils.centre(aux, cellSize)).append(vDelimiter);
            }
            if (i != numRows - 1)
                str.append(lineDelimiter);
            else
                str.append(lineEdge);
        }
        return str.toString();
    }

    @Override
    public void update(GameDTO game) {
        clearConsole();
        boolean hidden = true;
        for (int i = 0; i < game.getNumberOfPlayers(); ++i) {
            if (game.isCurrentPlayer(i)) hidden = false;
            System.out.println(boardToString(game, i, game.getDimX(), game.getDimY(), hidden));
            hidden = true;
        }
    }

    @Override
    public void endScreen(PlayerDTO player) {
        System.out.println("GAME OVER." + player.getID() + " wins.");
    }

    @Override
    public String[] getInput(String str) {
        System.out.print(str);
        String s = scanner.nextLine();
        return s.toLowerCase().trim().split(" ");
    }

    @Override
    public void handleException(GameException ge) {
        System.out.println(ge.getMessage());
    }

    @Override
    public boolean cargar() {
        System.out.println("Do you want to load a game? (Y/n) ");
        String s = scanner.nextLine();
        return s.equals("Y") || s.equals("yes") || s.equals("y") || s.equals("YES") || s.equals("Yes");
    }
}
