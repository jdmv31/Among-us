package main.java.amongUs;
import java.util.HashMap;
/** Representa el paquete de datos enviado por el servidor a todos los clientes
 * al finalizar la fase de votación. Contiene el veredicto final y el desglose de los votos.
 *
 * @author Sebastián Arismendi
 */

public class ResultadoVotacion {
    /** * Nombre o identificador del jugador que recibió la mayoría de los votos.
     * Si nadie fue expulsado, este valor puede ser nulo o estar vacío.
     */
    public String expulsado;
    /** * Indica si la votación terminó sin expulsados debido a un empate de votos
     * o porque la mayoría eligió la opción de "Skip" (Saltar voto).
     */
    public boolean fueEmpateOSkip;
    /** * Bandera para la revelación final. Indica si el jugador expulsado era realmente
     * un impostor. (Se usa para la animación "X era/no era un Impostor").
     */
    public boolean expulsadoEraImpostor;

    /** * Mapa que relaciona a cada jugador (su nombre o ID) con la cantidad total
     * de votos que recibió en su contra. Ideal para mostrar las estadísticas en pantalla.
     */
    public HashMap<String, Integer> votosPorJugador;
}