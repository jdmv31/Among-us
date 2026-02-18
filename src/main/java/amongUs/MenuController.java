package main.java.amongUs;

import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.UIController; // Importante para que FXGL lo reconozca
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.audio.Sound;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MenuController implements UIController {
    @FXML private Pane ventanaModal;
    @FXML private Pane ventanaModal1;
    @FXML private ImageView imgGuia;
    private final String [] imagenesGuia = {
            "1.png",
            "2.png",
            "3.png",
            "4.png",
            "5.png",
            "6.png"
    };

    private int imagenActual = 0;

    @Override
    public void init() {
        // Se ejecuta al cargar la interfaz, puedes dejarlo vacío por ahora
    }

    @FXML
    private void onSalir() {
        FXGL.getGameController().exit();
    }

    @FXML
    private void atrasarImagen(){
        if (imagenActual == 0)
            imagenActual = imagenesGuia.length - 1;
        else
            imagenActual--;
        actualizarImagen();
    }
    @FXML
    private void adelantarImagen(){
        if (imagenActual == imagenesGuia.length -1)
            imagenActual = 0;
        else
            imagenActual++;

        actualizarImagen();
    }

    private void actualizarImagen() {
        String nombreImagen = imagenesGuia[imagenActual];

        imgGuia.setImage(FXGL.getAssetLoader().loadImage(nombreImagen));
    }

    private void cambiarEscena(String rutaFXML) {
        try {
            FXGL.getGameScene().clearUINodes();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            if (loader.getController() == null) {
                loader.setController(this);
            }

            Parent root = loader.load();
            FXGL.addUINode(root);
        } catch (Exception e) {
            System.err.println("Error cargando: " + rutaFXML);
            e.printStackTrace();
        }
    }

    @FXML
    private void botonPresionado(){
        Sound sonido = FXGL.getAssetLoader().loadSound("clickBoton.mp3");
        getAudioPlayer().playSound(sonido);
    }

    @FXML
    private void onTutorial(){
        botonPresionado();
        cambiarEscena("/ui/tutorial.fxml");
    }

    @FXML
    private void onCreditos(){
        botonPresionado();
        cambiarEscena("/ui/creditos.fxml");
    }

    @FXML
    private void onVolverMenu() {
        botonPresionado();
        cambiarEscena("/ui/menuPrincipal.fxml");
    }

    @FXML
    private void onUnirsePartida(){
        botonPresionado();
        ventanaModal1.setVisible(false);
        ventanaModal.setVisible(true);
    }

    @FXML
    private void onCrearPartida(){
        botonPresionado();
        ventanaModal.setVisible(false);
        ventanaModal1.setVisible(true);
    }

    @FXML
    private void onJugar(){
        botonPresionado();
        cambiarEscena("/ui/jugar.fxml");
    }

    @FXML
    private void onIniciarJuego() {
        System.out.println("Iniciando el juego...");
        // Aquí podrías limpiar la UI y empezar la lógica de red con Kryonet
    }
}