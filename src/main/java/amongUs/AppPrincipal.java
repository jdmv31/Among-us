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


public class AppPrincipal extends GameApplication {
    public static Entity jugador;
    public static Cliente miCliente;
    public static Map<String, Entity> otrosJugadores = new HashMap<>();

    @Override
    protected void initInput() {
        int velocidadFisica = 150;

        FXGL.getInput().addAction(new UserAction("Mover Arriba") {
            @Override
            protected void onAction() {
                if (jugador != null) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(-velocidadFisica);
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
                if (jugador != null) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityY(velocidadFisica);
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
                if (jugador != null) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(-velocidadFisica);
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
                if (jugador != null) {
                    jugador.getComponent(PhysicsComponent.class).setVelocityX(velocidadFisica);
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
