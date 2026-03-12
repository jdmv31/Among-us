package main.java.amongUs;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.almasb.fxgl.texture.Texture;

/**
 * clase principal del juego que hereda de {@link GameApplication} del motor FXGL
 * es el nucleo del juego, se encarga de administrar el ciclo de vida del juego, renderiza los graficos,gestiona las
 * entradas por teclado, controla las colisiones y coordina los eventos de la partida
 * @author Josue Medina
 */

public class AppPrincipal extends GameApplication {
    /** representa al jugador local*/
    public static Entity jugador;
    /** representa la conexion de red del jugador local con el servidor */
    public static Cliente miCliente;
    /** almacena al resto de jugadores en la partida, se vincula su nombre con su entidad visual*/
    public static Map<String, Entity> otrosJugadores = new HashMap<>();
    public static Texture botonAccion;
    public static boolean accionDisponible = false;
    /** indica si el jugador tiene el rol de impostor*/
    public static boolean esImpostor = false;
    public static javafx.scene.shape.Rectangle oscuridad;
    public static Texture botonMatar;
    public static Texture botonReportar;
    public static javafx.scene.text.Text textoCooldown;
    public static boolean estoyMuerto = false;
    public static boolean peticionSabotaje = false;
    public static boolean sabotajeActivo = false;
    public static double tiempoSabotaje = 0;
    public static ConfiguracionMapa mapaActual;
    public static List<NodoAlcantarilla> redAlcantarillas = new java.util.ArrayList<>();
    public static SistemaCamaras sistemaCamaras = new SistemaCamaras();
    public static Tarea[] tareasAsignadas;
    public static int tareasCompletadas = 0;
    public static Texture barraTareasUI;
    public static int indiceTareaCercana = -1;
    public static boolean enMinijuego = false;
    public static Texture panelMinijuegoActual;
    public static boolean cercaDeBotonEmergencia = false;

    /**
     * configura los controles del teclado y sus respectivas cciones
     * define las teclas de movimiento (W, A, S, D) y diversas interacciones como abrir camaras (C)
     * usar alcantarillas (Espacio) matar(Q) o reportar (R)
     */
    @Override
    protected void initInput() {
        int velocidadFisica = 150;

        FXGL.getInput().addAction(new UserAction("Mover Arriba") {
            @Override
            protected void onAction() {
                if (jugador == null) return;

                if (jugador.hasComponent(TripulanteComponent.class) && jugador.getComponent(TripulanteComponent.class).isEnMinijuego()) return;

                boolean enAlcantarilla = jugador.hasComponent(ImpostorComponent.class) && jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla();

                if (!sistemaCamaras.isCamarasAbiertas() && !enAlcantarilla) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(-velocidadFisica);
                    if (!estoyMuerto) enviarCoordenadas();
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador == null) return;

                boolean enAlcantarilla = jugador.hasComponent(ImpostorComponent.class) && jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla();

                if (!enAlcantarilla) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(0);
                    if (!estoyMuerto) enviarCoordenadas();
                }
            }
        }, KeyCode.W);

        FXGL.getInput().addAction(new UserAction("Mover Abajo") {
            @Override
            protected void onAction() {
                if (jugador == null) return;

                if (jugador.hasComponent(TripulanteComponent.class) && jugador.getComponent(TripulanteComponent.class).isEnMinijuego()) return;

                boolean enAlcantarilla = jugador.hasComponent(ImpostorComponent.class) && jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla();

                if (!sistemaCamaras.isCamarasAbiertas() && !enAlcantarilla) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(velocidadFisica);
                    if (!estoyMuerto) enviarCoordenadas();
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador == null) return;

                boolean enAlcantarilla = jugador.hasComponent(ImpostorComponent.class) && jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla();

                if (!enAlcantarilla) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(0);
                    if (!estoyMuerto) enviarCoordenadas();
                }
            }
        }, KeyCode.S);

        FXGL.getInput().addAction(new UserAction("Mover Izquierda") {
            @Override
            protected void onAction() {
                if (jugador == null) return;

                if (jugador.hasComponent(TripulanteComponent.class) && jugador.getComponent(TripulanteComponent.class).isEnMinijuego()) return;

                boolean enAlcantarilla = jugador.hasComponent(ImpostorComponent.class) && jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla();

                if (!sistemaCamaras.isCamarasAbiertas() && !enAlcantarilla) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(-velocidadFisica);
                    if (!estoyMuerto) enviarCoordenadas();
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador == null) return;

                boolean enAlcantarilla = jugador.hasComponent(ImpostorComponent.class) && jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla();

                if (!enAlcantarilla) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(0);
                    if (!estoyMuerto) enviarCoordenadas();
                }
            }
        }, KeyCode.A);

        FXGL.getInput().addAction(new UserAction("Mover Derecha") {
            @Override
            protected void onAction() {
                if (jugador == null) return;

                if (jugador.hasComponent(TripulanteComponent.class) && jugador.getComponent(TripulanteComponent.class).isEnMinijuego()) return;

                boolean enAlcantarilla = jugador.hasComponent(ImpostorComponent.class) && jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla();

                if (!sistemaCamaras.isCamarasAbiertas() && !enAlcantarilla) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(velocidadFisica);
                    if (!estoyMuerto) enviarCoordenadas();
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador == null) return;

                boolean enAlcantarilla = jugador.hasComponent(ImpostorComponent.class) && jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla();

                if (!enAlcantarilla) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(0);
                    if (!estoyMuerto) enviarCoordenadas();
                }
            }
        }, KeyCode.D);

        FXGL.getInput().addAction(new UserAction("Abrir Camaras") {
            @Override
            protected void onActionBegin() {
                if (!estoyMuerto) {
                    sistemaCamaras.intentarAbrirCamaras(jugador, esImpostor);
                }
            }
        }, KeyCode.C);

        FXGL.getInput().addAction(new UserAction("Usar Alcantarilla") {
            @Override
            protected void onActionBegin() {
                if (esImpostor && jugador != null) {
                    jugador.getComponent(ImpostorComponent.class).alternarAlcantarilla();
                }
            }
        }, KeyCode.SPACE);

        FXGL.getInput().addAction(new UserAction("Vent Izquierda") {
            @Override
            protected void onActionBegin() {
                if (esImpostor) jugador.getComponent(ImpostorComponent.class).viajarAlcantarilla("IZQ");
            }
        }, KeyCode.LEFT);

        FXGL.getInput().addAction(new UserAction("Vent Derecha") {
            @Override
            protected void onActionBegin() {
                if (esImpostor) jugador.getComponent(ImpostorComponent.class).viajarAlcantarilla("DER");
            }
        }, KeyCode.RIGHT);

        FXGL.getInput().addAction(new UserAction("Vent Arriba") {
            @Override
            protected void onActionBegin() {
                if (esImpostor) jugador.getComponent(ImpostorComponent.class).viajarAlcantarilla("ARRIBA");
            }
        }, KeyCode.UP);

        FXGL.getInput().addAction(new UserAction("Vent Abajo") {
            @Override
            protected void onActionBegin() {
                if (esImpostor) jugador.getComponent(ImpostorComponent.class).viajarAlcantarilla("ABAJO");
            }
        }, KeyCode.DOWN);

        FXGL.getInput().addAction(new UserAction("Matar Jugador") {
            @Override
            protected void onActionBegin() {
                if (esImpostor && jugador != null) {
                    jugador.getComponent(ImpostorComponent.class).intentarMatar();
                }
            }
        }, KeyCode.Q);

        FXGL.getInput().addAction(new UserAction("Reportar Cadaver") {
            @Override
            protected void onActionBegin() {
                if (jugador != null && jugador.hasComponent(ReporteComponent.class)) {
                    if (!estoyMuerto){
                        jugador.getComponent(ReporteComponent.class).intentarReportar();
                    }
                }
            }
        }, KeyCode.R);

    }

    /**
     * obtiene las coordenadas del personaje en tiempo real y las actualiza mediante un paquete al servidor, de esta forma
     * todos pueden visualizar el movimiento de los demas jugadores
     */
    private void enviarCoordenadas() {
        if (miCliente != null && miCliente.cliente != null && miCliente.cliente.isConnected()) {
            Movimiento mov = new Movimiento();
            mov.username = MenuController.nombreUsuario;
            mov.x = (int) jugador.getX();
            mov.y = (int) jugador.getY();
            miCliente.cliente.sendUDP(mov);
            System.out.println("Enviando posición -> X: " + mov.x + " Y: " + mov.y);
        } else {
            System.out.println("Error: Cliente desconectado");
        }
    }

    /**
     * inicializa las fisicas en el juego de parte del jugador para que no atraviese paredes u objetos
     */
    @Override
    protected void initPhysics(){
        FXGL.getPhysicsWorld().setGravity(0,0);
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.PARED) {});
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.OBJETO) {});
    }

    /**
     * establece los parametros basicos para la ventana del juego, siendo la resolucion, el titulo y el icono del juego
     * ademas que deshabilita el redimensionamiento de la ventana
     */
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Among Us UNEG");
        settings.setAppIcon("icono.png");
        settings.setManualResizeEnabled(false);
    }

    /**
     * inicaliza la interfaz de usuario al ejecutar el juego, carga el menu principal y sus respectivos controladores
     */
    @Override
    protected void initUI() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/ui/menuPrincipal.fxml")
            );
            loader.setController(new MenuController());
            Parent root = loader.load();
            FXGL.addUINode(root);

        } catch (Exception e) {
            System.err.println("Error al cargar la interfaz");
            e.printStackTrace();
        }
    }

    /**
     * inicializa los elementos principales del juego antes de empezar la partida, como cargar la musica del menu
     * y la fabrica de entidades en {@link Fabrica}
     */
    @Override
    protected void initGame(){
        FXGL.getGameWorld().addEntityFactory(new Fabrica());

        try {
            FXGL.getAudioPlayer().loopMusic(FXGL.getAssetLoader().loadMusic("musicaMenu.mp3"));
            FXGL.getSettings().setGlobalMusicVolume(0.4);
        } catch (Exception e) {
            System.err.println("Error cargando la música: " + e.getMessage());
        }
    }

    /**
     * reduce drasticamente el rango de vision al activarse un saboteo por parte de los impostores
     */
    public static void activarCorteElectrico() {
        sabotajeActivo = true;
        tiempoSabotaje = 15.0;

        if (sistemaCamaras.isCamarasAbiertas()) {
            FXGL.getInput().mockKeyPress(javafx.scene.input.KeyCode.C);
            FXGL.getInput().mockKeyRelease(javafx.scene.input.KeyCode.C);
        }

        if (!esImpostor && jugador != null && !estoyMuerto) {
            if (oscuridad != null) {
                javafx.scene.paint.RadialGradient gradienteSabotaje = new javafx.scene.paint.RadialGradient(
                        0, 0, 0.5, 0.5, 0.5, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                        new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.TRANSPARENT),
                        new javafx.scene.paint.Stop(0.04, javafx.scene.paint.Color.TRANSPARENT),
                        new javafx.scene.paint.Stop(0.12, javafx.scene.paint.Color.rgb(10, 10, 10, 0.98)),
                        new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.rgb(10, 10, 10, 1.0))
                );
                oscuridad.setFill(gradienteSabotaje);
            }

            if (sistemaCamaras.isCamarasAbiertas()) {
                sistemaCamaras.forzarCierre(jugador);
            }

        } else if (esImpostor) {
            if (oscuridad != null) {
                javafx.scene.paint.RadialGradient gradienteAlarma = new javafx.scene.paint.RadialGradient(
                        0, 0, 0.5, 0.5, 0.5, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                        new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.TRANSPARENT),
                        new javafx.scene.paint.Stop(0.15, javafx.scene.paint.Color.TRANSPARENT),
                        new javafx.scene.paint.Stop(0.45, javafx.scene.paint.Color.rgb(50, 10, 10, 0.6)), // Tono rojizo
                        new javafx.scene.paint.Stop(0.75, javafx.scene.paint.Color.rgb(50, 10, 10, 0.95)),
                        new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.rgb(20, 0, 0, 0.98))
                );
                oscuridad.setFill(gradienteAlarma);
            }
        }
        if (enMinijuego) {
            if (panelMinijuegoActual != null) {
                FXGL.removeUINode(panelMinijuegoActual);
                panelMinijuegoActual = null;
            }
            enMinijuego = false;
            if (jugador != null && jugador.hasComponent(TripulanteComponent.class)) {
                jugador.getComponent(TripulanteComponent.class).setEnMinijuego(false);
            }
        }
    }

    /**
     * aca se encuentra la logica central, se ejecuta continuamente en cada fotograma del juego
     * gestiona en tiempo real verificaciones como el temporizador de sabotajes y el de asesinato,
     * la actualizacion de capas de renderizado y evalua si el jugador esta cerca de tareas, cuerpos o botones para
     * realizar diversas acciones
     *
     * * @param tpf es el tiempo transcurrido en segundos desde la ultima actualizacion
     */
    @Override
    protected void onUpdate(double tpf) {
        if (peticionSabotaje) {
            peticionSabotaje = false;
            activarCorteElectrico();
        }

        if (sabotajeActivo) {
            tiempoSabotaje -= tpf;
            if (tiempoSabotaje <= 0) {
                sabotajeActivo = false;
            }
        }

        if (sabotajeActivo) {
            tiempoSabotaje -= tpf;
            if (tiempoSabotaje <= 0) {
                sabotajeActivo = false;
                if (oscuridad != null) {
                    javafx.scene.paint.RadialGradient gradienteNormal = new javafx.scene.paint.RadialGradient(
                            0, 0, 0.5, 0.5, 0.5, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                            new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.TRANSPARENT),
                            new javafx.scene.paint.Stop(0.15, javafx.scene.paint.Color.TRANSPARENT),
                            new javafx.scene.paint.Stop(0.45, javafx.scene.paint.Color.rgb(10, 10, 10, 0.6)),
                            new javafx.scene.paint.Stop(0.75, javafx.scene.paint.Color.rgb(10, 10, 10, 0.95)),
                            new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.rgb(10, 10, 10, 0.98))
                    );
                    oscuridad.setFill(gradienteNormal);
                }
            }
        }
        if (jugador != null && jugador.isActive()) {
            jugador.setZIndex((int) (jugador.getY() + (32 * 1.8)));

            if (botonAccion != null) {
                cercaDeBotonEmergencia = mapaActual.getPosicionBotonEmergencia() != null &&
                        jugador.getPosition().distance(mapaActual.getPosicionBotonEmergencia()) < 60;

                if (!esImpostor) {
                    boolean cercaDeCamaras = sistemaCamaras.getUbicacionMesaCamaras() != null &&
                            jugador.getPosition().distance(sistemaCamaras.getUbicacionMesaCamaras()) < 50;
                    if (estoyMuerto) {
                        cercaDeCamaras = false;
                        cercaDeBotonEmergencia = false;
                    }
                    TripulanteComponent tripComp = jugador.getComponent(TripulanteComponent.class);
                    boolean cercaDeTarea = tripComp != null && tripComp.hayTareaCercana();
                    boolean enMinijuego = tripComp != null && tripComp.isEnMinijuego();

                    if (cercaDeBotonEmergencia && !estoyMuerto && !sabotajeActivo) {
                        if (!accionDisponible || !botonAccion.getImage().getUrl().contains("accion.png")) {
                            botonAccion.setImage(FXGL.image("accion.png"));
                            accionDisponible = true;
                        }
                    } else if ((cercaDeCamaras || cercaDeTarea) && !sistemaCamaras.isCamarasAbiertas() && !enMinijuego) {
                        if (!accionDisponible || !botonAccion.getImage().getUrl().contains("accion.png")) {
                            botonAccion.setImage(FXGL.image("accion.png"));
                            accionDisponible = true;
                        }
                    } else {
                        if (accionDisponible) {
                            botonAccion.setImage(FXGL.image("accionNegada.png"));
                            accionDisponible = false;
                        }
                    }
                } else {
                    boolean cercaDeAlcantarilla = false;
                    ImpostorComponent impComp = jugador.getComponent(ImpostorComponent.class);

                    TripulanteComponent tripComp = jugador.getComponent(TripulanteComponent.class);
                    boolean cercaDeTarea = tripComp != null && tripComp.hayTareaCercana();
                    boolean enMinijuego = tripComp != null && tripComp.isEnMinijuego();
                    boolean cercaDeCamaras = sistemaCamaras.getUbicacionMesaCamaras() != null &&
                            jugador.getPosition().distance(sistemaCamaras.getUbicacionMesaCamaras()) < 50;

                    if (impComp.estaEnAlcantarilla()) {
                        cercaDeAlcantarilla = true;
                    } else {
                        for (NodoAlcantarilla nodo : redAlcantarillas) {
                            if (jugador.getPosition().distance(nodo.x, nodo.y) < 20.0) {
                                cercaDeAlcantarilla = true;
                                break;
                            }
                        }
                    }
                    if (cercaDeBotonEmergencia && !sabotajeActivo) {
                        if (!accionDisponible || !botonAccion.getImage().getUrl().contains("accion.png")) {
                            botonAccion.setImage(FXGL.image("accion.png"));
                            accionDisponible = true;
                        }
                    } else if (cercaDeAlcantarilla && !sistemaCamaras.isCamarasAbiertas()) {
                        if (!accionDisponible || !botonAccion.getImage().getUrl().contains("accion.png")) {
                            botonAccion.setImage(FXGL.image("accion.png"));
                            accionDisponible = true;
                        }
                    } else if (cercaDeTarea && !sistemaCamaras.isCamarasAbiertas() && !enMinijuego) {
                        if (!accionDisponible || !botonAccion.getImage().getUrl().contains("accion.png")) {
                            botonAccion.setImage(FXGL.image("accion.png"));
                            accionDisponible = true;
                        }
                    }else {
                        if (accionDisponible) {
                            botonAccion.setImage(FXGL.image("accionNegada.png"));
                            accionDisponible = false;
                        }
                    }
                }
            }
        }

        for (Entity otro : otrosJugadores.values()) {
            if (otro != null && otro.isActive()) {
                otro.setZIndex((int) (otro.getY() + (32 * 1.8)));
            }
        }
    }

    /**
     * inicaliza la partida una vez que todos los jugadores esten en la lobby, carga el mapa seleccionado y distribuye
     * a los jugadores en el spawn, configura la vision y carga las interfaces de usuario dependiendo el rol del jugador
     * @param nombreMapa es el nombre del archivo TMX que se utilizara
     */
    public static void empezarPartida(String nombreMapa) {
        if (nombreMapa.equals("mapa2.tmx")){
            mapaActual = new MapaCancha();
        }
        else if (nombreMapa.equals("mapa1.tmx")){
            mapaActual = new MapaBiblioteca();
        }

        try {
            FXGL.getGameScene().clearUINodes();
            FXGL.setLevelFromMap(mapaActual.getArchivoTMX());

            double[] limites = mapaActual.getLimitesCamara();
            com.almasb.fxgl.app.scene.Viewport viewport = FXGL.getGameScene().getViewport();
            viewport.setBounds((int)limites[0], (int)limites[1], (int)limites[2], (int)limites[3]);
            viewport.setZoom(2.5);

            redAlcantarillas = mapaActual.getRedAlcantarillas();
            sistemaCamaras.inicializar(mapaActual.getPosicionMesaCamaras(), mapaActual.getCoordenadasCamaras());
            Point2D spawnCentral = mapaActual.getPuntoAparicionCentral();

            otrosJugadores.clear();

            if (MenuController.estadoActual != null) {
                for (JugadorLobby j : MenuController.estadoActual.jugadores) {
                    double offsetX = (Math.random() * 40) - 20;
                    double offsetY = (Math.random() * 40) - 20;
                    SpawnData data = new SpawnData(spawnCentral.getX() + offsetX, spawnCentral.getY() + offsetY);
                    data.put("nombre", j.nombre);

                    Entity entidad = FXGL.spawn("jugador", data);

                    if (j.nombre.equals(MenuController.nombreUsuario)) {
                        jugador = entidad;
                        viewport.bindToEntity(jugador, FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0);
                    } else {
                        if (entidad.hasComponent(com.almasb.fxgl.physics.PhysicsComponent.class)) {
                            entidad.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class)
                                    .setBodyType(com.almasb.fxgl.physics.box2d.dynamics.BodyType.KINEMATIC);
                        }
                        otrosJugadores.put(j.nombre, entidad);
                    }
                }
            }

            miCliente = MenuController.cliente;
            oscuridad = new javafx.scene.shape.Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight());
            javafx.scene.paint.RadialGradient gradiente = new javafx.scene.paint.RadialGradient(
                    0, 0, 0.5, 0.5, 0.5, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                    new javafx.scene.paint.Stop(0, javafx.scene.paint.Color.TRANSPARENT),
                    new javafx.scene.paint.Stop(0.15, javafx.scene.paint.Color.TRANSPARENT),
                    new javafx.scene.paint.Stop(0.45, javafx.scene.paint.Color.rgb(10, 10, 10, 0.6)),
                    new javafx.scene.paint.Stop(0.75, javafx.scene.paint.Color.rgb(10, 10, 10, 0.95)),
                    new javafx.scene.paint.Stop(1, javafx.scene.paint.Color.rgb(10, 10, 10, 0.98))
            );
            oscuridad.setFill(gradiente);
            oscuridad.setMouseTransparent(true);
            FXGL.addUINode(oscuridad);

            botonAccion = FXGL.texture("accionNegada.png");
            double tamanoBoton = 90.0;
            botonAccion.setFitWidth(tamanoBoton);
            botonAccion.setFitHeight(tamanoBoton);
            double margen = 20.0;
            botonAccion.setTranslateX(FXGL.getAppWidth() - tamanoBoton - margen);
            botonAccion.setTranslateY(FXGL.getAppHeight() - tamanoBoton - margen);
            botonReportar = FXGL.texture("reportarNegado.png");
            botonReportar.setFitWidth(tamanoBoton);
            botonReportar.setFitHeight(tamanoBoton);

            if (esImpostor) {
                botonReportar.setTranslateX(FXGL.getAppWidth() - (tamanoBoton * 3) - (margen * 3));
                botonReportar.setTranslateY(FXGL.getAppHeight() - tamanoBoton - margen);
            } else {
                botonReportar.setTranslateX(FXGL.getAppWidth() - tamanoBoton - margen);
                botonReportar.setTranslateY(FXGL.getAppHeight() - (tamanoBoton * 2) - (margen * 2));
            }
            FXGL.addUINode(botonReportar);
            ReporteComponent reporteComp = new ReporteComponent();
            reporteComp.setBotonReportar(botonReportar);
            jugador.addComponent(reporteComp);

            botonReportar.setOnMouseClicked(e -> {
                jugador.getComponent(ReporteComponent.class).intentarReportar();
            });

            if (esImpostor) {
                botonMatar = FXGL.texture("matarNegado.png");
                botonMatar.setFitWidth(tamanoBoton);
                botonMatar.setFitHeight(tamanoBoton);
                botonMatar.setTranslateX(FXGL.getAppWidth() - tamanoBoton - margen);
                botonMatar.setTranslateY(FXGL.getAppHeight() - (tamanoBoton * 2) - (margen * 2));
                botonMatar.setOnMouseClicked(e -> jugador.getComponent(ImpostorComponent.class).intentarMatar());
                FXGL.addUINode(botonMatar);
                textoCooldown = new javafx.scene.text.Text("");
                textoCooldown.setFill(javafx.scene.paint.Color.RED);
                textoCooldown.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 35));
                textoCooldown.setTranslateX(botonMatar.getTranslateX() + (tamanoBoton / 2.5));
                textoCooldown.setTranslateY(botonMatar.getTranslateY() + (tamanoBoton / 1.5));
                FXGL.addUINode(textoCooldown);
                jugador.getComponent(ImpostorComponent.class).setUIAsesinato(botonMatar, textoCooldown);

                Texture botonSabotaje = FXGL.texture("sabotaje.png");
                botonSabotaje.setFitWidth(tamanoBoton);
                botonSabotaje.setFitHeight(tamanoBoton);
                botonSabotaje.setTranslateX(FXGL.getAppWidth() - (tamanoBoton * 2) - (margen * 2));
                botonSabotaje.setTranslateY(FXGL.getAppHeight() - tamanoBoton - margen);
                botonSabotaje.setOnMouseClicked(e -> jugador.getComponent(ImpostorComponent.class).intentarSabotaje());
                FXGL.addUINode(botonSabotaje);

                javafx.scene.text.Text textoCooldownSabotaje = new javafx.scene.text.Text("");
                textoCooldownSabotaje.setFill(javafx.scene.paint.Color.RED);
                textoCooldownSabotaje.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 35));
                textoCooldownSabotaje.setTranslateX(botonSabotaje.getTranslateX() + (tamanoBoton / 2.5));
                textoCooldownSabotaje.setTranslateY(botonSabotaje.getTranslateY() + (tamanoBoton / 1.5));
                FXGL.addUINode(textoCooldownSabotaje);

                jugador.getComponent(ImpostorComponent.class).setUISabotaje(botonSabotaje, textoCooldownSabotaje);
            }

            jugador.addComponent(new TripulanteComponent());
            Tarea[] tareasDelMapa = new Tarea[0];

            if (mapaActual instanceof MapaCancha) {
                tareasDelMapa = ((MapaCancha) mapaActual).obtenerTareas();
            }
            else if (mapaActual instanceof MapaBiblioteca) {
                tareasDelMapa = ((MapaBiblioteca) mapaActual).obtenerTareas();
            }
            List<Tarea> listaTareas = java.util.Arrays.asList(tareasDelMapa);
            Collections.shuffle(listaTareas);

            int cantidadTareas = Math.min(4, listaTareas.size());
            Tarea[] tareasSeleccionadas = new Tarea[cantidadTareas];
            for (int i = 0; i < cantidadTareas; i++) {
                tareasSeleccionadas[i] = listaTareas.get(i);
            }
            jugador.getComponent(TripulanteComponent.class).asignarTareas(tareasSeleccionadas);
            tareasCompletadas = 0;

            FXGL.addUINode(botonAccion);
            accionDisponible = false;

            botonAccion.setOnMouseClicked(e -> {
                if (accionDisponible) {
                    if (cercaDeBotonEmergencia && !sabotajeActivo && !estoyMuerto) {
                        System.out.println("Solicitando Reunión de Emergencia");
                        PeticionReunion peticion = new PeticionReunion();

                        if (miCliente != null && miCliente.cliente != null && miCliente.cliente.isConnected()) {
                            miCliente.cliente.sendTCP(peticion);
                        }
                    }
                    else if (esImpostor) {
                        boolean cercaDeAlcantarilla = false;
                        ImpostorComponent impComp = jugador.getComponent(ImpostorComponent.class);
                        if (impComp.estaEnAlcantarilla()) cercaDeAlcantarilla = true;
                        else {
                            for (NodoAlcantarilla nodo : redAlcantarillas) {
                                if (jugador.getPosition().distance(nodo.x, nodo.y) < 20.0) { cercaDeAlcantarilla = true; break; }
                            }
                        }
                        if (cercaDeAlcantarilla) {
                            FXGL.getInput().mockKeyPress(javafx.scene.input.KeyCode.SPACE);
                            FXGL.getInput().mockKeyRelease(javafx.scene.input.KeyCode.SPACE);
                        } else {
                            TripulanteComponent tripComp = jugador.getComponent(TripulanteComponent.class);
                            if (tripComp != null && tripComp.hayTareaCercana()) {
                                tripComp.intentarUsarTarea();
                            } else {
                                FXGL.getInput().mockKeyPress(javafx.scene.input.KeyCode.C);
                                FXGL.getInput().mockKeyRelease(javafx.scene.input.KeyCode.C);
                            }
                        }
                    } else {
                        TripulanteComponent tripComp = jugador.getComponent(TripulanteComponent.class);
                        if (tripComp.hayTareaCercana()) {
                            tripComp.intentarUsarTarea();
                        } else {
                            FXGL.getInput().mockKeyPress(javafx.scene.input.KeyCode.C);
                            FXGL.getInput().mockKeyRelease(javafx.scene.input.KeyCode.C);
                        }
                    }
                }
            });

            FXGL.getGameScene().getRoot().requestFocus();
            mostrarPantallaRol();
        } catch(Exception e) {
            System.err.println("Error cargando el mapa: " + e.getMessage());
        }
    }

    /**
     * carga en la interfaz de usuario el png respectivo tras solicitar una reunion de emergencia, es activada mediante el boton rojo
     * del centro del mapa
     * @param solicitante representa el username de la persona que uso el boton
     */
    public static void iniciarCinematicaEmergencia(String solicitante) {
        if (jugador != null && jugador.hasComponent(com.almasb.fxgl.physics.PhysicsComponent.class)) {
            jugador.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityX(0);
            jugador.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityY(0);
        }
        FXGL.getInput().setRegisterInput(false);
        com.almasb.fxgl.texture.Texture imagenEmergencia = FXGL.texture("reunion.png");
        imagenEmergencia.setTranslateX((FXGL.getAppWidth() / 2.0) - (imagenEmergencia.getWidth() / 2.0));
        imagenEmergencia.setTranslateY((FXGL.getAppHeight() / 2.0) - (imagenEmergencia.getHeight() / 2.0));

        javafx.scene.Group grupoCinematica = new javafx.scene.Group(imagenEmergencia);
        FXGL.addUINode(grupoCinematica);

        FXGL.getGameTimer().runOnceAfter(() -> {
            FXGL.removeUINode(grupoCinematica);
            cargarInterfazVotacion();
        }, javafx.util.Duration.seconds(3.5));
    }

    /**
     * carga en la interfaz de usuario el png respectivo tras encontrar un cuerpo dentro del mapa, puede activarse en cualquier punto del mapa
     * @param reportador contiene el username de la persona que encontro al cuerpo
     * @param cadaver contiene el username del jugador encontrado tieso
     */

    public static void iniciarCinematicaReporte(String reportador, String cadaver) {
        if (jugador != null && jugador.hasComponent(com.almasb.fxgl.physics.PhysicsComponent.class)) {
            jugador.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityX(0);
            jugador.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityY(0);
        }

        FXGL.getInput().setRegisterInput(false);
        com.almasb.fxgl.texture.Texture imagenCuerpo = FXGL.texture("cuerpo.png");
        imagenCuerpo.setTranslateX((FXGL.getAppWidth() / 2.0) - (imagenCuerpo.getWidth() / 2.0));
        imagenCuerpo.setTranslateY((FXGL.getAppHeight() / 2.0) - (imagenCuerpo.getHeight() / 2.0));

        javafx.scene.Group grupoCinematica = new javafx.scene.Group(imagenCuerpo);
        FXGL.addUINode(grupoCinematica);

        FXGL.getGameTimer().runOnceAfter(() -> {
            FXGL.removeUINode(grupoCinematica);
            cargarInterfazVotacion();
        }, javafx.util.Duration.seconds(3.5));
    }

    /**
     * cuando se cumple alguna condicion de victoria procede a cargarse una pantalla indicando los ganadores
     * @param fin contiene los datos del fin de partida
     */
    public static void mostrarPantallaFin(FinPartida fin) {
        FXGL.getInput().setRegisterInput(false);

        if (jugador != null && jugador.hasComponent(com.almasb.fxgl.physics.PhysicsComponent.class)) {
            jugador.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityX(0);
            jugador.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityY(0);
        }
        limpiarTodosLosJugadores();

        String imagenFin = "";
        if (fin.ganador.equals("TRIPULANTES")) {
            imagenFin = esImpostor ? "derrota_impostor.png" : "victoria_tripulante.png";
        } else { // Ganaron IMPOSTORES
            imagenFin = esImpostor ? "victoria_impostor.png" : "derrota_tripulante.png";
        }

        try {
            com.almasb.fxgl.texture.Texture texturaFin = FXGL.texture(imagenFin);
            texturaFin.setTranslateX((FXGL.getAppWidth() / 2.0) - (texturaFin.getWidth() / 2.0));
            texturaFin.setTranslateY((FXGL.getAppHeight() / 2.0) - (texturaFin.getHeight() / 2.0));

            javafx.scene.Group grupoFinal = new javafx.scene.Group(texturaFin);

            if (fin.nombresCSV != null && !fin.nombresCSV.isEmpty()) {
                String[] nombres = fin.nombresCSV.split(",");
                String[] colores = fin.coloresCSV.split(",");

                javafx.scene.layout.HBox contenedorSprites = new javafx.scene.layout.HBox(40);
                contenedorSprites.setAlignment(javafx.geometry.Pos.CENTER);

                for (int i = 0; i < nombres.length; i++) {
                    if (nombres[i].isEmpty()) continue;

                    String colorStr = (i < colores.length && !colores[i].isEmpty()) ? colores[i] : "rojo";

                    try {
                        com.almasb.fxgl.texture.Texture sprite = FXGL.texture("tripulante_" + colorStr + ".png");
                        sprite.setScaleX(3.5);
                        sprite.setScaleY(3.5);
                        contenedorSprites.getChildren().add(sprite);
                    } catch (Exception ex) {
                        System.err.println("No se pudo cargar sprite para el ganador");
                    }
                }
                contenedorSprites.layoutBoundsProperty().addListener((obs, old, bounds) -> {
                    contenedorSprites.setTranslateX((FXGL.getAppWidth() / 2.0) - (bounds.getWidth() / 2.0));
                });
                contenedorSprites.setTranslateY((FXGL.getAppHeight() / 2.0) + 10);

                grupoFinal.getChildren().add(contenedorSprites);
            }

            FXGL.addUINode(grupoFinal);

            FXGL.getGameTimer().runOnceAfter(() -> {
                volverAlMenuPrincipal();
            }, javafx.util.Duration.seconds(6));

        } catch (Exception e) {
            System.err.println("Error cargando pantalla de fin: " + e.getMessage());
        }
    }

    /**
     * carga el archivo fxml dedicado exclusivamente a una reunion de emergencia bien sea por reporte de un cuerpo o por
     * presionar el boton rojo
     */
    public static void cargarInterfazVotacion() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    AppPrincipal.class.getResource("/ui/reunion.fxml")
            );
            javafx.scene.Parent root = loader.load();
            root.setTranslateX((FXGL.getAppWidth() - 800) / 2.0);
            root.setTranslateY((FXGL.getAppHeight() - 600) / 2.0);

            FXGL.addUINode(root);
            if (botonAccion != null) botonAccion.setVisible(false);
            if (botonMatar != null) botonMatar.setVisible(false);
            if (botonReportar != null) botonReportar.setVisible(false);

        } catch (Exception e) {
            System.err.println("Error al cargar FXML de la reunión: ");
            e.printStackTrace();
        }
    }

    /**
     * analiza el conteo de votos emitidos en una reunion de emergencia y efectua las consecuencias pertinentes
     * como expulsar a un jugador o dejarlo en empate
     * a su vez verifica si dicha expulsion representa una condicion de victoria
     * @param res es el objeto que contiene los datos finales tras la votacion
     */
    public static void procesarExpulsion(ResultadoVotacion res) {
        var cadaveres = FXGL.getGameWorld().getEntitiesByType(TipoEntidad.CADAVER);
        for (com.almasb.fxgl.entity.Entity cadaver : cadaveres) {
            cadaver.removeFromWorld();
        }
        if (res.expulsado == null || res.expulsado.equals("Nadie") || res.expulsado.equals("SKIP")) {
            System.out.println("Nadie fue expulsado. Empate o Skip.");
            return;
        }

        System.out.println("El jugador " + res.expulsado + " fue expulsado");
        if (res.expulsado.equals(MenuController.nombreUsuario)) {
            estoyMuerto = true;
            if (jugador != null) {
                jugador.getViewComponent().setOpacity(0.5);
                if (jugador.hasComponent(AnimacionJugador.class)) {
                    AnimacionJugador animacion = jugador.getComponent(AnimacionJugador.class);
                    animacion.estaMuerto = true;
                    animacion.convertirFantasma();
                }
            }
        }
        else {
            com.almasb.fxgl.entity.Entity entidadExpulsado = otrosJugadores.get(res.expulsado);
            if (entidadExpulsado != null) {
                if (entidadExpulsado.hasComponent(AnimacionJugador.class)) {
                    AnimacionJugador animacion = entidadExpulsado.getComponent(AnimacionJugador.class);
                    animacion.estaMuerto = true;
                }

                if (!estoyMuerto) {
                    entidadExpulsado.getViewComponent().setVisible(false);
                    entidadExpulsado.getViewComponent().setOpacity(0.0);
                } else {
                    entidadExpulsado.getViewComponent().setVisible(true);
                    entidadExpulsado.getViewComponent().setOpacity(0.5);
                }
                if (entidadExpulsado.hasComponent(com.almasb.fxgl.physics.PhysicsComponent.class)) {
                    entidadExpulsado.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityX(0);
                    entidadExpulsado.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityY(0);
                }
            }
        }

        if (estoyMuerto) {
            for (com.almasb.fxgl.entity.Entity otro : otrosJugadores.values()) {
                if (otro.hasComponent(AnimacionJugador.class)) {
                    AnimacionJugador animOtro = otro.getComponent(AnimacionJugador.class);
                    if (animOtro.estaMuerto) {
                        otro.getViewComponent().setVisible(true);
                        otro.getViewComponent().setOpacity(0.5);
                    }
                }
            }
        }
    }

    /**
     * muestra por pantalla el rol correspondiente segun lo que le toque a cada persona dentro de la partida
     */
    private static void mostrarPantallaRol() {
        String imagenRol = esImpostor ? "pantalla_impostor.png" : "pantalla_tripulante.png";
        String miColor = jugador.getComponent(AnimacionJugador.class).getColor();

        try {
            Texture texturaFondo = FXGL.texture(imagenRol);
            Texture texturaJugador = FXGL.texture("tripulante_" + miColor + ".png");
            texturaJugador.setScaleX(4.0);
            texturaJugador.setScaleY(4.0);

            texturaJugador.setTranslateX((texturaFondo.getWidth() / 2.0) - (texturaJugador.getWidth() / 2.0));
            texturaJugador.setTranslateY((texturaFondo.getHeight() / 2.0) - (texturaJugador.getHeight() / 2.0));

            javafx.scene.Group grupoRol = new javafx.scene.Group(texturaFondo, texturaJugador);

            grupoRol.setTranslateX((FXGL.getAppWidth() / 2.0) - (texturaFondo.getWidth() / 2.0));
            grupoRol.setTranslateY((FXGL.getAppHeight() / 2.0) - (texturaFondo.getHeight() / 2.0));

            FXGL.getInput().setRegisterInput(false);
            FXGL.addUINode(grupoRol);

            FXGL.getGameTimer().runOnceAfter(() -> {
                FXGL.removeUINode(grupoRol);
                FXGL.getInput().setRegisterInput(true);
            }, javafx.util.Duration.seconds(3.5));

        } catch(Exception e) {
            System.err.println("Error al cargar la pantalla de roles: " + e.getMessage());
        }
    }

    /**
     * elimina el cuerpo de un jugador del mapa
     * @param nombreUsuario representa el username del jugador a remover del mapa
     */
    public static void removerJugador(String nombreUsuario) {
        if (otrosJugadores.containsKey(nombreUsuario)) {
            com.almasb.fxgl.entity.Entity jugadorARemover = otrosJugadores.get(nombreUsuario);
            if (jugadorARemover != null) {
                jugadorARemover.removeFromWorld();
            }
            otrosJugadores.remove(nombreUsuario);
        }
    }
    /**
     * elimina a todas las entidades del mapa tras salir de una pantalla de votacion, los devuelve a su spawn inicial
     */
    public static void limpiarTodosLosJugadores() {
        for (com.almasb.fxgl.entity.Entity otro : otrosJugadores.values()) {
            if (otro != null) {
                otro.removeFromWorld();
            }
        }
        otrosJugadores.clear();
        if (jugador != null) {
            jugador.removeFromWorld();
        }
    }
    /**
     * reestablece todas las variables estaticas y limpia la pantalla para devolver al jugador
     * al menu principal del juego, a su vez cortando todas las conexiones al servidor
     */
    public static void volverAlMenuPrincipal() {

        if (miCliente != null && miCliente.cliente != null && miCliente.cliente.isConnected()) {
            miCliente.cliente.close();
        }
        miCliente = null;
        MenuController.cliente = null;
        MenuController.estadoActual = null;
        com.almasb.fxgl.dsl.FXGL.getGameScene().clearUINodes();
        com.almasb.fxgl.dsl.FXGL.getGameWorld().getEntitiesCopy().forEach(com.almasb.fxgl.entity.Entity::removeFromWorld);
        com.almasb.fxgl.dsl.FXGL.getGameScene().getViewport().unbind();
        com.almasb.fxgl.dsl.FXGL.getGameScene().getViewport().setX(0);
        com.almasb.fxgl.dsl.FXGL.getGameScene().getViewport().setY(0);
        com.almasb.fxgl.dsl.FXGL.getGameScene().getViewport().setZoom(1.0);

        jugador = null;
        otrosJugadores.clear();
        esImpostor = false;
        estoyMuerto = false;
        sabotajeActivo = false;
        peticionSabotaje = false;
        tareasCompletadas = 0;
        accionDisponible = false;
        indiceTareaCercana = -1;
        enMinijuego = false;

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    AppPrincipal.class.getResource("/ui/menuPrincipal.fxml")
            );
            loader.setController(new MenuController());
            javafx.scene.Parent root = loader.load();
            com.almasb.fxgl.dsl.FXGL.addUINode(root);
            com.almasb.fxgl.dsl.FXGL.getInput().setRegisterInput(true);
        } catch (Exception e) {
            System.err.println("Error al recargar la interfaz del menú principal");
            e.printStackTrace();
        }
    }

    /**
     * metodo principal que se encargara de ejecutar el proyecto
     * @param args argumentos necesarios para realizar la ejecucion
     */
    public static void main(String[] args) {
        launch(args);
    }
}
