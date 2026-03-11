package main.java.amongUs;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;

/**
 * Componente encargado de gestionar la lógica de reporte de cadáveres.
 * Controla la proximidad a los cuerpos, el estado visual del botón de reporte
 * y el envío de la petición de reunión al servidor.
 * * @author Sebastián Arismendi
 */

public class ReporteComponent extends Component {

    /** textura que permite interactuar con el botón de reportar
     */
    private Texture botonReportar;
    /** determina si existen los requisitos necesarios para generar un reporte
     */
    private boolean reporteDisponible = false;
    /** * Almacena el nombre o identificador del cadáver que se encuentra dentro del
     * rango de detección. Se limpia cuando el jugador se aleja.
     */
    private String cadaverCercano = "";

    /** Distancia máxima (en píxeles) para poder detectar y reportar un cuerpo. */
    private double distanciaDeteccion = 40.0;

    /**
     * Configura la textura del botón de interfaz y lo establece en estado inicial (desactivado).
     * @param botonReportar Objeto Texture que representa el botón en el HUD.
     */
    public void setBotonReportar(Texture botonReportar) {
        this.botonReportar = botonReportar;
        this.botonReportar.setImage(FXGL.image("reportarNegado.png"));
    }
    /**
     * Método de actualización constante (frame a frame).
     * Verifica las condiciones del jugador y la cercanía a entidades de tipo CADAVER.
     */
    @Override

/**
 * Ciclo de vida del componente que se ejecuta en cada frame del juego.
 * @param tpf (Time Per Frame) Tiempo transcurrido desde el último frame.
 */
    public void onUpdate(double tpf) {
        // Verificación de estado vital: Un fantasma no puede reportar cuerpos.
        if (AppPrincipal.estoyMuerto) return;
/**  Comprobación de contextos: Extrae estados de otros componentes o sistemas.
 *
 */
        /** ¿Está el jugador mirando las cámaras?
         *
         */
        boolean enCamaras = AppPrincipal.sistemaCamaras != null && AppPrincipal.sistemaCamaras.isCamarasAbiertas();
        /**
         * ¿Esta escondido en los ductos?
          */
        boolean enAlcantarilla = entity.hasComponent(ImpostorComponent.class) &&
                entity.getComponent(ImpostorComponent.class).estaEnAlcantarilla();

        /** Si el tripulante esta haciendo una tarea */

boolean enMinijuego = entity.hasComponent(TripulanteComponent.class) &&
                entity.getComponent(TripulanteComponent.class).isEnMinijuego();
/**  Restricción de interfaz: Si está ocupado en cualquiera de lo anterior,
        // se fuerza la desactivación del botón y se detiene el análisis.
 */
        if (enCamaras || enAlcantarilla || enMinijuego) {
            desactivarReporte();
            return;
        }
/** Escaneo del entorno: Busca todas las entidades marcadas como CADAVER en el mundo.
 *
 */
        boolean cercaDeCadaver = false;
        cadaverCercano = "";
        var cadaveres = FXGL.getGameWorld().getEntitiesByType(TipoEntidad.CADAVER);

        for (Entity cadaver : cadaveres) {
            /** Cálculo matemático de distancia euclidiana entre el jugador y el cadáver.
             */
            if (entity.getPosition().distance(cadaver.getPosition()) < distanciaDeteccion) {
                cercaDeCadaver = true; // Se marca que hay un objetivo válido
                cadaverCercano = "Alguien";
                break;
            }
        }

        if (cercaDeCadaver) {
            activarReporte();
        } else {
            desactivarReporte();
        }
    }
    /**
     * Cambia visualmente el botón a su versión activa y habilita la funcionalidad.
     */
    private void activarReporte() {
        if (!reporteDisponible && botonReportar != null) {
            botonReportar.setImage(FXGL.image("reportar.png"));
            reporteDisponible = true;
        }
    }
    /**
     * Cambia visualmente el botón a su versión "negada" y deshabilita la funcionalidad.
     */
    private void desactivarReporte() {
        if (reporteDisponible && botonReportar != null) {
            botonReportar.setImage(FXGL.image("reportarNegado.png"));
            reporteDisponible = false;
            cadaverCercano = "";
        }
    }
    /**
     * Ejecuta la acción de reportar. Crea una PeticionReunion y la envía
     * al servidor mediante TCP si todas las condiciones se cumplen.
     */
    public void intentarReportar() {
        if (reporteDisponible && !AppPrincipal.estoyMuerto && !cadaverCercano.isEmpty()) {
            PeticionReunion peticion = new PeticionReunion();
            peticion.reportador = MenuController.nombreUsuario;
            peticion.cadaver = cadaverCercano;
            peticion.porBotonEmergencia = false;

            if (AppPrincipal.miCliente != null && AppPrincipal.miCliente.cliente != null) {
                AppPrincipal.miCliente.cliente.sendTCP(peticion);
            }
            desactivarReporte();
        }
    }
}