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

public class ReunionController {
    public static ReunionController instancia;
    @FXML private GridPane contenedorVotos;
    @FXML private TextArea areaChat;
    @FXML private TextField campoMensaje;
    @FXML private Label lblTiempo;
    @FXML private Label lblMensajeSistema;
    private int tiempoRestante = 180;
    private TimerAction temporizador;
    private boolean haVotado = false;
    private Map<String, HBox> cartasJugadores = new java.util.HashMap<>();

    @FXML
    public void initialize() {
        instancia = this;
        cargarJugadores();
        iniciarTemporizador();
    }

    private void iniciarTemporizador() {
        if (lblTiempo != null) {
            lblTiempo.setText("Tiempo: " + tiempoRestante);
            lblTiempo.setTextFill(Color.WHITE);
        }

        temporizador = FXGL.getGameTimer().runAtInterval(() -> {
            tiempoRestante--;

            if (lblTiempo != null) {
                lblTiempo.setText("Tiempo: " + tiempoRestante);

                // angelo: cambia a rojo cuando queden 10 segundos
                if (tiempoRestante <= 10) {
                    lblTiempo.setTextFill(Color.RED);
                }
            }

            if (tiempoRestante <= 0) {
                temporizador.expire();
                if (!haVotado && !AppPrincipal.estoyMuerto) {
                    emitirVoto("SKIP", null, null);
                }
            }
        }, javafx.util.Duration.seconds(1.0));
    }

    @FXML
    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();
        if (!texto.isEmpty() && AppPrincipal.miCliente != null) {
            MensajeChat msg = new MensajeChat();
            msg.emisor = MenuController.nombreUsuario;
            msg.mensaje = texto;
            msg.esFantasma = AppPrincipal.estoyMuerto;

            AppPrincipal.miCliente.cliente.sendTCP(msg);
            campoMensaje.clear();
        }
    }

    public void agregarMensaje(String emisor, String mensaje, boolean esFantasma) {
        String prefijo = esFantasma ? "[Fantasma] " : "";
        areaChat.appendText(prefijo + emisor + ": " + mensaje + "\n");
    }

    public void cerrarReunion() {
        instancia = null;

        if (temporizador != null) {
            temporizador.expire();
        }

        if (contenedorVotos != null) {
            javafx.scene.Node raiz = contenedorVotos;
            while (raiz.getParent() != null && !(raiz.getParent() instanceof javafx.scene.Group)) {
                raiz = raiz.getParent();
            }
            FXGL.removeUINode(raiz);
        }

        FXGL.getInput().setRegisterInput(true);
        if (AppPrincipal.botonAccion != null) AppPrincipal.botonAccion.setVisible(true);

        if (AppPrincipal.botonMatar != null && AppPrincipal.esImpostor) {
            AppPrincipal.botonMatar.setVisible(!AppPrincipal.estoyMuerto);
        }
        if (AppPrincipal.botonReportar != null) {
            AppPrincipal.botonReportar.setVisible(!AppPrincipal.estoyMuerto);
        }

        if (AppPrincipal.mapaActual != null) {
            javafx.geometry.Point2D spawn = AppPrincipal.mapaActual.getPuntoAparicionCentral();
            if (AppPrincipal.jugador != null) {
                if (AppPrincipal.jugador.hasComponent(com.almasb.fxgl.physics.PhysicsComponent.class)) {
                    AppPrincipal.jugador.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).overwritePosition(spawn);
                } else {
                    AppPrincipal.jugador.setPosition(spawn);
                }
            }
            for (com.almasb.fxgl.entity.Entity otro : AppPrincipal.otrosJugadores.values()) {
                if (otro.hasComponent(com.almasb.fxgl.physics.PhysicsComponent.class)) {
                    otro.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).overwritePosition(spawn);
                } else {
                    otro.setPosition(spawn);
                }
            }
        }
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
        carta.setPrefWidth(230);
        carta.setMinHeight(40);

        String rutaImagen = estaMuerto ? color + "_muerto.png" : "tripulante_" + color + ".png";
        ImageView icono;
        try {
            icono = new ImageView(FXGL.image(rutaImagen));
        } catch (Exception e) {
            System.err.println("No se encontró la imagen: " + rutaImagen);
            icono = new ImageView();
        }

        icono.setFitWidth(50);
        icono.setFitHeight(50);

        Label labelNombre = new Label(nombre);
        labelNombre.setTextFill(estaMuerto ? Color.RED : Color.WHITE);
        labelNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);
        carta.getChildren().addAll(icono, labelNombre, espaciador);

        if (estaMuerto) {
            Label lblMuerto = new Label("X");
            lblMuerto.setTextFill(Color.RED);
            lblMuerto.setStyle("-fx-font-weight: bold; -fx-font-size: 28px;");
            carta.getChildren().add(lblMuerto);
        }
        else if (!AppPrincipal.estoyMuerto && !nombre.equals(MenuController.nombreUsuario)) {
            ImageView btnVotar = new ImageView(FXGL.image("botonVotar.png"));
            btnVotar.setFitWidth(35);
            btnVotar.setFitHeight(35);
            btnVotar.setStyle("-fx-cursor: hand;");
            btnVotar.setOnMouseEntered(e -> btnVotar.setOpacity(0.7));
            btnVotar.setOnMouseExited(e -> btnVotar.setOpacity(1.0));
            btnVotar.setOnMouseClicked(e -> emitirVoto(nombre, carta, btnVotar));
            carta.getChildren().add(btnVotar);
        }

        cartasJugadores.put(nombre, carta);
        contenedorVotos.add(carta, col, row);
    }

    public void mostrarResultados(ResultadoVotacion res) {
        if (temporizador != null) {
            temporizador.expire();
        }
        if (lblTiempo != null) {
            lblTiempo.setText("Votación finalizada");
            lblTiempo.setTextFill(Color.DARKRED);
        }

        if (res.votosPorJugador != null) {
            for (Map.Entry<String, Integer> entry : res.votosPorJugador.entrySet()) {
                String sospechoso = entry.getKey();
                int cantidadVotos = entry.getValue();
                if (!sospechoso.equals("SKIP") && cartasJugadores.containsKey(sospechoso)) {
                    HBox carta = cartasJugadores.get(sospechoso);
                    Label lblVotos = new Label(" +" + cantidadVotos + " votos");
                    lblVotos.setTextFill(Color.ORANGE);
                    lblVotos.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-background-color: #444; -fx-padding: 3; -fx-background-radius: 5;");

                    carta.getChildren().add(lblVotos);
                }
            }
            int skips = res.votosPorJugador.getOrDefault("SKIP", 0);
            if (skips > 0 && lblMensajeSistema != null) {
                lblMensajeSistema.setText("Votos saltados: " + skips);
            }
        }
        FXGL.getGameTimer().runOnceAfter(() -> {
            AppPrincipal.procesarExpulsion(res);
            cerrarReunion();
            FXGL.getInput().setRegisterInput(false);

            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ui/expulsion.fxml"));
                javafx.scene.layout.AnchorPane expulsionUI = loader.load();
                ExpulsionController controlador = loader.getController();

                FXGL.addUINode(expulsionUI);
                String expulsado = res.expulsado;
                String color = "rojo";
                boolean eraImpostor = false;

                if (expulsado != null && !expulsado.equals("Nadie")) {
                    if (expulsado.equals(MenuController.nombreUsuario)) {
                        color = AppPrincipal.jugador.getComponent(AnimacionJugador.class).getColor();
                        eraImpostor = AppPrincipal.esImpostor;
                    } else {
                        com.almasb.fxgl.entity.Entity ent = AppPrincipal.otrosJugadores.get(expulsado);
                        if (ent != null) {
                            color = ent.getComponent(AnimacionJugador.class).getColor();
                            eraImpostor = ent.hasComponent(ImpostorComponent.class);
                        }
                    }
                }
                controlador.iniciarCinematica(expulsado, color, eraImpostor);
            } catch (Exception e) {
                System.err.println("Error al cargar la cinemática de expulsión:");
                e.printStackTrace();
                FXGL.getInput().setRegisterInput(true);
            }

        }, javafx.util.Duration.seconds(4.5));
    }

    private void emitirVoto(String sospechoso, HBox carta, ImageView boton) {
        if (haVotado || AppPrincipal.estoyMuerto) return;
        haVotado = true;

        if (carta != null && boton != null) {
            carta.getChildren().remove(boton);
            Label lblVotado = new Label("Votado");
            lblVotado.setTextFill(Color.LIGHTGREEN);
            lblVotado.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            carta.getChildren().add(lblVotado);
        }

        VotoEmitido voto = new VotoEmitido();
        voto.votante = MenuController.nombreUsuario;
        voto.sospechoso = sospechoso;
        AppPrincipal.miCliente.cliente.sendTCP(voto);
    }

    @FXML
    private void onSaltarVoto() {
        if (haVotado || AppPrincipal.estoyMuerto) return;
        emitirVoto("SKIP", null, null);
    }
}