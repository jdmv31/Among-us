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
    private boolean camarasAbiertas = false;
    private Texture btnIzq;
    private Texture btnDer;
    private int indiceCamaraActual = 0;

    @Override
    protected void initInput() {
        int velocidadFisica = 150;

        FXGL.getInput().addAction(new UserAction("Mover Arriba") {
            @Override
            protected void onAction() {
                if (jugador != null && !camarasAbiertas) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(-velocidadFisica); // Correcto (Y negativo)
                    enviarCoordenadas();
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador != null) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(0);
                    enviarCoordenadas();
                }
            }
        }, KeyCode.W);

        FXGL.getInput().addAction(new UserAction("Mover Abajo") {
            @Override
            protected void onAction() {
                if (jugador != null && !camarasAbiertas) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(velocidadFisica); // CORREGIDO: Y positivo (sin el menos)
                    enviarCoordenadas();
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador != null) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(0);
                    enviarCoordenadas();
                }
            }
        }, KeyCode.S);

        FXGL.getInput().addAction(new UserAction("Mover Izquierda") {
            @Override
            protected void onAction() {
                if (jugador != null && !camarasAbiertas) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(-velocidadFisica); // CORREGIDO: Eje X negativo
                    enviarCoordenadas();
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador != null) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(0);
                    enviarCoordenadas();
                }
            }
        }, KeyCode.A);

        FXGL.getInput().addAction(new UserAction("Mover Derecha") {
            @Override
            protected void onAction() {
                if (jugador != null && !camarasAbiertas) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(velocidadFisica); // CORREGIDO: Eje X positivo
                    enviarCoordenadas();
                }
            }
            @Override
            protected void onActionEnd() {
                if (jugador != null) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(0);
                    enviarCoordenadas();
                }
            }
        }, KeyCode.D);

        FXGL.getInput().addAction(new UserAction("Abrir Camaras") {
            @Override
            protected void onActionBegin() {
                if (jugador != null) {
                    double distancia = jugador.getPosition().distance(ubicacionMesaCamaras);

                    if (distancia < 60 || camarasAbiertas) {
                        alternarCamaras();
                    } else {
                        System.out.println("Estás muy lejos de las cámaras para usarlas.");
                    }
                }
            }
        }, KeyCode.C);
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
            System.out.println("Error: Cliente desconectado o nulo");
        }
    }
    @Override
    protected void initPhysics(){
        FXGL.getPhysicsWorld().setGravity(0,0);
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.PARED) {
            @Override
            protected void onCollision(Entity jugador, Entity pared) {
                System.out.println("¡El jugador chocó contra una pared!");
            }
        });
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.OBJETO) {
            @Override
            protected void onCollision(Entity jugador, Entity objeto) {
                System.out.println("¡El jugador está chocando con un objeto!");
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

            FXGL.getGameScene().getViewport().bindToEntity(jugador, FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0);
        } else {
            // Mostrar la interfaz y los botones
            FXGL.addUINode(uiCamaras, 0, 0);
            FXGL.addUINode(btnIzq, -10, 390);
            FXGL.addUINode(btnDer, 590, 390);
            camarasAbiertas = true;

            if (jugador != null) {
                jugador.getComponent(PhysicsComponent.class).setVelocityX(0);
                jugador.getComponent(PhysicsComponent.class).setVelocityY(0);
            }

            FXGL.getGameScene().getViewport().unbind();

            // Forzar a que siempre empiece en la primera camara al abrir
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


    @Override
    protected void onUpdate(double tpf) {
        if (jugador != null) {
            jugador.setZIndex((int) (jugador.getY() + (32 * 1.8)));
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
                viewport.setZoom(1.5);
                viewport.bindToEntity(jugador, FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0);
                viewport.setBounds(0, 0, 992, 960);
            }

            miCliente = MenuController.cliente;
            FXGL.getGameScene().getRoot().requestFocus();
        } catch(Exception e) {
            System.err.println("Error cargando el mapa: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
