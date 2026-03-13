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

/**
 * Componente exclusivo de los jugadores que son tripulantes
 * Controla toda la logica relacionada con las tareas: asignarlas al inicio,
 * detectar cuando el jugador esta cerca de una, abrir la interfaz del minijuego
 * y actualizar la barra de progreso general
 * @author Angelo Martini
 * */
public class TripulanteComponent extends Component {
    private Tarea[] tareasAsignadas;
    private int tareasCompletadas = 0;
    private Texture barraTareasUI;
    private Pane contenedorMinijuego;

    private int indiceTareaCercana = -1;
    private boolean enMinijuego = false;
    private Texture panelMinijuegoActual;
    private com.almasb.fxgl.texture.AnimatedTexture botonAnimadoActual;

    private TimerAction timerAnimacion;
    private TimerAction timerFinalizacion;
    private TimerAction timerCierre;
    private javafx.scene.layout.VBox listaTareasUI;
    private javafx.scene.layout.StackPane contenedorTareas;

    /**
     * Recibe la lista de misiones para este jugador al arrancar la partida y dibuja la interfaz
     * en la esquina superior izquierda la barra de progreso y lista de texto
     * @param tareas arreglo de objetos {@link Tarea} que el jugador debe completar
     * */
    public void asignarTareas(Tarea[] tareas) {
        this.tareasAsignadas = tareas;
        this.tareasCompletadas = 0;

        // Crear la barra de progreso visual (arranca vacia)

        barraTareasUI = FXGL.texture("barra_0.png");
        barraTareasUI.setFitWidth(250);
        barraTareasUI.setPreserveRatio(true);
        barraTareasUI.setTranslateX(10);
        barraTareasUI.setTranslateY(10);
        FXGL.addUINode(barraTareasUI);

        // Contenedor vertical para apilar los nombres de las tareas

        listaTareasUI = new javafx.scene.layout.VBox(5);

        for (Tarea t : tareas) {
            javafx.scene.text.Text textoTarea = new javafx.scene.text.Text(t.getNombre());
            textoTarea.setFill(Color.rgb(255, 255, 255, 0.6));
            textoTarea.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 13));
            listaTareasUI.getChildren().add(textoTarea);
        }

        // Crear el recuadro negro semitransparente que va de fondo a las letras
        double altoFondo = Math.max(30, tareas.length * 20 + 10);
        Rectangle fondoTareas = new Rectangle(210, altoFondo);
        fondoTareas.setFill(Color.rgb(100, 100, 100, 0.5));
        fondoTareas.setArcWidth(10);
        fondoTareas.setArcHeight(10);

        // Agrupar el fondo oscuro y la lista de textos para moverlos como un solo bloque
        contenedorTareas = new javafx.scene.layout.StackPane(fondoTareas, listaTareasUI);
        contenedorTareas.setTranslateX(18);
        contenedorTareas.setTranslateY(120);
        javafx.scene.layout.StackPane.setAlignment(listaTareasUI, javafx.geometry.Pos.TOP_LEFT);
        listaTareasUI.setPadding(new javafx.geometry.Insets(5, 5, 5, 10));

        FXGL.addUINode(contenedorTareas);
    }

    /**
     * Fuerza el estado del jugador para indicar si esta o no usando un panel
     * @param estado true si tiene un minijuego abierto, false si esta libre
     * */
    public void setEnMinijuego(boolean estado){
        this.enMinijuego = estado;
    }

    /**
     * Ciclo principal del componente que se ejecuta constantemente
     * Si el jugador muere o abre un panel, deja de buscar tareas
     * Si esta caminando, calcula la distancia a todas sus tareas pendientes para avisar cuando este cerca de una
     * @param tpf tiempo transcurrido desde el ultimo frame
     * */
    @Override
    public void onUpdate(double tpf) {

        // Un fantasma no puede hacer minijuegos, asi que los cerramos de golpe si lo matan mientras hacia uno

        if (AppPrincipal.estoyMuerto) {
            if (enMinijuego) {
                cerrarMinijuego();
            }
            indiceTareaCercana = -1;
            return;
        }

        if (enMinijuego) {
            return;
        }

        if (tareasAsignadas == null) return;

        indiceTareaCercana = -1;

        // Buscamos la primera tarea que no este lista y que este a menos de 30 pixeles

        for (int i = 0; i < tareasAsignadas.length; i++) {
            if (!tareasAsignadas[i].tareaCompletada() && entity.getPosition().distance(tareasAsignadas[i].getUbicacion()) < 30) {
                indiceTareaCercana = i;
                break;
            }
        }
    }

    /**
     * Es llamado por los controles del jugador cuando presiona la tecla de accion
     * Verifica que el jugador este vivo y cerca de un panel antes de abrir la interfaz
     * */
    public void intentarUsarTarea() {
        if (AppPrincipal.estoyMuerto) return;

        if (indiceTareaCercana != -1 && !enMinijuego) {
            abrirMinijuego(indiceTareaCercana);
        }
    }

    /**
     * Construye y muestra la pantalla interactiva de la mision seleccionada
     * Frena en seco al jugador y distingue si es la tarea especial del reactor o una tarea normal de animacion
     * @param indice la posicion exacta de la mision en el arreglo de tareas
     * */
    private void abrirMinijuego(int indice) {
        enMinijuego = true;
        Tarea tarea = tareasAsignadas[indice];
        contenedorMinijuego = new Pane();

        // Le quitamos el impulso fisico para que no resbale mientras hace la tarea

        if (entity.hasComponent(PhysicsComponent.class)) {
            entity.getComponent(PhysicsComponent.class).setVelocityX(0);
            entity.getComponent(PhysicsComponent.class).setVelocityY(0);
        }

        try {
            Texture fondo = FXGL.texture(tarea.getTexturaFondo());

            // Boton de salida manual en caso de emergencia

            javafx.scene.text.Text btnCerrar = new javafx.scene.text.Text("X");
            btnCerrar.setFill(Color.RED);
            btnCerrar.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 40));
            btnCerrar.setTranslateX(fondo.getWidth() - 40);
            btnCerrar.setTranslateY(40);
            btnCerrar.setOnMouseClicked(e -> cerrarMinijuego());
            btnCerrar.setStyle("-fx-cursor: hand;");

            // Logica especial si la tarea es del tipo teclado (Reactor)

            if (tarea instanceof TareaReactor) {
                TareaReactor tareaReactor = (TareaReactor) tarea;
                fondo.setOnMouseClicked(e -> {
                    int resultado = tareaReactor.intentarPulsarTecla(e.getX(), e.getY());

                    if (resultado == 1) { // Gano el minijuego

                        fondo.setImage(FXGL.image(tareaReactor.getTexturaFinal()));
                        timerCierre = FXGL.getGameTimer().runOnceAfter(() -> {
                            completarTarea(indice);
                            cerrarMinijuego();
                        }, Duration.seconds(1));

                    } else if (resultado == -1) { // Le erro al numero
                        fondo.setImage(FXGL.image(tareaReactor.getTexturaError()));
                        FXGL.getGameTimer().runOnceAfter(() -> {
                            if (enMinijuego) {
                                fondo.setImage(FXGL.image(tareaReactor.getTexturaFondo()));
                            }
                        }, Duration.seconds(0.5));
                    }
                });

                contenedorMinijuego.getChildren().addAll(fondo, btnCerrar);

            }
            // Logica para las tareas normales (animacion por tiempo)

            else {
                com.almasb.fxgl.texture.AnimationChannel channel = tarea.getCanalAnimacion();
                double frameW = channel.getFrameWidth(0);
                double frameH = channel.getFrameHeight(0);
                double duracionTarea = tarea.getDuracionSegundos();
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

                // Al presionar el boton de la UI, arranca la secuencia de imagenes

                hitboxUI.setOnAction(e -> {
                    hitboxUI.setDisable(true);

                    int totalFrames = (int) (channel.getImage().getWidth() / frameW);
                    double duracionPorFrame = duracionTarea / Math.max(1, totalFrames);

                    java.util.concurrent.atomic.AtomicInteger frameActual = new java.util.concurrent.atomic.AtomicInteger(0);

                    timerAnimacion = FXGL.getGameTimer().runAtInterval(() -> {
                        int idx = frameActual.getAndIncrement();
                        if (idx < totalFrames) {
                            animacionUI.setViewport(new javafx.geometry.Rectangle2D(idx * frameW, 0, frameW, frameH));
                        }
                    }, Duration.seconds(duracionPorFrame));

                    timerFinalizacion = FXGL.getGameTimer().runOnceAfter(() -> {
                        if (timerAnimacion != null) timerAnimacion.expire();
                        contenedorMinijuego.getChildren().remove(animacionUI);
                        Texture imagenFinal = FXGL.texture(tarea.getTexturaFinal());
                        imagenFinal.setTranslateX(tarea.getPosicionBoton().getX());
                        imagenFinal.setTranslateY(tarea.getPosicionBoton().getY());
                        contenedorMinijuego.getChildren().add(imagenFinal);

                        timerCierre = FXGL.getGameTimer().runOnceAfter(() -> {
                            completarTarea(indice);
                            cerrarMinijuego();
                        }, Duration.seconds(1));

                    }, Duration.seconds(duracionTarea));
                });

                contenedorMinijuego.getChildren().addAll(fondo, animacionUI, hitboxUI, btnCerrar);
            }

            // Centramos el panel gigante en medio de la pantalla del juego

            contenedorMinijuego.setTranslateX((FXGL.getAppWidth() / 2.0) - (fondo.getWidth() / 2.0));
            contenedorMinijuego.setTranslateY((FXGL.getAppHeight() / 2.0) - (fondo.getHeight() / 2.0));
            FXGL.addUINode(contenedorMinijuego);

        } catch (Exception e) {
            System.err.println("Error al cargar la tarea: " + e.getMessage());
            e.printStackTrace();
            enMinijuego = false; // Prevencion por si crashea, no dejar al jugador bloqueado
        }
    }

    /**
     * Marca la mision como finalizada en el arreglo interno, actualiza la imagen de la barra verde
     * y tacha el texto en la lista de la pantalla
     * @param indice posicion de la tarea que acaba de terminar el jugador
     * */
    private void completarTarea(int indice) {
        if (!tareasAsignadas[indice].tareaCompletada()) {
            tareasAsignadas[indice].completar();
            tareasCompletadas++;

            if (barraTareasUI != null) {
                barraTareasUI.setImage(FXGL.image("barra_" + tareasCompletadas + ".png"));
            }

            // Cambiamos el estilo de la letra a verde y le pasamos una linea por encima

            if (listaTareasUI != null && indice < listaTareasUI.getChildren().size()) {
                javafx.scene.text.Text texto = (javafx.scene.text.Text) listaTareasUI.getChildren().get(indice);
                texto.setFill(Color.LIMEGREEN);
                texto.setStrikethrough(true);
            }
        }
    }

    /**
     * @return true si el jugador tiene la pantalla tapada con un panel de tarea
     * */
    public boolean isEnMinijuego() {
        return enMinijuego;
    }

    /**
     * @return true si el jugador esta parado al lado de una tarea interactuable
     * */
    public boolean hayTareaCercana() {
        return indiceTareaCercana != -1;
    }

    /**
     * @return el objeto Texture que contiene la imagen de la barra de progreso general
     * */
    public Texture getBarraTareasUI() {
        return barraTareasUI;
    }

    /**
     * Detiene todos los contadores de tiempo pendientes y limpia la pantalla quitando los graficos del minijuego
     * Restaura la libertad de movimiento del jugador
     * */
    private void cerrarMinijuego() {
        if (timerAnimacion != null) { timerAnimacion.expire(); timerAnimacion = null; }
        if (timerFinalizacion != null) { timerFinalizacion.expire(); timerFinalizacion = null; }
        if (timerCierre != null) { timerCierre.expire(); timerCierre = null; }

        if (contenedorMinijuego != null) {
            FXGL.removeUINode(contenedorMinijuego);
            contenedorMinijuego = null;
        }
        enMinijuego = false;
    }

    /**
     * @return el nodo grafico que agrupa tanto el fondo oscuro como los nombres de las tareas
     * */
    public javafx.scene.layout.StackPane getContenedorTareas() {
        return contenedorTareas;
    }
}