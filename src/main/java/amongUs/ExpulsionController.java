package main.java.amongUs;

import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Pos;

public class ExpulsionController {
    @FXML private AnchorPane rootPane;
    @FXML private Label lblExpulsion;

    public void iniciarCinematica(String nombreExpulsado, String colorExpulsado, boolean eraImpostor, int jugadoresRestantes) {
        lblExpulsion.setTextAlignment(TextAlignment.CENTER);
        lblExpulsion.setAlignment(Pos.CENTER);

        lblExpulsion.setText("");

        String textoMostrar;

        if (nombreExpulsado == null || nombreExpulsado.equals("Nadie") || nombreExpulsado.equals("SKIP")) {
            textoMostrar = "Nadie fue expulsado.\n(" + jugadoresRestantes + " jugadores restantes)";
        } else {
            textoMostrar = nombreExpulsado + (eraImpostor ? " era un Impostor." : " no era un Impostor.")
                    + "\n(" + jugadoresRestantes + " jugadores restantes)";
        }
        escribirTexto(textoMostrar);

        Timeline cierreTimer = new Timeline(new KeyFrame(Duration.seconds(5), e -> finalizarCinematica()));
        cierreTimer.play();
    }

    private void escribirTexto(String texto) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < texto.length(); i++) {
            final String sub = texto.substring(0, i + 1);
            KeyFrame kf = new KeyFrame(Duration.millis(45 * i), e -> lblExpulsion.setText(sub));
            timeline.getKeyFrames().add(kf);
        }
        timeline.setDelay(Duration.seconds(0.5));
        timeline.play();
    }

    private void finalizarCinematica() {
        Platform.runLater(() -> {
            FXGL.removeUINode(rootPane);
            FXGL.getInput().setRegisterInput(true);
        });
    }
}