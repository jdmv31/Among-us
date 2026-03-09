package main.java.amongUs;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class ExpulsionController {
    @FXML private AnchorPane rootPane;
    @FXML private Label lblExpulsion;

    public void iniciarCinematica(String nombreExpulsado, String colorExpulsado, boolean eraImpostor) {
        lblExpulsion.setText("");

        String textoMostrar;
        boolean mostrarAnimacion = true;

        if (nombreExpulsado.equals("Nadie")) {
            textoMostrar = "Nadie fue expulsado.";
            mostrarAnimacion = false;
        } else {
            textoMostrar = nombreExpulsado + (eraImpostor ? " era un Impostor." : " no era un Impostor.");
        }
        if (mostrarAnimacion) {
            int numeroDeFrames = 8;
            int anchoFrame = 32;
            int altoFrame = 32;
            AnimationChannel channel = new AnimationChannel(
                    FXGL.image("expulsion_"+colorExpulsado+ ".png"),
                    numeroDeFrames,
                    anchoFrame,
                    altoFrame,
                    Duration.seconds(1),
                    0,
                    numeroDeFrames - 1
            );

            AnimatedTexture texture = new AnimatedTexture(channel);
            texture.loop();
            texture.setLayoutY(250);
            texture.setLayoutX(-150);

            rootPane.getChildren().add(texture);
            TranslateTransition tt = new TranslateTransition(Duration.seconds(6), texture);
            tt.setFromX(0);
            tt.setToX(1000);
            tt.play();
        }
        escribirTexto(textoMostrar);
        Timeline cierreTimer = new Timeline(new KeyFrame(Duration.seconds(7), e -> finalizarCinematica()));
        cierreTimer.play();
    }

    private void escribirTexto(String texto) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < texto.length(); i++) {
            final String sub = texto.substring(0, i + 1);
            KeyFrame kf = new KeyFrame(Duration.millis(50 * i), e -> lblExpulsion.setText(sub));
            timeline.getKeyFrames().add(kf);
        }
        timeline.setDelay(Duration.seconds(1.5));
        timeline.play();
    }

    private void finalizarCinematica() {
        Platform.runLater(() -> {
            FXGL.removeUINode(rootPane);
            FXGL.getInput().setRegisterInput(true);
        });
    }
}