package main.java.amongUs;

import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

public class SistemaCamaras {
    private boolean camarasAbiertas = false;
    private Texture uiCamaras;
    private Texture btnIzq;
    private Texture btnDer;
    private int indiceCamaraActual = 0;
    private Point2D ubicacionMesaCamaras;
    private Point2D[] coordenadasCamaras;

    public void inicializar(Point2D ubicacionMesa, Point2D[] coordenadas) {
        this.ubicacionMesaCamaras = ubicacionMesa;
        this.coordenadasCamaras = coordenadas;

        uiCamaras = FXGL.texture("MonitorDeCamaras.png");
        btnIzq = FXGL.texture("flechaAmarillaIzq.png");
        btnDer = FXGL.texture("flechaAmarillaDer.png");
        btnIzq.setOnMouseClicked(e -> cambiarCamara(-1));
        btnDer.setOnMouseClicked(e -> cambiarCamara(1));
    }

    public void intentarAbrirCamaras(Entity jugador, boolean esImpostor) {
        if (AppPrincipal.sabotajeActivo) return;

        if (!esImpostor && jugador != null) {
            double distancia = jugador.getPosition().distance(ubicacionMesaCamaras);

            if (distancia < 50 || camarasAbiertas) {
                alternarCamaras(jugador);
            } else {
                System.out.println("Estás muy lejos de las cámaras para usarlas.");
            }
        }
    }

    public void alternarCamaras(Entity jugador) {
        com.almasb.fxgl.app.scene.Viewport viewport = FXGL.getGameScene().getViewport();

        if (camarasAbiertas) {
            FXGL.removeUINode(uiCamaras);
            FXGL.removeUINode(btnIzq);
            FXGL.removeUINode(btnDer);
            camarasAbiertas = false;

            if (AppPrincipal.oscuridad != null) AppPrincipal.oscuridad.setVisible(true);

            if (jugador.hasComponent(TripulanteComponent.class)) {
                jugador.getComponent(TripulanteComponent.class).getBarraTareasUI().setVisible(true);
                jugador.getComponent(TripulanteComponent.class).getContenedorTareas().setVisible(true);
            }
            viewport.setZoom(2.5);
            viewport.bindToEntity(jugador, FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0);

        } else {
            FXGL.addUINode(uiCamaras, 0, 0);
            FXGL.addUINode(btnIzq, -10, 390);
            FXGL.addUINode(btnDer, 590, 390);
            camarasAbiertas = true;

            if (AppPrincipal.oscuridad != null) AppPrincipal.oscuridad.setVisible(false);

            // Aquí ocultamos ambas cosas de la UI
            if (jugador.hasComponent(TripulanteComponent.class)) {
                jugador.getComponent(TripulanteComponent.class).getBarraTareasUI().setVisible(false);
                jugador.getComponent(TripulanteComponent.class).getContenedorTareas().setVisible(false);
            }

            // ¡No olvides esto para que el personaje no se resbale!
            if (jugador != null && jugador.hasComponent(com.almasb.fxgl.physics.PhysicsComponent.class)) {
                jugador.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityX(0);
                jugador.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityY(0);
            }

            viewport.unbind();
            viewport.setZoom(1.5);
            indiceCamaraActual = 0;
            actualizarVistaCamara(viewport);
        }
    }

    private void cambiarCamara(int direccion) {
        indiceCamaraActual += direccion;
        if (indiceCamaraActual < 0) {
            indiceCamaraActual = coordenadasCamaras.length - 1;
        } else if (indiceCamaraActual >= coordenadasCamaras.length) {
            indiceCamaraActual = 0;
        }
        actualizarVistaCamara(FXGL.getGameScene().getViewport());
    }

    private void actualizarVistaCamara(Viewport viewport) {
        Point2D coord = coordenadasCamaras[indiceCamaraActual];
        double anchoVisible = FXGL.getAppWidth() / 1.5;
        double altoVisible = FXGL.getAppHeight() / 1.5;
        viewport.setX(coord.getX() - (anchoVisible / 2));
        viewport.setY(coord.getY() - (altoVisible / 2));
    }

    public void forzarCierre(Entity jugador) {
        if (camarasAbiertas) {
            alternarCamaras(jugador);
        }
    }

    public boolean isCamarasAbiertas() { return camarasAbiertas; }
    public Point2D getUbicacionMesaCamaras() { return ubicacionMesaCamaras; }
}