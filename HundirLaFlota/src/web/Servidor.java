package web;

import java.net.*;
import java.util.*;

import misc.Level;
import view.GUI.HostingWindow;

import java.io.DataOutputStream;
import java.io.IOException;

public class Servidor extends Conexion { //Se hereda de conexion para hacer uso de los sockets y dem�s
    public Servidor(int numPlayers) throws IOException {
        super("servidor");

    } //Se usa el constructor para servidor de Conexion

    /**
     * Given a list of Sockets, a number of players, the level and the hosting window, this method is in charge of accepting
     * all new clients that may connect to the server, adding them to the list and updating the corresponding window when it does.
     * Also gives each client an integer representing their position in the list and the level.
     *
     * @param socketList - the empty list of {@code Socket} to be filled
     * @param numPlayers - An int representing the number of players that will be conecting to the server
     * @param ventana    - the corresponding {@code HostingWindow} to be updated
     * @param level      - the {@code Level} of the game
     */
    public void startServer(List<Socket> socketList, int numPlayers, HostingWindow ventana, Level level)//Metodo para iniciar el servidor
    {
        try {
            System.out.println("Esperando..."); //Esperando conexion
            //Accept comienza el socket y espera una conexion desde un cliente. Es decir, es un metodo que bloquea la ejecucion del codigo
            //Acepto a todos los jugadores
            for (int i = 0; i < numPlayers; ++i) {
                socketList.add(i, ss.accept());
                ventana.setConnectedPlayers(i + 1); // Cada vez que se conecta alguien actualizamos la hosting window
            }
            for (int i = 0; i < socketList.size(); ++i) {
                DataOutputStream out;
                String s = level.getName();
                try {

                    out = new DataOutputStream(socketList.get(i).getOutputStream());
                    out.writeUTF("{level:" + s + ",index:" + i + "}"); // Ademas asignamos a cada cliente un indice segun su orden de llegada

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
