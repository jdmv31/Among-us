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

/**
 *  @author Josue Medina
 * Controlador encargado de gestionar la lógica visual y las animaciones de la
 * pantalla de expulsión (ejection) tras una votación en el juego.
 * Esta clase maneja la presentación de texto dinámico y la transición de regreso
 * al estado de juego principal utilizando el motor FXGL.
 */
public class ExpulsionController {

    /** Contenedor principal de la interfaz de expulsión definido en el FXML. */
    @FXML private AnchorPane rootPane;

    /** Etiqueta de texto donde se muestra el resultado de la expulsión. */
    @FXML private Label lblExpulsion;

    /**
     * Inicia la secuencia cinematográfica de expulsión.
     * Configura el formato del texto y determina el mensaje a mostrar según
     * si se expulsó a un jugador, si este era el impostor o si la votación fue omitida.
     * Al finalizar un periodo de 5 segundos, se llama automáticamente a {@link #finalizarCinematica()}.
     *
     * * @param nombreExpulsado Nombre del jugador que ha sido votado.
     * @param colorExpulsado Representación del color del jugador expulsado (uso futuro).
     * @param eraImpostor Booleano que indica si el jugador expulsado pertenecía al bando impostor.
     * @param jugadoresRestantes Cantidad de jugadores que aún permanecen en la partida.
     */
    public void iniciarCinematica(String nombreExpulsado, String colorExpulsado, boolean eraImpostor, int jugadoresRestantes) {
        lblExpulsion.setTextAlignment(TextAlignment.CENTER);
        lblExpulsion.setAlignment(Pos.CENTER);
        lblExpulsion.setText("");

        String textoMostrar;

        // Lógica de decisión de mensaje
        if (nombreExpulsado == null || nombreExpulsado.equals("Nadie") || nombreExpulsado.equals("SKIP")) {
            textoMostrar = "Nadie fue expulsado.\n(" + jugadoresRestantes + " jugadores restantes)";
        } else {
            textoMostrar = nombreExpulsado + (eraImpostor ? " era un Impostor." : " no era un Impostor.")
                    + "\n(" + jugadoresRestantes + " jugadores restantes)";
        }

        escribirTexto(textoMostrar);

        // Temporizador para cerrar la pantalla automáticamente
        Timeline cierreTimer = new Timeline(new KeyFrame(Duration.seconds(5), e -> finalizarCinematica()));
        cierreTimer.play();
    }

    /**
     * Realiza un efecto de "máquina de escribir" para mostrar el texto de forma progresiva.
     * Crea una línea de tiempo donde cada carácter del mensaje aparece con un ligero
     * retraso incremental, mejorando la estética narrativa de la expulsión.
     * * @param texto El mensaje completo que se desea animar en el {@code lblExpulsion}.
     */
    private void escribirTexto(String texto) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < texto.length(); i++) {
            final String sub = texto.substring(0, i + 1);
            // Se calcula el tiempo de aparición para cada subcadena
            KeyFrame kf = new KeyFrame(Duration.millis(45 * i), e -> lblExpulsion.setText(sub));
            timeline.getKeyFrames().add(kf);
        }
        timeline.setDelay(Duration.seconds(0.5));
        timeline.play();
    }

    /**
     * Limpia la interfaz de usuario y restaura el control de entrada al jugador.
     * Utiliza {@code Platform.runLater} para asegurar que las modificaciones en
     * la UI de JavaFX se realicen en el hilo correspondiente. Remueve el nodo
     * de la escena y reactiva el registro de inputs en FXGL.
     */
    private void finalizarCinematica() {
        Platform.runLater(() -> {
            FXGL.removeUINode(rootPane);
            FXGL.getInput().setRegisterInput(true);
        });
    }
}