package main.java.amongUs;

import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

/**
 * Clase encargada de manejar el sistema de camaras de seguridad del mapa
 * Permite a los jugadores interactuar con el monitor para vigilar distintas zonas,
 * controlando la interfaz visual y el movimiento de la pantalla
 * * @author Angelo Martini
 * */
public class SistemaCamaras {
    private boolean camarasAbiertas = false;
    private Texture uiCamaras;
    private Texture btnIzq;
    private Texture btnDer;
    private int indiceCamaraActual = 0;
    private Point2D ubicacionMesaCamaras;
    private Point2D[] coordenadasCamaras;

    /**
     * Prepara el sistema cargando las imagenes de la interfaz y estableciendo los puntos de interes
     * Tambien asigna los eventos de clic a los botones para cambiar de camara
     * * @param ubicacionMesa el punto exacto (X, Y) donde se encuentra el monitor fisico en el mapa
     * @param coordenadas un arreglo con todos los puntos que enfoca cada camara de seguridad
     * */
    public void inicializar(Point2D ubicacionMesa, Point2D[] coordenadas) {
        this.ubicacionMesaCamaras = ubicacionMesa;
        this.coordenadasCamaras = coordenadas;

        uiCamaras = FXGL.texture("MonitorDeCamaras.png");
        btnIzq = FXGL.texture("flechaAmarillaIzq.png");
        btnDer = FXGL.texture("flechaAmarillaDer.png");

        // Asignamos la accion de avanzar o retroceder segun la flecha que se toque
        btnIzq.setOnMouseClicked(e -> cambiarCamara(-1));
        btnDer.setOnMouseClicked(e -> cambiarCamara(1));
    }

    /**
     * Valida si el jugador cumple las condiciones para acceder al monitor
     * Revisa la distancia hacia la mesa y se asegura de que no haya un sabotaje activo
     * * @param jugador la entidad del jugador que intenta interactuar
     * @param esImpostor nos sirve para aplicar restricciones especificas si el que interactua es el impostor
     * */
    public void intentarAbrirCamaras(Entity jugador, boolean esImpostor) {
        // Si sabotearon las comunicaciones, las camaras no sirven
        if (AppPrincipal.sabotajeActivo) return;

        if (!esImpostor && jugador != null) {
            double distancia = jugador.getPosition().distance(ubicacionMesaCamaras);

            // Permitimos abrir si esta a menos de 50 pixeles, o cerrar si ya las tenia abiertas
            if (distancia < 50 || camarasAbiertas) {
                alternarCamaras(jugador);
            } else {
                System.out.println("Estas muy lejos de las camaras para usarlas.");
            }
        }
    }

    /**
     * Hace el switch entre la vista de jugador y la vista de camaras
     * Oculta la oscuridad, esconde la interfaz de tareas y mueve la pantalla al punto de la camara
     * * @param jugador se requiere para frenar su movimiento y para volver a anclarle la camara al salir
     * */
    public void alternarCamaras(Entity jugador) {
        com.almasb.fxgl.app.scene.Viewport viewport = FXGL.getGameScene().getViewport();

        if (camarasAbiertas) {
            // Proceso de cerrado: quitamos la UI de camaras

            FXGL.removeUINode(uiCamaras);
            FXGL.removeUINode(btnIzq);
            FXGL.removeUINode(btnDer);
            camarasAbiertas = false;

            // Restauramos la neblina del mapa

            if (AppPrincipal.oscuridad != null) AppPrincipal.oscuridad.setVisible(true);

            // Devolvemos la lista de tareas a la pantalla del tripulante

            if (jugador.hasComponent(TripulanteComponent.class)) {
                jugador.getComponent(TripulanteComponent.class).getBarraTareasUI().setVisible(true);
                jugador.getComponent(TripulanteComponent.class).getContenedorTareas().setVisible(true);
            }

            // Volvemos a seguir al jugador con la camara

            viewport.setZoom(2.5);
            viewport.bindToEntity(jugador, FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0);

        } else {
            /* Proceso de apertura: ponemos la UI de camaras encima de todo */

            FXGL.addUINode(uiCamaras, 0, 0);
            FXGL.addUINode(btnIzq, -10, 390);
            FXGL.addUINode(btnDer, 590, 390);
            camarasAbiertas = true;

            // Quitamos la sombra para ver el mapa limpio

            if (AppPrincipal.oscuridad != null) AppPrincipal.oscuridad.setVisible(false);

            // Escondemos la UI de tareas para que no estorbe

            if (jugador.hasComponent(TripulanteComponent.class)) {
                jugador.getComponent(TripulanteComponent.class).getBarraTareasUI().setVisible(false);
                jugador.getComponent(TripulanteComponent.class).getContenedorTareas().setVisible(false);
            }

            // Frenamos en seco al jugador modificando su componente fisico

            if (jugador != null && jugador.hasComponent(com.almasb.fxgl.physics.PhysicsComponent.class)) {
                jugador.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityX(0);
                jugador.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityY(0);
            }

            // Desvinculamos la pantalla del jugador y mostramos la primera camara

            viewport.unbind();
            viewport.setZoom(1.5);
            indiceCamaraActual = 0;
            actualizarVistaCamara(viewport);
        }
    }

    /**
     * Navega por el arreglo de coordenadas de las camaras formando un ciclo
     * Si llega al final, vuelve al principio y viceversa
     * * @param direccion recibe 1 para avanzar a la derecha o -1 para retroceder a la izquierda
     * */
    private void cambiarCamara(int direccion) {
        indiceCamaraActual += direccion;
        if (indiceCamaraActual < 0) {
            indiceCamaraActual = coordenadasCamaras.length - 1;
        } else if (indiceCamaraActual >= coordenadasCamaras.length) {
            indiceCamaraActual = 0;
        }
        actualizarVistaCamara(FXGL.getGameScene().getViewport());
    }

    /**
     * Mueve el area visible del juego hacia la coordenada de la camara seleccionada actualmente
     * Centra la vista restando la mitad del ancho y alto visible
     * * @param viewport el manejador de la camara interna de FXGL
     * */
    private void actualizarVistaCamara(Viewport viewport) {
        Point2D coord = coordenadasCamaras[indiceCamaraActual];
        double anchoVisible = FXGL.getAppWidth() / 1.5;
        double altoVisible = FXGL.getAppHeight() / 1.5;
        viewport.setX(coord.getX() - (anchoVisible / 2));
        viewport.setY(coord.getY() - (altoVisible / 2));
    }

    /**
     * Metodo de auxilio para apagar las camaras bruscamente
     * Es ideal para situaciones donde llaman a una reunion de emergencia o el jugador es asesinado mientras miraba las camaras
     * * @param jugador la entidad a la que se le cerrara la interfaz
     * */
    public void forzarCierre(Entity jugador) {
        if (camarasAbiertas) {
            alternarCamaras(jugador);
        }
    }

    /**
     * @return true si el monitor de camaras esta en uso, false si esta cerrado
     * */
    public boolean isCamarasAbiertas() { return camarasAbiertas; }

    /**
     * @return un objeto Point2D con las coordenadas (X, Y) de la mesa fisica de camaras
     * */
    public Point2D getUbicacionMesaCamaras() { return ubicacionMesaCamaras; }
}
