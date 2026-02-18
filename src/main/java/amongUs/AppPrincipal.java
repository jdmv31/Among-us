package main.java.amongUs;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.Parent;

public class AppPrincipal extends GameApplication {
    public static Entity jugador;
    public static Cliente miCliente;

    @Override
    protected void initInput() {
        double velocidad = 3.5;
        FXGL.onKey(javafx.scene.input.KeyCode.W, () -> {
            if (jugador != null) {
                jugador.translateY(-velocidad);
                enviarCoordenadas();
            }
        });
        FXGL.onKey(javafx.scene.input.KeyCode.S, () -> {
            if (jugador != null) {
                jugador.translateY(velocidad);
                enviarCoordenadas();
            }
        });
        FXGL.onKey(javafx.scene.input.KeyCode.A, () -> {
            if (jugador != null) {
                jugador.translateX(-velocidad);
                enviarCoordenadas();
            }
        });
        FXGL.onKey(javafx.scene.input.KeyCode.D, () -> {
            if (jugador != null) {
                jugador.translateX(velocidad);
                enviarCoordenadas();
            }
        });
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
    public static void empezarPartida(String nombreMapa) {
        try {
            FXGL.getGameScene().clearUINodes();
            FXGL.setLevelFromMap(nombreMapa);

            SpawnData data = new SpawnData(300, 200);
            data.put("nombre", MenuController.nombreUsuario);

            jugador = FXGL.spawn("jugador", data);

            Viewport viewport = FXGL.getGameScene().getViewport();
            viewport.setZoom(1.5);
            viewport.bindToEntity(jugador, FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0);
            viewport.setBounds(0, 0, 992, 960);

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
