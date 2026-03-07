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

    // nicole: esto representa las coordenadas de las camaras en el mapa
    private final Point2D[] coordenadasCamaras = {
            new Point2D(-30, 500),  // Cámara del pasillo de abajo
            new Point2D(-50, 150),  // Cámara de arriba, pasillo bomberos
            new Point2D(450, 300),  // Cámara pasillo de comedor a laboratorio
            new Point2D(500, 500)   // Cámara de la cantina
    };

    public void inicializar(Point2D ubicacionMesa) {
        this.ubicacionMesaCamaras = ubicacionMesa;

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
        Viewport viewport = FXGL.getGameScene().getViewport();

        if (camarasAbiertas) {
            FXGL.removeUINode(uiCamaras);
            FXGL.removeUINode(btnIzq);
            FXGL.removeUINode(btnDer);
            camarasAbiertas = false;

            if (AppPrincipal.oscuridad != null) AppPrincipal.oscuridad.setVisible(true);

            if (AppPrincipal.barraTareasUI != null) AppPrincipal.barraTareasUI.setVisible(true);
            viewport.setZoom(2.5);
            viewport.bindToEntity(jugador, FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0);

        } else {
            FXGL.addUINode(uiCamaras, 0, 0);
            FXGL.addUINode(btnIzq, -10, 390);
            FXGL.addUINode(btnDer, 590, 390);
            camarasAbiertas = true;

            if (AppPrincipal.oscuridad != null) AppPrincipal.oscuridad.setVisible(false);
            if (AppPrincipal.barraTareasUI != null) AppPrincipal.barraTareasUI.setVisible(false);
            if (jugador != null && jugador.hasComponent(PhysicsComponent.class)) {
                jugador.getComponent(PhysicsComponent.class).setVelocityX(0);
                jugador.getComponent(PhysicsComponent.class).setVelocityY(0);
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
        viewport.setX(coord.getX());
        viewport.setY(coord.getY());
    }

    public void forzarCierre(Entity jugador) {
        if (camarasAbiertas) {
            alternarCamaras(jugador);
        }
    }

    public boolean isCamarasAbiertas() { return camarasAbiertas; }
    public Point2D getUbicacionMesaCamaras() { return ubicacionMesaCamaras; }
}