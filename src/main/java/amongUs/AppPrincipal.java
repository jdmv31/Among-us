package main.java.amongUs;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.Parent;

public class AppPrincipal extends GameApplication {
    // Variables globales en AppPrincipal
    private Entity jugador;
    private Cliente miCliente; // Asumiendo que instanciaste tu cliente al iniciar

    @Override
    protected void initInput() {
        double velocidad = 5.0;

        FXGL.onKey(javafx.scene.input.KeyCode.W, () -> {
            jugador.translateY(-velocidad);
            enviarCoordenadas();
        });
        FXGL.onKey(javafx.scene.input.KeyCode.S, () -> {
            jugador.translateY(velocidad);
            enviarCoordenadas();
        });
        FXGL.onKey(javafx.scene.input.KeyCode.A, () -> {
            jugador.translateX(-velocidad);
            enviarCoordenadas();
        });
        FXGL.onKey(javafx.scene.input.KeyCode.D, () -> {
            jugador.translateX(velocidad);
            enviarCoordenadas();
        });
    }

    private void enviarCoordenadas() {
        if (miCliente != null && miCliente.cliente.isConnected()) {
            Movimiento mov = new Movimiento();
            mov.username = "miUsuario";
            mov.x = (int) jugador.getX();
            mov.y = (int) jugador.getY();
            miCliente.cliente.sendUDP(mov);
        }
    }
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Among Us - Java Edition");
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
        try{
            FXGL.setLevelFromMap("mapa2.tmx");
        }catch(Exception e){
            System.err.println("Error cargando el mapa: " + e.getMessage());
        }
        jugador = FXGL.spawn("jugador", 100, 100);
        miCliente = new Cliente();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
