package main.java.amongUs;

/**
 * @author Josue Medina
 * esta clase representa un paquete de red que se envia a traves de kryonet
 * notifica al servidor y a los demas jugadores que un impostor ha asesinado a alguien
 */
public class Asesinato {
    /** username del impostor que ha asesinado a otro jugador*/
    public String asesino;
    /** username del jugador que ha sido asesinado*/
    public String victima;
}