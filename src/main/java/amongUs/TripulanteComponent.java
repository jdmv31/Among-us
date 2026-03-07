package main.java.amongUs;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
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
            com.almasb.fxgl.texture.AnimationChannel channel = tarea.getCanalAnimacion();
            double frameW = channel.getFrameWidth(0);
            double frameH = channel.getFrameHeight(0);
            double duracionTarea = tarea.getDuracionSegundos(); // Obtenemos el tiempo dinámicamente

            javafx.scene.image.ImageView animacionUI = new javafx.scene.image.ImageView(channel.getImage());
            animacionUI.setViewport(new javafx.geometry.Rectangle2D(0, 0, frameW, frameH));
            animacionUI.setTranslateX(tarea.getPosicionBoton().getX());
            animacionUI.setTranslateY(tarea.getPosicionBoton().getY());

            javafx.geometry.Rectangle2D limites = tarea.getHitboxClic();
            javafx.scene.control.Button hitboxUI = new javafx.scene.control.Button();
            hitboxUI.setPrefSize(limites.getWidth(), limites.getHeight());
            hitboxUI.setTranslateX(limites.getMinX());
            hitboxUI.setTranslateY(limites.getMinY());
            hitboxUI.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

            javafx.scene.text.Text btnCerrar = new javafx.scene.text.Text("X");
            btnCerrar.setFill(Color.RED);
            btnCerrar.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 40));
            btnCerrar.setTranslateX(fondo.getWidth() - 40);
            btnCerrar.setTranslateY(40);
            btnCerrar.setOnMouseClicked(e -> cerrarMinijuego());
            contenedorMinijuego.getChildren().addAll(fondo, animacionUI, hitboxUI, btnCerrar);
            contenedorMinijuego.setTranslateX((FXGL.getAppWidth() / 2.0) - (fondo.getWidth() / 2.0));
            contenedorMinijuego.setTranslateY((FXGL.getAppHeight() / 2.0) - (fondo.getHeight() / 2.0));

            hitboxUI.setOnAction(e -> {
                hitboxUI.setDisable(true);
                contenedorMinijuego.getChildren().remove(fondo);
                contenedorMinijuego.getChildren().remove(btnCerrar);
                contenedorMinijuego.getChildren().remove(hitboxUI);

                int totalFrames = (int) (channel.getImage().getWidth() / frameW);
                double duracionPorFrame = duracionTarea / Math.max(1, totalFrames);

                java.util.concurrent.atomic.AtomicInteger frameActual = new java.util.concurrent.atomic.AtomicInteger(0);

                TimerAction timerAnimacion = FXGL.getGameTimer().runAtInterval(() -> {
                    int idx = frameActual.getAndIncrement();
                    if (idx < totalFrames) {
                        animacionUI.setViewport(new javafx.geometry.Rectangle2D(idx * frameW, 0, frameW, frameH));
                    }
                }, Duration.seconds(duracionPorFrame));

                FXGL.getGameTimer().runOnceAfter(() -> {
                    if (timerAnimacion != null) timerAnimacion.expire();
                    contenedorMinijuego.getChildren().remove(animacionUI);
                    Texture imagenFinal = FXGL.texture(tarea.getTexturaFinal());
                    imagenFinal.setTranslateX(tarea.getPosicionBoton().getX());
                    imagenFinal.setTranslateY(tarea.getPosicionBoton().getY());
                    contenedorMinijuego.getChildren().add(imagenFinal);
                    FXGL.getGameTimer().runOnceAfter(() -> {
                        completarTarea(indice);
                        cerrarMinijuego();
                    }, Duration.seconds(1));

                }, Duration.seconds(duracionTarea));
            });

            FXGL.addUINode(contenedorMinijuego);

        } catch (Exception e) {
            System.err.println("Error al cargar la tarea: " + e.getMessage());
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