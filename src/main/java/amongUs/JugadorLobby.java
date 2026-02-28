package main.java.amongUs;

public class JugadorLobby {
    public int conexionId;
    public String nombre;
    public String color;
    public boolean host;

    public JugadorLobby(){
        conexionId = 0;
        nombre = color = "";
        host = false;
    }
}
