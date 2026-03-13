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
import java.util.List;
import java.util.ArrayList;
import com.almasb.fxgl.time.TimerAction;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controlador de la interfaz gráfica (UI) para la fase de Reunión/Votación.
 * Gestiona el chat en vivo, el temporizador, la visualización de los jugadores vivos/muertos
 * y el envío/recepción de los votos al servidor.
 * @author Sebastián Arismendi
 */
public class ReunionController {
    /** Patrón Singleton (simplificado) para acceder a la reunión activa desde otras clases de red. */
    public static ReunionController instancia;

    /** --- Elementos de la UI enlazados con JavaFX (FXML) ---
     *
     */
    @FXML private GridPane contenedorVotos;
    @FXML private TextArea areaChat;
    @FXML private TextField campoMensaje;
    @FXML private Label lblTiempo;
    @FXML private Label lblMensajeSistema;


    /** Tiempo en segundos antes de que la votación termine automáticamente. */
    private int tiempoRestante = 180;
    private TimerAction temporizador;
    private boolean haVotado = false;

    /** Mapa para acceder rápidamente al elemento visual (HBox) de cada jugador por su nombre. */
    private Map<String, HBox> cartasJugadores = new java.util.HashMap<>();

    /** Lista de todos los botones de "Votar" en pantalla, para poder ocultarlos masivamente tras votar. */
    private List<ImageView> botonesVoto = new ArrayList<>();

    /**
     * Método llamado automáticamente por JavaFX al cargar el archivo FXML.
     * Inicializa la interfaz, dibuja a los jugadores y arranca el reloj.
     */
    @FXML


    public void initialize() {
        instancia = this;
        cargarJugadores();
        iniciarTemporizador();
    }

    /**
     * Configura e inicia un temporizador de FXGL que resta 1 segundo cada segundo real.
     * Si el tiempo llega a cero, fuerza un voto de tipo "SKIP" (Saltar) para el jugador local.
     */
    private void iniciarTemporizador() {
        if (lblTiempo != null) {
            lblTiempo.setText("Tiempo: " + tiempoRestante);
            lblTiempo.setTextFill(Color.WHITE);
        }

        temporizador = FXGL.getGameTimer().runAtInterval(() -> {
            tiempoRestante--;

            if (lblTiempo != null) {
                lblTiempo.setText("Tiempo: " + tiempoRestante);

                if (tiempoRestante <= 10) {
                    lblTiempo.setTextFill(Color.RED);
                }
            }
            if (tiempoRestante <= 0) {
                temporizador.expire();
                if (!haVotado && !AppPrincipal.estoyMuerto) {
                    emitirVoto("SKIP");
                }
            }
        }, javafx.util.Duration.seconds(1.0));
    }
    /**
     * Captura el texto ingresado en el campo de chat, crea un paquete MensajeChat
     * y lo envía al servidor para que lo redistribuya.
     */
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
    /**
     * Añade el mensaje al área de texto visual, indicando si proviene de un fantasma.
     */
    public void agregarMensaje(String emisor, String mensaje, boolean esFantasma) {
        String prefijo = esFantasma ? "[Fantasma] " : "";
        areaChat.appendText(prefijo + emisor + ": " + mensaje + "\n");
    }
    /**
     * Limpia la memoria, destruye la interfaz de la reunión y devuelve a los jugadores
     * al punto de aparición (spawn) del mapa para continuar la partida.
     */
    public void cerrarReunion() {
        instancia = null;
        botonesVoto.clear();

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
    /**
     * Organiza la distribución de los jugadores en la interfaz de votación.
     * Utiliza un sistema de rejilla (GridPane) con un máximo de 2 columnas.
     * Primero añade al jugador local y luego recorre la lista de otros jugadores.
     */
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

    /**
     * Crea individualmente la "carta" visual de cada jugador (un contenedor HBox).
     * Define si el jugador aparece como vivo o muerto, su color y añade el botón de voto.
     * * @param nombre   Nombre del jugador a mostrar.
     * @param entidad  La entidad física/lógica del jugador para extraer sus componentes.
     * @param col      Columna del GridPane donde se ubicará.
     * @param row      Fila del GridPane donde se ubicará.
     */
    private void agregarCartaJugador(String nombre, Entity entidad, int col, int row) {
        String color = "rojo";
        boolean estaMuerto = false;
        if (nombre.equals(MenuController.nombreUsuario)) {
            if (AppPrincipal.jugador.hasComponent(AnimacionJugador.class)) {
                color = AppPrincipal.jugador.getComponent(AnimacionJugador.class).getColor();
            }
            estaMuerto = AppPrincipal.estoyMuerto;
        }
        else if (entidad != null && entidad.hasComponent(AnimacionJugador.class)) {
            AnimacionJugador anim = entidad.getComponent(AnimacionJugador.class);
            color = anim.getColor();
            estaMuerto = anim.estaMuerto || anim.esFantasma;
        }

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
        else if (!AppPrincipal.estoyMuerto && !nombre.equals(MenuController.nombreUsuario) && !estaMuerto) {
            ImageView btnVotar = new ImageView(FXGL.image("botonVotar.png"));
            btnVotar.setFitWidth(35);
            btnVotar.setFitHeight(35);
            btnVotar.setStyle("-fx-cursor: hand;");
            btnVotar.setOnMouseEntered(e -> btnVotar.setOpacity(0.7));
            btnVotar.setOnMouseExited(e -> btnVotar.setOpacity(1.0));
            btnVotar.setOnMouseClicked(e -> emitirVoto(nombre));
            carta.getChildren().add(btnVotar);

            botonesVoto.add(btnVotar);
        }
        cartasJugadores.put(nombre, carta);
        contenedorVotos.add(carta, col, row);
    }
    /**
     * Pinta de verde el nombre de un jugador para indicar a todos que ya emitió su voto.
     */
    public void marcarJugadorComoVotado(String nombreJugador) {
        HBox carta = cartasJugadores.get(nombreJugador);
        if (carta != null) {
            for (javafx.scene.Node nodo : carta.getChildren()) {
                if (nodo instanceof Label) {
                    Label lbl = (Label) nodo;
                    if (lbl.getText().equals(nombreJugador)) {
                        lbl.setTextFill(Color.LIGHTGREEN);
                        break;
                    }
                }
            }
        }
    }
    /**
     * Procesa el paquete ResultadoVotacion enviado por el servidor.
     * Muestra cuántos votos recibió cada jugador en la UI, espera unos segundos
     * para que los jugadores lo vean, y luego lanza la cinemática de expulsión.
     * @param res Objeto con el desglose de los votos y el veredicto final.
     */
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

                    for (javafx.scene.Node nodo : carta.getChildren()) {
                        if (nodo instanceof Label && ((Label)nodo).getText().equals(sospechoso)) {
                            ((Label)nodo).setText("");
                            break;
                        }
                    }
                    Label lblVotos = new Label(cantidadVotos + " votos");
                    lblVotos.setTextFill(Color.ORANGE);
                    lblVotos.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-background-color: #444; -fx-padding: 3; -fx-background-radius: 5;");
                    carta.getChildren().add(lblVotos);
                }
            }
            int skips = res.votosPorJugador.getOrDefault("SKIP", 0);
            if (skips > 0) {
                String plural = (skips == 1) ? "persona ha" : "personas han";
                areaChat.appendText("\n[SISTEMA]: " + skips + " " + plural + " votado a skip.\n");
            }
            if (lblMensajeSistema != null) {
                lblMensajeSistema.setText("");
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
                boolean eraImpostor = res.expulsadoEraImpostor;
                int vivos = 0;
                if (!AppPrincipal.estoyMuerto) vivos++;

                for (Entity otro : AppPrincipal.otrosJugadores.values()) {
                    if (otro.hasComponent(AnimacionJugador.class)) {
                        AnimacionJugador anim = otro.getComponent(AnimacionJugador.class);
                        if (!anim.estaMuerto && !anim.esFantasma) {
                            vivos++;
                        }
                    }
                }
                controlador.iniciarCinematica(expulsado, color, eraImpostor, vivos);

            } catch (Exception e) {
                System.err.println("Error al cargar la cinemática de expulsión:");
                e.printStackTrace();
                FXGL.getInput().setRegisterInput(true);
            }

        }, javafx.util.Duration.seconds(4.5));
    }
    /**
     * Genera el paquete de red para votar por un jugador y lo envía al servidor.
     * También actualiza la UI local ocultando los botones para evitar doble voto.
     * @param sospechoso Nombre del jugador al que se acusa, o "SKIP".
     */
    private void emitirVoto(String sospechoso) {
        if (haVotado || AppPrincipal.estoyMuerto) return;
        haVotado = true;
        for (ImageView btn : botonesVoto) {
            if (btn.getParent() instanceof HBox) {
                ((HBox) btn.getParent()).getChildren().remove(btn);
            }
        }
        botonesVoto.clear();
        HBox miCarta = cartasJugadores.get(MenuController.nombreUsuario);
        if (miCarta != null) {
            for (javafx.scene.Node nodo : miCarta.getChildren()) {
                if (nodo instanceof Label) {
                    Label lbl = (Label) nodo;
                    if (lbl.getText().equals(MenuController.nombreUsuario)) {
                        lbl.setTextFill(Color.LIGHTGREEN);
                        break;
                    }
                }
            }
        }
        VotoEmitido voto = new VotoEmitido();
        voto.votante = MenuController.nombreUsuario;
        voto.sospechoso = sospechoso;

        if (AppPrincipal.miCliente != null && AppPrincipal.miCliente.cliente != null && AppPrincipal.miCliente.cliente.isConnected()) {
            AppPrincipal.miCliente.cliente.sendTCP(voto);
        }
    }

    /**
     * Acción del botón de interfaz "Skip Vote" (Saltar Voto).
     */
    @FXML
    private void onSaltarVoto() {
        if (haVotado || AppPrincipal.estoyMuerto) return;
        emitirVoto("SKIP");
    }
}