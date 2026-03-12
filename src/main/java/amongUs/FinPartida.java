package main.java.amongUs;

/**
 * Clase de modelo que representa el estado final de una partida.
 * Se utiliza para transportar la información necesaria desde la lógica del juego
 * hasta la interfaz de usuario de "Fin de Partida", permitiendo mostrar quién ganó
 * y qué jugadores conformaban el equipo victorioso.
 * @author Angel Aguilera
 */
public class FinPartida {

    /** * Indica el bando ganador de la partida.
     * Valores típicos: "IMPOSTORES" o "TRIPULANTES".
     */
    public String ganador;

    /** * Cadena de texto con los nombres de los jugadores ganadores separados por comas (CSV).
     * Ejemplo: "Player1,Player2,Player3"
     */
    public String nombresCSV = "";

    /** * Cadena de texto con los colores asociados a los nombres en {@code nombresCSV},
     * separados por comas.
     * Se utiliza para renderizar los sprites de los ganadores con sus colores correctos.
     * Ejemplo: "rojo,azul,verde"
     */
    public String coloresCSV = "";
}