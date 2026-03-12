package main.java.amongUs;
/**
 * paquete empleado por el servidor para determinar la posicion actual de cada persona conectada
 * para posteriormente reflejar su movimiento al resto de personas jugando
 * @author Nicole Flores
 * */

public class Movimiento {
    public String username;
    public int x;
    public int y;

    public Movimiento(){
        username = "";
        x = y = 0;
    }
}
