package main.java.amongUs;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import java.util.HashMap;
import java.util.Map;
import com.almasb.fxgl.texture.Texture;

public class AppPrincipal extends GameApplication {
    public static Entity jugador;
    public static Cliente miCliente;
    public static Map<String, Entity> otrosJugadores = new HashMap<>();
    private Texture uiCamaras;
    private Texture btnIzq;
    private Texture btnDer;
    private int indiceCamaraActual = 0;
    public static Texture botonAccion;
    public static boolean accionDisponible = false;
    public static boolean enAlcantarilla = false;
    private Entity ventflechaIzq,ventflechaAbajo,ventflechaArriba,ventflechaDer;
    public static int alcantarillaActual = -1;
    public static boolean esImpostor = false;
    public static javafx.scene.shape.Rectangle oscuridad;
    public static Texture botonMatar;
    public static boolean matarDisponible = false;
    public static boolean cooldownKill = false;
    public static double tiempoCooldown = 0.0;
    public static javafx.scene.text.Text textoCooldown;
    public static boolean estoyMuerto = false;
    public static String victimaCercana = "";

    public static boolean camarasAbiertas = false;
    public static boolean peticionSabotaje = false;
    public static boolean sabotajeActivo = false;
    public static double tiempoSabotaje = 0;
    public static Texture botonSabotaje;
    public static javafx.scene.text.Text textoCooldownSabotaje;
    public static boolean sabotajeDisponible = true;
    public static double tiempoCooldownSabotaje = 0;

    // josue: esta es una clase interna para representar la interconexion entre las alcantarillas en caso de ser impostor
    public static class NodoAlcantarilla {
        int id;
        double x, y;
        int ventIzquierda, ventDerecha, ventArriba, ventAbajo;

        public NodoAlcantarilla(int id, double x, double y, int ventIzquierda, int ventDerecha, int ventArriba, int ventAbajo) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.ventIzquierda = ventIzquierda;
            this.ventDerecha = ventDerecha;
            this.ventArriba = ventArriba;
            this.ventAbajo = ventAbajo;
        }
    }
    public static NodoAlcantarilla[] redAlcantarillas = new NodoAlcantarilla[] {
            new NodoAlcantarilla(0, 81, 354, -1, 4, -1, 1),   // Hueco 1
            new NodoAlcantarilla(1, 164, 797, -1, 2, 0, -1),  // Hueco 2
            new NodoAlcantarilla(2, 808, 768, 1, -1, 3, -1),  // Hueco 3
            new NodoAlcantarilla(3, 897, 386, -1, -1, -1, 2), // Hueco 4
            new NodoAlcantarilla(4, 550, 223, 0, -1, -1, -1)  // Hueco 5
    };

    @Override
    protected void initInput() {
        int velocidadFisica = 150;

        FXGL.getInput().addAction(new UserAction("Mover Arriba") {
            @Override
            protected void onAction() {
                if (jugador != null && !camarasAbiertas && !enAlcantarilla) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(-velocidadFisica);

                    if (!estoyMuerto) {
                        enviarCoordenadas();
                    }
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador != null && !enAlcantarilla) {
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
                if (jugador != null && !camarasAbiertas && !enAlcantarilla) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(velocidadFisica);

                    if (!estoyMuerto) {
                        enviarCoordenadas();
                    }
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador != null && !enAlcantarilla) {
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
                if (jugador != null && !camarasAbiertas && !enAlcantarilla) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(-velocidadFisica);

                    if (!estoyMuerto) {
                        enviarCoordenadas();
                    }
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador != null && !enAlcantarilla) {
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
                if (jugador != null && !camarasAbiertas && !enAlcantarilla) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(velocidadFisica);

                    if (!estoyMuerto) {
                        enviarCoordenadas();
                    }
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador != null && !enAlcantarilla) {
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
                if (!esImpostor && jugador != null && !enAlcantarilla) {
                    double distancia = jugador.getPosition().distance(ubicacionMesaCamaras);

                    if (distancia < 60 || camarasAbiertas) {
                        alternarCamaras();
                    } else {
                        System.out.println("Estás muy lejos de las cámaras para usarlas.");
                    }
                }
            }
        }, KeyCode.C);

        FXGL.getInput().addAction(new UserAction("Usar Alcantarilla") {
            @Override
            protected void onActionBegin() {
                if (esImpostor && jugador != null && !camarasAbiertas) {
                    if (!enAlcantarilla) {

                        NodoAlcantarilla nodoCercano = null;
                        double distanciaMinima = 20.0;

                        for (NodoAlcantarilla nodo : redAlcantarillas) {
                            double distancia = jugador.getPosition().distance(nodo.x, nodo.y);
                            if (distancia < distanciaMinima) {
                                distanciaMinima = distancia;
                                nodoCercano = nodo;
                            }
                        }

                        if (nodoCercano != null) {
                            enAlcantarilla = true;
                            alcantarillaActual = nodoCercano.id;

                            jugador.getComponent(PhysicsComponent.class).setVelocityX(0);
                            jugador.getComponent(PhysicsComponent.class).setVelocityY(0);
                            jugador.getComponent(PhysicsComponent.class).overwritePosition(new javafx.geometry.Point2D(nodoCercano.x, nodoCercano.y));
                            jugador.setPosition(nodoCercano.x, nodoCercano.y);
                            enviarCoordenadas();

                            jugador.getComponent(AnimacionJugador.class).entrarAlcantarilla();
                            MovimientoAlcantarilla entrar = new MovimientoAlcantarilla();
                            entrar.nombreUsuario = miCliente.username;
                            entrar.entrando = true;
                            miCliente.cliente.sendTCP(entrar);


                            FXGL.getGameTimer().runOnceAfter(() -> {
                                if (enAlcantarilla) {
                                    jugador.getViewComponent().setVisible(false);
                                }
                            }, javafx.util.Duration.seconds(0.5));

                            mostrarFlechasVents();
                        }
                    } else {
                        alcantarillaActual = -1;
                        ocultarFlechasVents();

                        jugador.getViewComponent().setVisible(true);
                        jugador.getComponent(AnimacionJugador.class).salirAlcantarilla();
                        MovimientoAlcantarilla salir = new MovimientoAlcantarilla();
                        salir.nombreUsuario = miCliente.username;
                        salir.entrando = false;
                        miCliente.cliente.sendTCP(salir);

                        FXGL.getGameTimer().runOnceAfter(() -> {
                            enAlcantarilla = false;
                        }, javafx.util.Duration.seconds(0.5));
                    }
                }
            }
        }, KeyCode.SPACE);

        FXGL.getInput().addAction(new UserAction("Vent Izquierda") {
            @Override
            protected void onActionBegin() { viajarAlcantarilla("IZQ"); }
        }, KeyCode.LEFT);

        FXGL.getInput().addAction(new UserAction("Vent Derecha") {
            @Override
            protected void onActionBegin() { viajarAlcantarilla("DER"); }
        }, KeyCode.RIGHT);

        FXGL.getInput().addAction(new UserAction("Vent Arriba") {
            @Override
            protected void onActionBegin() { viajarAlcantarilla("ARRIBA"); }
        }, KeyCode.UP);

        FXGL.getInput().addAction(new UserAction("Vent Abajo") {
            @Override
            protected void onActionBegin() { viajarAlcantarilla("ABAJO"); }
        }, KeyCode.DOWN);

        FXGL.getInput().addAction(new UserAction("Matar Jugador") {
            @Override
            protected void onActionBegin() {
                matar();
            }
        }, KeyCode.Q);

    }

    private void viajarAlcantarilla(String direccion) {
        if (enAlcantarilla && alcantarillaActual != -1) {
            int destino = -1;
            switch(direccion) {
                case "IZQ": destino = redAlcantarillas[alcantarillaActual].ventIzquierda; break;
                case "DER": destino = redAlcantarillas[alcantarillaActual].ventDerecha; break;
                case "ARRIBA": destino = redAlcantarillas[alcantarillaActual].ventArriba; break;
                case "ABAJO": destino = redAlcantarillas[alcantarillaActual].ventAbajo; break;
            }

            if (destino != -1) {
                alcantarillaActual = destino;
                NodoAlcantarilla nuevoNodo = redAlcantarillas[destino];

                jugador.getComponent(PhysicsComponent.class).overwritePosition(new javafx.geometry.Point2D(nuevoNodo.x, nuevoNodo.y));
                jugador.setPosition(nuevoNodo.x, nuevoNodo.y);
                jugador.getViewComponent().setVisible(false);

                enviarCoordenadas();
                mostrarFlechasVents();
            }
        }
    }

    private void mostrarFlechasVents() {
        ocultarFlechasVents();

        if (alcantarillaActual == -1) return;
        NodoAlcantarilla nodoActual = redAlcantarillas[alcantarillaActual];
        double centroX = jugador.getX() - 4;
        double centroY = jugador.getY() + 4;

        if (nodoActual.ventIzquierda != -1) {
            Texture texIzq = FXGL.texture("flechaRojaIzq.png", 40, 40);
            texIzq.setOnMouseClicked(e -> viajarAlcantarilla("IZQ"));
            ventflechaIzq = FXGL.entityBuilder()
                    .at(centroX - 45, centroY) // A la izquierda
                    .view(texIzq).zIndex(2000).buildAndAttach();
        }

        if (nodoActual.ventDerecha != -1) {
            Texture texDer = FXGL.texture("flechaRojaDer.png", 40, 40);
            texDer.setOnMouseClicked(e -> viajarAlcantarilla("DER"));
            ventflechaDer = FXGL.entityBuilder()
                    .at(centroX + 53, centroY) // A la derecha
                    .view(texDer).zIndex(2000).buildAndAttach();
        }

        if (nodoActual.ventArriba != -1) {
            Texture texArriba = FXGL.texture("flechaRojaArriba.png", 40, 40);
            texArriba.setOnMouseClicked(e -> viajarAlcantarilla("ARRIBA"));
            ventflechaArriba = FXGL.entityBuilder()
                    .at(centroX + 4, centroY - 45) // Arriba
                    .view(texArriba).zIndex(2000).buildAndAttach();
        }

        if (nodoActual.ventAbajo != -1) {
            Texture texAbajo = FXGL.texture("flechaRojaAbajo.png", 40, 40);
            texAbajo.setOnMouseClicked(e -> viajarAlcantarilla("ABAJO"));
            ventflechaAbajo = FXGL.entityBuilder()
                    .at(centroX + 4, centroY + 45) // Abajo
                    .view(texAbajo).zIndex(2000).buildAndAttach();
        }
    }

    private void ocultarFlechasVents() {
        if (ventflechaIzq != null) { ventflechaIzq.removeFromWorld(); ventflechaIzq = null; }
        if (ventflechaDer != null) { ventflechaDer.removeFromWorld(); ventflechaDer = null; }
        if (ventflechaArriba != null) { ventflechaArriba.removeFromWorld(); ventflechaArriba = null; }
        if (ventflechaAbajo != null) {ventflechaAbajo.removeFromWorld(); ventflechaAbajo = null; }
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
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.PARED) {
            @Override
            protected void onCollision(Entity jugador, Entity pared) {
                System.out.println("Chocaste contra una pared");
            }
        });
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.OBJETO) {
            @Override
            protected void onCollision(Entity jugador, Entity objeto) {
                System.out.println("Chocaste contra un objeto");
            }
        });
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

    private final javafx.geometry.Point2D ubicacionMesaCamaras = new javafx.geometry.Point2D(110, 750);

    private final javafx.geometry.Point2D[] coordenadasCamaras = {
            new javafx.geometry.Point2D(-30, 500), // nicole: camara del pasillo de abajo cerca del cuarto de camaras
            new javafx.geometry.Point2D(-50, 150),  // camara de arriba, pasillo bomberos
            new javafx.geometry.Point2D(450, 300),  // camara pasillo de comedor a laboratorio
            new javafx.geometry.Point2D(500, 500)   // camara de la cantina
    };

    private void alternarCamaras() {
        if (camarasAbiertas) {
            FXGL.removeUINode(uiCamaras);
            FXGL.removeUINode(btnIzq);
            FXGL.removeUINode(btnDer);
            camarasAbiertas = false;
            if (oscuridad != null) oscuridad.setVisible(true);
            FXGL.getGameScene().getViewport().setZoom(2.5);
            FXGL.getGameScene().getViewport().bindToEntity(jugador, FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0);

        } else {
            FXGL.addUINode(uiCamaras, 0, 0);
            FXGL.addUINode(btnIzq, -10, 390);
            FXGL.addUINode(btnDer, 590, 390);
            camarasAbiertas = true;
            if (oscuridad != null) oscuridad.setVisible(false);

            if (jugador != null) {
                jugador.getComponent(PhysicsComponent.class).setVelocityX(0);
                jugador.getComponent(PhysicsComponent.class).setVelocityY(0);
            }

            FXGL.getGameScene().getViewport().unbind();
            FXGL.getGameScene().getViewport().setZoom(1.5);
            indiceCamaraActual = 0;
            actualizarVistaCamara();
        }
    }
    private void cambiarCamara(int direccion) {
        indiceCamaraActual += direccion;
        if (indiceCamaraActual < 0) {
            indiceCamaraActual = coordenadasCamaras.length - 1;
        }
        else if (indiceCamaraActual >= coordenadasCamaras.length) {
            indiceCamaraActual = 0;
        }

        actualizarVistaCamara();
    }

    private void actualizarVistaCamara() {
        Viewport viewport = FXGL.getGameScene().getViewport();
        javafx.geometry.Point2D coord = coordenadasCamaras[indiceCamaraActual];
        viewport.setX(coord.getX());
        viewport.setY(coord.getY());
    }

    private static void matar() {
        if (esImpostor && matarDisponible && !cooldownKill && !victimaCercana.isEmpty()) {
            System.out.println("¡Mataste a " + victimaCercana + "!");
            Entity victima = otrosJugadores.get(victimaCercana);
            if (victima != null) {
                victima.getComponent(AnimacionJugador.class).morir();
                if (victima.hasComponent(PhysicsComponent.class)) {
                    victima.getComponent(PhysicsComponent.class).setVelocityX(0);
                    victima.getComponent(PhysicsComponent.class).setVelocityY(0);
                }
            }
            cooldownKill = true;
            tiempoCooldown = 30.0;
            matarDisponible = false;
            botonMatar.setImage(FXGL.image("matarNegado.png"));

            Asesinato paquete = new Asesinato();
            paquete.asesino = MenuController.nombreUsuario;
            paquete.victima = victimaCercana;
            miCliente.cliente.sendTCP(paquete);

            victimaCercana = "";
        }
    }

    @Override
    protected void initGame(){
        FXGL.getGameWorld().addEntityFactory(new Fabrica());

        uiCamaras = FXGL.texture("MonitorDeCamaras.png");
        btnIzq = FXGL.texture("flechaAmarillaIzq.png");
        btnDer = FXGL.texture("flechaAmarillaDer.png");

        btnIzq.setOnMouseClicked(e -> cambiarCamara(-1));
        btnDer.setOnMouseClicked(e -> cambiarCamara(1));
        try {
            FXGL.getAudioPlayer().loopMusic(FXGL.getAssetLoader().loadMusic("musicaMenu.mp3"));
            FXGL.getSettings().setGlobalMusicVolume(0.4);
        } catch (Exception e) {
            System.err.println("Error cargando la música: " + e.getMessage());
        }
    }

    public static void iniciarSabotaje() {
        sabotajeDisponible = false;
        tiempoCooldownSabotaje = 60.0;
        botonSabotaje.setImage(FXGL.image("sabotajeNegado.png"));

        activarCorteElectrico();
        Sabotaje peticion = new Sabotaje();
        peticion.activar = true;
        // Se envía por TCP para asegurar de que todos reciban el corte eléctrico sin pérdida de paquetes
        if (miCliente != null && miCliente.cliente != null) {
            miCliente.cliente.sendTCP(peticion);
        }
    }

    public static void activarCorteElectrico() {
        sabotajeActivo = true;
        tiempoSabotaje = 15.0;

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

            if (camarasAbiertas) {
                FXGL.getInput().mockKeyPress(javafx.scene.input.KeyCode.C);
                FXGL.getInput().mockKeyRelease(javafx.scene.input.KeyCode.C);
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

    private void botonMatar(){
        if (botonMatar != null && !enAlcantarilla) {
            boolean rango = false;
            double distanciaMinima = 30.0;
            String jugadorMasCercano = "";

            for (java.util.Map.Entry<String, Entity> entry : otrosJugadores.entrySet()) {
                Entity otro = entry.getValue();
                if (otro != null && otro.getViewComponent().isVisible() && !otro.getComponent(AnimacionJugador.class).estaMuerto) {
                    double distancia = jugador.getPosition().distance(otro.getPosition());

                    if (distancia < distanciaMinima) {
                        rango = true;
                        distanciaMinima = distancia;
                        jugadorMasCercano = entry.getKey();
                    }
                }
            }

            if (rango && !cooldownKill) {
                if (!matarDisponible) {
                    botonMatar.setImage(FXGL.image("matar.png"));
                    matarDisponible = true;
                }
                victimaCercana = jugadorMasCercano;
            } else {
                if (matarDisponible || cooldownKill) {
                    botonMatar.setImage(FXGL.image("matarNegado.png"));
                    matarDisponible = false;
                    victimaCercana = "";
                }
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

                if (oscuridad != null && jugador != null && !estoyMuerto) {
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

        if (esImpostor && botonMatar != null && !enAlcantarilla) {
            if (cooldownKill) {
                tiempoCooldown -= tpf;
                if (tiempoCooldown <= 0) {
                    cooldownKill = false;
                    textoCooldown.setText("");
                } else {
                    textoCooldown.setText(String.valueOf((int) tiempoCooldown + 1));
                }
            }

            if (botonSabotaje != null && !sabotajeDisponible) {
                tiempoCooldownSabotaje -= tpf;
                if (tiempoCooldownSabotaje <= 0) {
                    sabotajeDisponible = true;
                    textoCooldownSabotaje.setText("");
                    botonSabotaje.setImage(FXGL.image("sabotaje.png"));
                } else {
                    textoCooldownSabotaje.setText(String.valueOf((int) tiempoCooldownSabotaje + 1));
                }
            }
        }

        if (esImpostor && botonMatar != null && !enAlcantarilla) {
            if (cooldownKill) {
                tiempoCooldown -= tpf;
                if (tiempoCooldown <= 0) {
                    cooldownKill = false;
                    textoCooldown.setText("");
                } else {
                    textoCooldown.setText(String.valueOf((int) tiempoCooldown + 1));
                }
            }
        }
        if (jugador != null) {
            jugador.setZIndex((int) (jugador.getY() + (32 * 1.8)));

            if (esImpostor) {
                botonMatar();
            }

            if (botonAccion != null) {
                boolean cercaDeInteraccion = false;

                if (esImpostor) {
                    for (NodoAlcantarilla nodo : redAlcantarillas) {
                        if (jugador.getPosition().distance(nodo.x, nodo.y) < 20) {
                            cercaDeInteraccion = true;
                            break;
                        }
                    }
                } else {
                    if (ubicacionMesaCamaras != null && jugador.getPosition().distance(ubicacionMesaCamaras) < 60) {
                        cercaDeInteraccion = true;
                    }
                }

                if (cercaDeInteraccion && !camarasAbiertas && !enAlcantarilla) {
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
        for (Entity otro : otrosJugadores.values()) {
            if (otro != null) {
                otro.setZIndex((int) (otro.getY() + (32 * 1.8)));
            }
        }
    }

    public static void empezarPartida(String nombreMapa) {
        try {
            FXGL.getGameScene().clearUINodes();
            FXGL.setLevelFromMap(nombreMapa);

            otrosJugadores.clear();

            if (MenuController.estadoActual != null) {
                for (JugadorLobby j : MenuController.estadoActual.jugadores) {
                    SpawnData data = new SpawnData(300, 200);
                    data.put("nombre", j.nombre);

                    Entity entidad = FXGL.spawn("jugador", data);

                    if (j.nombre.equals(MenuController.nombreUsuario)) {
                        jugador = entidad;
                    } else {
                        otrosJugadores.put(j.nombre, entidad);
                    }
                }
            }

            if (jugador != null) {
                Viewport viewport = FXGL.getGameScene().getViewport();
                viewport.setZoom(2.5);
                viewport.bindToEntity(jugador, FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0);
                viewport.setBounds(0, 0, 992, 960);
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
            double tamanoBoton = 120.0;
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
                botonMatar.setOnMouseClicked(e -> matar());
                FXGL.addUINode(botonMatar);
                textoCooldown = new javafx.scene.text.Text("");
                textoCooldown.setFill(javafx.scene.paint.Color.RED);
                textoCooldown.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 45));
                textoCooldown.setTranslateX(botonMatar.getTranslateX() + (tamanoBoton / 2.5));
                textoCooldown.setTranslateY(botonMatar.getTranslateY() + (tamanoBoton / 1.5));
                FXGL.addUINode(textoCooldown);
                botonSabotaje = FXGL.texture("sabotaje.png");
                botonSabotaje.setFitWidth(tamanoBoton);
                botonSabotaje.setFitHeight(tamanoBoton);
                botonSabotaje.setTranslateX(FXGL.getAppWidth() - (tamanoBoton * 2) - (margen * 3));
                botonSabotaje.setTranslateY(FXGL.getAppHeight() - (tamanoBoton * 2) - (margen * 2));

                botonSabotaje.setOnMouseClicked(e -> {
                    if (sabotajeDisponible && !sabotajeActivo) {
                        iniciarSabotaje();
                    }
                });
                FXGL.addUINode(botonSabotaje);

                textoCooldownSabotaje = new javafx.scene.text.Text("");
                textoCooldownSabotaje.setFill(javafx.scene.paint.Color.RED);
                textoCooldownSabotaje.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 45));
                textoCooldownSabotaje.setTranslateX(botonSabotaje.getTranslateX() + (tamanoBoton / 2.5));
                textoCooldownSabotaje.setTranslateY(botonSabotaje.getTranslateY() + (tamanoBoton / 1.5));
                FXGL.addUINode(textoCooldownSabotaje);
                matarDisponible = false;
            }

            FXGL.addUINode(botonAccion);
            accionDisponible = false;

            botonAccion.setOnMouseClicked(e -> {
                if (accionDisponible) {
                    if (esImpostor) {
                        FXGL.getInput().mockKeyPress(javafx.scene.input.KeyCode.SPACE);
                        FXGL.getInput().mockKeyRelease(javafx.scene.input.KeyCode.SPACE);
                    } else {
                        FXGL.getInput().mockKeyPress(javafx.scene.input.KeyCode.C);
                        FXGL.getInput().mockKeyRelease(javafx.scene.input.KeyCode.C);
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

    public static void main(String[] args) {
        launch(args);
    }

}
