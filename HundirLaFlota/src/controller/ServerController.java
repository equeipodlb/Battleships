package controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import exceptions.GameParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import exceptions.CommandExecuteException;
import exceptions.OrientationFormatException;
import misc.Level;
import misc.Orientation;
import misc.Position;
import model.Game;
import model.Player;
import model.PlayerDTO;
import view.GameObserver;
import view.GUI.HostingWindow;
import web.Servidor;

public class ServerController implements Controller {
    private final List<Socket> socketList;
    private final Servidor server;
    private final Game game;
    private final Level level;
    private final Socket actualSocket;

    public ServerController(int numPlayers, Level level, Game game, Servidor server, HostingWindow hostingWindow) throws IOException {
        this.socketList = new ArrayList<Socket>(0);
        this.server = server;
        this.game = game;
        this.server.startServer(socketList, numPlayers, hostingWindow, level);
        this.level = level;
        this.actualSocket = socketList.get(0);
    }

    @Override
    public void receiveInfo() {
        String ataque;
        try {
            DataInputStream entrada = new DataInputStream(actualSocket.getInputStream());
            ataque = entrada.readUTF();
            JSONObject jsonInput = new JSONObject(new JSONTokener(ataque));
            if (jsonInput.has("set")) {
                parseSet(jsonInput.getJSONObject("set"));
            }
        } catch (IOException | GameParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method parses the information received from a "set" type of info
     *
     * @param jsonObject - representing the attack/place or configuration sent
     * @throws GameParseException
     */
    private void parseSet(JSONObject jsonObject) throws GameParseException {
        if (jsonObject.has("configuration")) {
            parseConfi(jsonObject.getJSONObject("configuration"));

        } else if (jsonObject.has("currentShip")) {

            int i = jsonObject.getInt("currentShip");
            game.setCurrentShip(i);
        }
    }

    /**
     * For each socket in the list, this method waits to receive the corresponding information relative to the ships' placement and effectively
     * uses the model to place them while also notifying the clients of the placement and the player change if necessary
     *
     * @param: none
     * @return: none
     */
    public void placingPhase() {
        for (int i = 0; i < socketList.size(); ++i) {
            Socket sc = socketList.get(i);
            while (!game.allShipsPlaced(i)) {
                try {
                    DataInputStream entrada = new DataInputStream(sc.getInputStream());
                    receiveInfo();
                    String place = entrada.readUTF();
                    JSONObject jsonInput = new JSONObject(new JSONTokener(place));

                    int ship = jsonInput.has("currentShip") ? jsonInput.getInt("currentShip") : null;
                    JSONArray posiciones = jsonInput.has("position") ? jsonInput.getJSONArray("position") : null;
                    Position posicion = Position.parse(posiciones);
                    String orientacionShortcut = jsonInput.has("orientation") ? jsonInput.getString("orientation") : null;
                    Orientation o = null;
                    try {
                        o = Orientation.parse(orientacionShortcut);
                    } catch (OrientationFormatException e) {
                        e.printStackTrace();
                    }
                    DataOutputStream salida = new DataOutputStream(sc.getOutputStream());
                    try {
                        game.placeShip(ship, posicion, o);
                        notifyPlacedShip(i, game.getPlayers().get(i));

                    } catch (CommandExecuteException e) {
                        salida.writeUTF("Place no realizado");
                        e.printStackTrace();
                    }
                } catch (IOException | GameParseException e) {
                    e.printStackTrace();
                }
            }
            notifyCurrentPlayer((i + 1) % socketList.size());
        }
    }

    private void notifyCurrentPlayer(int i) {
        for (Socket sc : socketList) {
            try {
                DataOutputStream salida = new DataOutputStream(sc.getOutputStream());
                salida.writeUTF("{changePlayer:" + i + "}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * For each socket in the list, this method waits to receive the corresponding information relative to each players' attack and effectively
     * uses the model to attack the corresponding players while also notifying the clients of the attack and the player change if necessary
     */
    public void attackingPhase() {
        while (!game.isFinished()) { // Mientras que el juego no haya terminado
            for (int i = 0; i < socketList.size(); ++i) { // Para cada cliente
                Socket sc = socketList.get(i);
                try {
                    DataInputStream entrada = new DataInputStream(sc.getInputStream());
                    String ataque;
                    // Esperamos a leer un ataque (un JSON de la forma [player,Position]
                    ataque = entrada.readUTF();
                    JSONObject jsonInput = new JSONObject(new JSONTokener(ataque));
                    // Parsear ataque
                    int jugador;
                    jugador = jsonInput.has("player") ? jsonInput.getInt("player") : null;
                    Position posicion;
                    JSONArray posiciones = jsonInput.has("position") ? jsonInput.getJSONArray("position") : null;
                    posicion = Position.parse(posiciones);
                    DataOutputStream salida = new DataOutputStream(sc.getOutputStream());
                    try { // Una vez parseado:
                        game.attackShip(jugador, posicion); // Realizamos el ataque
                        boolean hitOrMiss = game.checkMiss(jugador, posicion); // Comprobamos si fue o no miss
                        JSONObject ataqueJson = new JSONObject();
                        ataqueJson.put("miss", hitOrMiss);
                        ataqueJson.put("position", posiciones);
                        ataqueJson.put("player", jugador);
                        salida.writeUTF(ataqueJson.toString()); // Se envia al cliente para que pueda hacer
                        // onAttack(player,Position,hitOrMiss)
                    } catch (CommandExecuteException e) { // Si por alguna razon el ataque falla
                        salida.writeUTF("Ataque no realizado");
                        e.printStackTrace();
                    }
                } catch (IOException | GameParseException e) {
                    System.out.println(e.getMessage());
                }
                notifyCurrentPlayer((i + 1) % socketList.size());
            }
        }
    }

    //ENVIO DE NOTIFICACIONES A LOS CLIENTES.
    void notifyPhaseChange() {
        for (Socket s : socketList) {
            DataOutputStream out;
            try {
                out = new DataOutputStream(s.getOutputStream());
                out.writeUTF("{phaseChange:}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Given an int representing the player and its DTO instance, this method notifies every player that the player has placed a ship
     *
     * @param: currentPlayerNum - an integer representing the player
     * @param: dto - the {@code PlayerDTO} instance of the player
     */
    private void notifyPlacedShip(int currentPlayerNum, PlayerDTO dto) {
        for (Socket s : socketList) {
            DataOutputStream out;
            try {
                out = new DataOutputStream(s.getOutputStream());
                String mensaje;
                mensaje = "{placedShip:{";
                mensaje += "currentPlayer:" + currentPlayerNum;
                mensaje += ",playerDTO:" + dto.toJson().toString();
                out.writeUTF(mensaje + "}}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void savedGame(String file) {
        for (Socket s : socketList) {
            DataOutputStream out;
            try {
                out = new DataOutputStream(s.getOutputStream());
                out.writeUTF("{savedGame:" + file + "}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void endScreen(Player player) {
        for (Socket s : socketList) {
            DataOutputStream out;
            try {
                out = new DataOutputStream(s.getOutputStream());
                out.writeUTF("{endScreen:" + player.getState().toString() + "}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void changePlayer(int i) {
        for (Socket s : socketList) {
            DataOutputStream out;
            try {
                out = new DataOutputStream(s.getOutputStream());

                out.writeUTF("{changePlayer:" + i + "}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void defeatedPlayer(int i) {
        for (Socket s : socketList) {
            DataOutputStream out;
            try {
                out = new DataOutputStream(s.getOutputStream());
                out.writeUTF("{defeatedPlayer:" + i + "}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addObserver(GameObserver v) {
    }

    @Override
    public List<PlayerDTO> getListOfPlayers() {
        return game.getPlayers();
    }

    @Override
    public void setConfig(Level level, int realPlayers, int botPlayers, List<String> playerNames) throws CommandExecuteException {
        String[] array = new String[playerNames.size()];
        for (int i = 0; i < playerNames.size(); ++i) {
            array[i] = playerNames.get(i);
        }
        game.init(level, realPlayers, botPlayers, array);

    }

    @Override
    public int getCurrentPlayer() {
        return game.getCurrentPlayer();
    }

    @Override
    public Level getLevel() {
        return game.getLevel();
    }

    /**
     * Given an empty list of strings, this method updates it with the corresponding names of every player
     *
     * @param: list - A list of strings
     */
    public void retrieveNames(List<String> list) {
        for (int i = 0; i < socketList.size(); ++i) {
            try {
                DataInputStream entrada = new DataInputStream(socketList.get(i).getInputStream());
                String nombre = entrada.readUTF();
                list.add(nombre);
                System.out.println(nombre);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Given a level, the number of real and bot players and the list of names. this method creates and sends the information the clients need
     * to initialize the configuration of their games
     *
     * @param: level - the {@code: Level} chosen
     * @param: realPlayers - the number of real players
     * @param: playerNames - the names of the players
     */
    public void sendConfig(Level level2, int realPlayers, List<String> playerNames2) {
        DataOutputStream out;
        String s = createJSONConfig(level2, realPlayers, playerNames2);
        try {
            for (Socket cs : socketList) {
                out = new DataOutputStream(cs.getOutputStream());
                out.writeUTF(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createJSONConfig(Level level2, int realPlayers, List<String> playerNames2) {
        String s = "{set:{configuration:";
        s += "{";
        s += "Level:" + level.getName() + ",";
        s += "realplayers:" + realPlayers + ",";
        s += "botplayers:" + 0 + "},";
        s += "names:[";
        for (String name : playerNames2) {
            s += name + ",";
        }
        StringBuilder s1 = new StringBuilder();
        s1.append(s);
        if (s.length() > 1)
            s1.deleteCharAt(s1.length() - 1);
        s1.append("]");
        s = s1.toString();
        s += "}}}";
        return s;
    }

    @Override
    public void attackBot() {
    }


    @Override
    public void setCurrentShip(int i) {
    }


    @Override
    public void getNotify() {
    }


    @Override
    public boolean getStart() {
        return false;
    }


    @Override
    public void attackPos(int currentPlayer, Position position, int player) throws CommandExecuteException {
    }


    @Override
    public void placeShip(int currentShip, Position pos, Orientation o) throws CommandExecuteException {
    }
}
