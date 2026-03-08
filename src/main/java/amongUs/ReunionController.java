package main.java.amongUs;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import java.util.Map;
import com.almasb.fxgl.time.TimerAction;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ReunionController {
    public static ReunionController instancia;
    @FXML
    private GridPane contenedorVotos;
    private int tiempoRestante = 180;
    private TimerAction temporizador;
    private Label lblTiempo;
    private VBox contenedorChat;
    private TextArea areaChat;
    private TextField campoMensaje;

    @FXML
    public void initialize() {
        instancia = this;
        contenedorVotos.setHgap(200);
        contenedorVotos.setVgap(10);

        cargarJugadores();
        iniciarTemporizador();
        crearInterfazChat();
    }

    private void iniciarTemporizador() {
        lblTiempo = new Label("Tiempo: " + tiempoRestante);
        lblTiempo.setTextFill(Color.WHITE);
        lblTiempo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-background-color: #2b2b2b; -fx-padding: 5; -fx-background-radius: 5; -fx-border-color: red; -fx-border-radius: 5;");
        lblTiempo.setTranslateX(FXGL.getAppWidth() / 2.0 - 60);
        lblTiempo.setTranslateY(20);
        FXGL.addUINode(lblTiempo);

        temporizador = FXGL.getGameTimer().runAtInterval(() -> {
            tiempoRestante--;
            lblTiempo.setText("Tiempo: " + tiempoRestante);
            if (tiempoRestante <= 10)
                lblTiempo.setTextFill(Color.RED);
            if (tiempoRestante <= 0)
                cerrarReunion();
        }, javafx.util.Duration.seconds(1.0));
    }

    private void crearInterfazChat() {
        areaChat = new TextArea();
        areaChat.setEditable(false);
        areaChat.setPrefSize(250, 300);
        areaChat.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: white; -fx-font-family: Arial;");
        campoMensaje = new TextField();
        campoMensaje.setPromptText("Escribe un mensaje...");
        campoMensaje.setPrefWidth(180);
        campoMensaje.setOnAction(e -> enviarMensaje());
        Button btnEnviar = new Button("Enviar");
        btnEnviar.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-cursor: hand;");
        btnEnviar.setOnAction(e -> enviarMensaje());
        HBox cajaInput = new HBox(5, campoMensaje, btnEnviar);
        cajaInput.setAlignment(Pos.CENTER);
        contenedorChat = new VBox(10, areaChat, cajaInput);
        contenedorChat.setStyle("-fx-background-color: #1e1e1e; -fx-padding: 10; -fx-border-color: #555555; -fx-border-radius: 5;");
        contenedorChat.setTranslateX(FXGL.getAppWidth() - 300);
        contenedorChat.setTranslateY((FXGL.getAppHeight() - 400) / 2.0);

        FXGL.addUINode(contenedorChat);
    }

    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();
        if (!texto.isEmpty() && AppPrincipal.miCliente != null) {
            MensajeChat msg = new MensajeChat();
            msg.emisor = MenuController.nombreUsuario;
            msg.mensaje = texto;
            AppPrincipal.miCliente.cliente.sendTCP(msg);
            campoMensaje.clear();
        }
    }

    public void agregarMensaje(String emisor, String mensaje) {
        areaChat.appendText(emisor + ": " + mensaje + "\n");
    }

    public void cerrarReunion() {
        instancia = null;

        if (temporizador != null) {
            temporizador.expire();
        }

        if (lblTiempo != null) {
            FXGL.removeUINode(lblTiempo);
        }

        if (contenedorChat != null) {
            FXGL.removeUINode(contenedorChat);
        }

        if (contenedorVotos != null && contenedorVotos.getParent() != null) {
            FXGL.removeUINode(contenedorVotos.getParent());
        }

        FXGL.getInput().setRegisterInput(true);
        if (AppPrincipal.botonAccion != null) AppPrincipal.botonAccion.setVisible(true);
        if (AppPrincipal.botonMatar != null && AppPrincipal.esImpostor) AppPrincipal.botonMatar.setVisible(true);
        if (AppPrincipal.botonReportar != null) AppPrincipal.botonReportar.setVisible(true);

        System.out.println("Reunión finalizada. Volviendo al mapa...");
    }

    private void cargarJugadores() {
        int columna = 0;
        int fila = 0;
        int maxColumnas = 2;
        agregarCartaJugador(MenuController.nombreUsuario, AppPrincipal.jugador, columna, fila);
        columna++;

        for (Map.Entry<String, Entity> entry : AppPrincipal.otrosJugadores.entrySet()) {
            if (columna >= maxColumnas) {
                columna = 0;
                fila++;
            }
            agregarCartaJugador(entry.getKey(), entry.getValue(), columna, fila);
            columna++;
        }
    }

    private void agregarCartaJugador(String nombre, Entity entidad, int col, int row) {
        AnimacionJugador anim = entidad.getComponent(AnimacionJugador.class);
        String color = anim.getColor();
        boolean estaMuerto = anim.estaMuerto;
        HBox carta = new HBox(15);
        carta.setAlignment(Pos.CENTER_LEFT);
        carta.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #555555; -fx-border-radius: 10;");
        carta.setPrefWidth(Region.USE_COMPUTED_SIZE);
        carta.setMinWidth(Region.USE_COMPUTED_SIZE);
        carta.setMinHeight(60);
        String rutaImagen = estaMuerto ? color + "_muerto.png" : "tripulante_" + color + ".png";
        ImageView icono;
        try {
            icono = new ImageView(FXGL.image(rutaImagen));
        } catch (Exception e) {
            System.err.println("No se encontró la imagen: " + rutaImagen);
            icono = new ImageView();
        }

        icono.setFitWidth(40);
        icono.setFitHeight(40);
        Label labelNombre = new Label(nombre);
        labelNombre.setTextFill(estaMuerto ? Color.RED : Color.WHITE);
        labelNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);
        carta.getChildren().addAll(icono, labelNombre, espaciador);

        if (estaMuerto) {
            Label lblMuerto = new Label("X");
            lblMuerto.setTextFill(Color.RED);
            lblMuerto.setStyle("-fx-font-weight: bold; -fx-font-size: 24px;");
            carta.getChildren().add(lblMuerto);

        }
        else if (!AppPrincipal.estoyMuerto) {
            ImageView btnVotar = new ImageView(FXGL.image("botonVotar.png"));
            btnVotar.setFitWidth(30);
            btnVotar.setFitHeight(30);
            btnVotar.setStyle("-fx-cursor: hand;");
            btnVotar.setOnMouseEntered(e -> btnVotar.setOpacity(0.7));
            btnVotar.setOnMouseExited(e -> btnVotar.setOpacity(1.0));
            btnVotar.setOnMouseClicked(e -> emitirVoto(nombre, carta, btnVotar));
            carta.getChildren().add(btnVotar);
        }
        contenedorVotos.add(carta, col, row);
    }

    private void emitirVoto(String sospechoso, HBox carta, ImageView boton) {
        System.out.println("Has votado por: " + sospechoso);
        carta.getChildren().remove(boton);
        Label lblVotado = new Label("Votado");
        lblVotado.setTextFill(Color.LIGHTGREEN);
        carta.getChildren().add(lblVotado);
    }
}