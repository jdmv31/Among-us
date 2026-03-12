package main.java.amongUs;

/**
 *
 * esta clase representa un paquete de red que se envia a traves de kryonet
 * notifica al servidor y a los demas jugadores que un impostor ha asesinado a alguien
 * @author Josue Medina
 */
public class Asesinato {
    /** username del impostor que ha asesinado a otro jugador*/
    public String asesino;
    /** username del jugador que ha sido asesinado*/
    public String victima;
}