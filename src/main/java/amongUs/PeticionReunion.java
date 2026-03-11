package main.java.amongUs;
/**
 * Representa un paquete de datos para solicitar el inicio de una fase de discusión.
 * Contiene la información necesaria para determinar quién inició la reunión
 * y bajo qué circunstancias ocurrió.
 *
 * @author Sebastián Arismendi
 */

public class PeticionReunion {
    /** Nombre o ID del jugador que activó la reunión o el reporte. */
    public String reportador;

    /** * Nombre o ID del jugador fallecido (si aplica).
     * Si la reunión es por botón de emergencia, este valor suele ser null o vacío.
     */
    public String cadaver;
    /** * Indica el origen de la reunión:
     * true: Se presionó el botón de emergencia en la mesa principal.
     * false: Se encontró un cuerpo y se usó el botón de "Reportar".
     */
    public boolean porBotonEmergencia;
}