package main.java.amongUs;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.input.MouseButton;

public class AppPrincipal extends GameApplication {
    public static Entity jugador;
    public static Cliente miCliente;
    public static Map<String, Entity> otrosJugadores = new HashMap<>();
    public static Texture botonAccion;
    public static boolean accionDisponible = false;
    public static boolean esImpostor = false;
    public static javafx.scene.shape.Rectangle oscuridad;
    public static Texture botonMatar;
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

    @Override
    protected void initInput() {
        int velocidadFisica = 150;

        FXGL.getInput().addAction(new UserAction("Mover Arriba") {
            @Override
            protected void onAction() {
                TripulanteComponent tripComp = jugador.getComponent(TripulanteComponent.class);
                if (tripComp != null && tripComp.isEnMinijuego()) return;

                if (jugador != null && !sistemaCamaras.isCamarasAbiertas() && !jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla()) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(-velocidadFisica);

                    if (!estoyMuerto) {
                        enviarCoordenadas();
                    }
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador != null && !jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla()) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(0);

                    if (!estoyMuerto) {
                        enviarCoordenadas();
                    }
                }
            }
        }, KeyCode.W);

        FXGL.getInput().addAction(new UserAction("Mover Abajo") {
            @Override
            protected void onAction() {
                TripulanteComponent tripComp = jugador.getComponent(TripulanteComponent.class);
                if (tripComp != null && tripComp.isEnMinijuego()) return;

                if (jugador != null && !sistemaCamaras.isCamarasAbiertas() && !jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla()) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(velocidadFisica);

                    if (!estoyMuerto) {
                        enviarCoordenadas();
                    }
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador != null && !jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla()) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(0);

                    if (!estoyMuerto) {
                        enviarCoordenadas();
                    }
                }
            }
        }, KeyCode.S);

        FXGL.getInput().addAction(new UserAction("Mover Izquierda") {
            @Override
            protected void onAction() {
                TripulanteComponent tripComp = jugador.getComponent(TripulanteComponent.class);
                if (tripComp != null && tripComp.isEnMinijuego()) return;

                if (jugador != null && !sistemaCamaras.isCamarasAbiertas() && !jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla()) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(-velocidadFisica);

                    if (!estoyMuerto) {
                        enviarCoordenadas();
                    }
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador != null && !jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla()) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(0);

                    if (!estoyMuerto) {
                        enviarCoordenadas();
                    }
                }
            }
        }, KeyCode.A);

        FXGL.getInput().addAction(new UserAction("Mover Derecha") {
            @Override
            protected void onAction() {
                TripulanteComponent tripComp = jugador.getComponent(TripulanteComponent.class);
                if (tripComp != null && tripComp.isEnMinijuego()) return;

                if (jugador != null && !sistemaCamaras.isCamarasAbiertas() && !jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla()) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(velocidadFisica);

                    if (!estoyMuerto) {
                        enviarCoordenadas();
                    }
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador != null && !jugador.getComponent(ImpostorComponent.class).estaEnAlcantarilla()) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(0);

                    if (!estoyMuerto) {
                        enviarCoordenadas();
                    }
                }
            }
        }, KeyCode.D);

        FXGL.getInput().addAction(new UserAction("Abrir Camaras") {
            @Override
            protected void onActionBegin() {
                sistemaCamaras.intentarAbrirCamaras(jugador, esImpostor);
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

    }

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
    @Override
    protected void initPhysics(){
        FXGL.getPhysicsWorld().setGravity(0,0);
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.PARED) {});
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.OBJETO) {});
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Among Us UNEG");
        settings.setAppIcon("icono.png");
        settings.setManualResizeEnabled(false);
    }

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

    private final javafx.geometry.Point2D[] coordenadasCamaras = {
            new javafx.geometry.Point2D(-30, 500), // nicole: camara del pasillo de abajo cerca del cuarto de camaras
            new javafx.geometry.Point2D(-50, 150),  // camara de arriba, pasillo bomberos
            new javafx.geometry.Point2D(450, 300),  // camara pasillo de comedor a laboratorio
            new javafx.geometry.Point2D(500, 500)   // camara de la cantina
    };

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
    }

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
        if (jugador != null) {
            jugador.setZIndex((int) (jugador.getY() + (32 * 1.8)));

            if (botonAccion != null) {
                if (!esImpostor) {
                    boolean cercaDeCamaras = sistemaCamaras.getUbicacionMesaCamaras() != null &&
                            jugador.getPosition().distance(sistemaCamaras.getUbicacionMesaCamaras()) < 50;

                    TripulanteComponent tripComp = jugador.getComponent(TripulanteComponent.class);
                    boolean cercaDeTarea = tripComp != null && tripComp.hayTareaCercana();
                    boolean enMinijuego = tripComp != null && tripComp.isEnMinijuego();

                    if ((cercaDeCamaras || cercaDeTarea) && !sistemaCamaras.isCamarasAbiertas() && !enMinijuego) {
                        if (!accionDisponible) {
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

                    if (cercaDeAlcantarilla && !sistemaCamaras.isCamarasAbiertas()) {
                        if (!accionDisponible) {
                            botonAccion.setImage(FXGL.image("accion.png"));
                            accionDisponible = true;
                        }
                    } else {
                        if (accionDisponible) {
                            botonAccion.setImage(FXGL.image("accionNegada.png"));
                            accionDisponible = false;
                        }
                    }
                }
            }
        }

        for (Entity otro : otrosJugadores.values()) {
            if (otro != null) {
                otro.setZIndex((int) (otro.getY() + (32 * 1.8)));
            }
        }
    }

    public static void empezarPartida(String nombreMapa) {
        if (nombreMapa.equals("mapa2.tmx")){
            mapaActual = new MapaCancha();
        }
        else if (nombreMapa.equals("mapa1.tmx")){
            System.out.println("aca va el mapa de la biblioteca");
        }

        try {
            FXGL.getGameScene().clearUINodes();
            FXGL.setLevelFromMap(mapaActual.getArchivoTMX());

            double[] limites = mapaActual.getLimitesCamara();
            com.almasb.fxgl.app.scene.Viewport viewport = FXGL.getGameScene().getViewport();
            viewport.setBounds((int)limites[0], (int)limites[1], (int)limites[2], (int)limites[3]);
            viewport.setZoom(2.5);

            redAlcantarillas = mapaActual.getRedAlcantarillas();
            sistemaCamaras.inicializar(mapaActual.getPosicionMesaCamaras());
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
            else{
                jugador.addComponent(new TripulanteComponent());
                Tarea[] tareasDelMapa = new Tarea[0];
                if (mapaActual instanceof MapaCancha) {
                    tareasDelMapa = ((MapaCancha) mapaActual).obtenerTareas();
                }
                /*
                else if (mapaActual instanceof MapaBiblioteca) {
                    tareasDelMapa = ((MapaBiblioteca) mapaActual).obtenerTareas();
                }
                */
                jugador.getComponent(TripulanteComponent.class).asignarTareas(tareasDelMapa);
                tareasCompletadas = 0;
            }

            FXGL.addUINode(botonAccion);
            accionDisponible = false;

            botonAccion.setOnMouseClicked(e -> {
                if (accionDisponible) {
                    if (esImpostor) {
                        FXGL.getInput().mockKeyPress(javafx.scene.input.KeyCode.SPACE);
                        FXGL.getInput().mockKeyRelease(javafx.scene.input.KeyCode.SPACE);
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
    public static void completarTarea(int indiceTarea) {
        if (!esImpostor && tareasAsignadas != null && indiceTarea >= 0 && indiceTarea < tareasAsignadas.length) {
            if (!tareasAsignadas[indiceTarea].tareaCompletada()) {
                tareasAsignadas[indiceTarea].completar();
                tareasCompletadas++;

                if (barraTareasUI != null) {
                    barraTareasUI.setImage(FXGL.image("barra_" + tareasCompletadas + ".png"));
                }

                System.out.println("Tarea completada: " + tareasAsignadas[indiceTarea].getNombre());
                if (tareasCompletadas >= tareasAsignadas.length) {
                    System.out.println("¡Todas las tareas listas! Enviar aviso al servidor de victoria.");
                }
            }
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
