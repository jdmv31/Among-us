package main.java.amongUs;
/**
 * Representa la solicitud inicial de un jugador para ingresar a una partida.
 * Esta clase actúa como el saludo entre el cliente y el servidor,
 * enviando la información básica necesaria para identificar al nuevo tripulante.
 * * @author Sebastián Arismendi
 */
public class PeticionUnirse {
    /** * El apodo que el jugador ha elegido para mostrar sobre su personaje
     * y en el chat durante las reuniones.
     */
    public String nombre;

    /**
     * Constructor por defecto.
     * Inicializa el nombre como una cadena vacía para prevenir errores de puntero nulo (NullPointerException)
     * durante la fase de registro en el servidor del juego.
     */
    public PeticionUnirse(){
        nombre = "";
    }
}
