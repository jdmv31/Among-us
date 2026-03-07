package main.java.amongUs;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import com.almasb.fxgl.time.TimerAction;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class TripulanteComponent extends Component {
    private Tarea[] tareasAsignadas;
    private int tareasCompletadas = 0;
    private Texture barraTareasUI;
    private Pane contenedorMinijuego;

    private int indiceTareaCercana = -1;
    private boolean enMinijuego = false;
    private Texture panelMinijuegoActual;
    private com.almasb.fxgl.texture.AnimatedTexture botonAnimadoActual;

    public void asignarTareas(Tarea[] tareas) {
        this.tareasAsignadas = tareas;
        this.tareasCompletadas = 0;

        barraTareasUI = FXGL.texture("barra_0.png");
        barraTareasUI.setFitWidth(250);
        barraTareasUI.setPreserveRatio(true);
        barraTareasUI.setTranslateX(10);
        barraTareasUI.setTranslateY(10);
        FXGL.addUINode(barraTareasUI);
    }

    @Override
    public void onUpdate(double tpf) {
        if (enMinijuego) {
            return;
        }

        if (tareasAsignadas == null || enMinijuego) return;

        indiceTareaCercana = -1;

        for (int i = 0; i < tareasAsignadas.length; i++) {
            if (!tareasAsignadas[i].tareaCompletada() && entity.getPosition().distance(tareasAsignadas[i].getUbicacion()) < 50) {
                indiceTareaCercana = i;
                break;
            }
        }
    }

    public void intentarUsarTarea() {
        if (indiceTareaCercana != -1 && !enMinijuego) {
            abrirMinijuego(indiceTareaCercana);
        }
    }

    private void abrirMinijuego(int indice) {
        enMinijuego = true;
        Tarea tarea = tareasAsignadas[indice];
        contenedorMinijuego = new Pane();

        if (entity.hasComponent(PhysicsComponent.class)) {
            entity.getComponent(PhysicsComponent.class).setVelocityX(0);
            entity.getComponent(PhysicsComponent.class).setVelocityY(0);
        }

        try {
            Texture fondo = FXGL.texture(tarea.getTexturaFondo());
            botonAnimadoActual = new com.almasb.fxgl.texture.AnimatedTexture(tarea.getCanalAnimacion());
            botonAnimadoActual.setTranslateX(tarea.getPosicionBoton().getX());
            botonAnimadoActual.setTranslateY(tarea.getPosicionBoton().getY());
            botonAnimadoActual.stop();

            javafx.geometry.Rectangle2D limites = tarea.getHitboxClic();
            Rectangle hitboxFuego = new Rectangle(limites.getWidth(), limites.getHeight());
            hitboxFuego.setTranslateX(limites.getMinX());
            hitboxFuego.setTranslateY(limites.getMinY());
            hitboxFuego.setFill(Color.rgb(0, 0, 0, 0.01));
            hitboxFuego.setCursor(javafx.scene.Cursor.HAND);

            javafx.scene.text.Text btnCerrar = new javafx.scene.text.Text("X");
            btnCerrar.setFill(Color.RED);
            btnCerrar.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 40));
            btnCerrar.setTranslateX(fondo.getWidth() - 40);
            btnCerrar.setTranslateY(40);
            btnCerrar.setOnMouseClicked(e -> cerrarMinijuego());

            contenedorMinijuego.getChildren().addAll(fondo, botonAnimadoActual, hitboxFuego, btnCerrar);
            contenedorMinijuego.setTranslateX((FXGL.getAppWidth() / 2.0) - (fondo.getWidth() / 2.0));
            contenedorMinijuego.setTranslateY((FXGL.getAppHeight() / 2.0) - (fondo.getHeight() / 2.0));

            hitboxFuego.setOnMousePressed(e -> {
                hitboxFuego.setDisable(true);
                contenedorMinijuego.getChildren().remove(btnCerrar);
                contenedorMinijuego.getChildren().remove(fondo);

                botonAnimadoActual.setOnCycleFinished(() -> {
                    botonAnimadoActual.stop();

                    Texture extintorFinal = FXGL.texture("extintor_final.png");
                    extintorFinal.setTranslateX(botonAnimadoActual.getTranslateX());
                    extintorFinal.setTranslateY(botonAnimadoActual.getTranslateY());

                    contenedorMinijuego.getChildren().remove(botonAnimadoActual);
                    contenedorMinijuego.getChildren().add(extintorFinal);

                    FXGL.getGameTimer().runOnceAfter(() -> {
                        completarTarea(indice);
                        cerrarMinijuego();
                    }, Duration.seconds(2));
                });

                botonAnimadoActual.playAnimationChannel(tarea.getCanalAnimacion());
            });

            FXGL.addUINode(contenedorMinijuego);

        } catch (Exception e) {
            System.err.println("Error en minijuego: " + e.getMessage());
            enMinijuego = false;
        }
    }

    private void completarTarea(int indice) {
        if (!tareasAsignadas[indice].tareaCompletada()) {
            tareasAsignadas[indice].completar();
            tareasCompletadas++;

            if (barraTareasUI != null) {
                barraTareasUI.setImage(FXGL.image("barra_" + tareasCompletadas + ".png"));
            }

            if (tareasCompletadas >= tareasAsignadas.length) {
                System.out.println("¡Todas las tareas listas! Enviar aviso al servidor.");
            }
        }
    }

    public boolean isEnMinijuego() {
        return enMinijuego;
    }

    public boolean hayTareaCercana() {
        return indiceTareaCercana != -1;
    }

    public Texture getBarraTareasUI() {
        return barraTareasUI;
    }

    private void cerrarMinijuego() {
        if (contenedorMinijuego != null) {
            FXGL.removeUINode(contenedorMinijuego);
            contenedorMinijuego = null;
        }
        enMinijuego = false;
    }
}