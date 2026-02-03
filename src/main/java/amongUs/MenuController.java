package main.java.amongUs;

import com.almasb.fxgl.dsl.FXGL;
import javafx.fxml.FXML;

public class MenuController {

    @FXML
    private void onSalir() {
        FXGL.getGameController().exit();
    }

    @FXML
    private void onIniciarJuego() {
        System.out.println("Iniciando el juego...");
    }
}