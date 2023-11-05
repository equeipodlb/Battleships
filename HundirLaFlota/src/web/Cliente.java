package web;

import java.io.*;
import java.net.*;

public class Cliente extends Conexion {
    public Cliente(String host) throws IOException {
        this.cs = new Socket(host, PUERTO);
    } //Se usa el constructor para cliente de Conexion

    public InputStream getInputStream() throws IOException {
        return this.cs.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return this.cs.getOutputStream();
    }

    public Socket getCs() {
        return cs;
    }
}
