package main.java.amongUs;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.UIController; // Importante para que FXGL lo reconozca
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class MenuController implements UIController {

    @Override
    public void init() {
        // Se ejecuta al cargar la interfaz, puedes dejarlo vacío por ahora
    }

    @FXML
    private void onSalir() {
        FXGL.getGameController().exit();
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
    private void onCreditos(){
        cambiarEscena("/ui/creditos.fxml");
    }

    @FXML
    private void onVolverMenu() {
        cambiarEscena("/ui/menuPrincipal.fxml");
    }

    @FXML
    private void onIniciarJuego() {
        System.out.println("Iniciando el juego...");
        // Aquí podrías limpiar la UI y empezar la lógica de red con Kryonet
    }
}