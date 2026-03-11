package main.java.amongUs;

/**
 * @author Angel Aguilera
 * Clase de modelo que representa a un usuario dentro de la sala de espera (Lobby).
 * Se utiliza para almacenar y transmitir los atributos básicos de cada participante
 * antes de que la partida comience. Es fundamental para la sincronización de la
 * lista de jugadores y la asignación de colores en la interfaz de selección.
 */
public class JugadorLobby {

    /** Identificador único de la conexión asignado por el servidor (Kryonet). */
    public int conexionId;

    /** Nombre de usuario visible para los demás jugadores. */
    public String nombre;

    /** Representación en cadena del color elegido (ej. "rojo", "azul", "verde"). */
    public String color;

    /** * Indica si el jugador es el anfitrión de la sala.
     * El host suele tener permisos especiales, como iniciar la partida.
     */
    public boolean host;

    /**
     * Constructor por defecto de la clase.
     * Inicializa los valores con valores nulos o vacíos. Este constructor es
     * necesario para la serialización y deserialización de librerías de red como Kryonet.
     */
    public JugadorLobby() {
        conexionId = 0;
        nombre = "";
        color = "";
        host = false;
    }
}