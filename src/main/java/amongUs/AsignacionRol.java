package main.java.amongUs;

/**
 * paquete de datos empleado al iniciar cada partida
 * se le envia a cada jugador de forma individual para determinar si sera impostor o tripulante
 * @author Josue Medina
 */
public class AsignacionRol {
    /** indica el rol del jugador
     * si es {@code true} el jugador sera un impostor
     * si es {@code false} el jugador sera un tripulante
     */
    public boolean esImpostor;
}