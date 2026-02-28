package main.java.amongUs;

import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.UIController; // Importante para que FXGL lo reconozca
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.audio.Sound;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Random;

public class MenuController implements UIController {
    @FXML private Pane ventanaModal;
    @FXML private Pane ventanaModal1;
    @FXML private ImageView imgGuia;
    @FXML private RadioButton botonBiblioteca;
    @FXML private RadioButton botonCancha;
    @FXML private TextField txtNombre;
    @FXML private TextField txtNombre2;
    @FXML private Label labelContador;
    public static Servidor servidor;
    public static Cliente cliente;
    public static String mapaSeleccionado = "mapa2.tmx";
    public static String nombreUsuario = "Tripulante";
    @FXML private HBox slot1, slot2, slot3, slot4, slot5, slot6, slot7, slot8, slot9, slot10;
    @FXML private Label nombre1,nombre2,nombre3,nombre4,nombre5,nombre6,nombre7,nombre8,nombre9,nombre10;
    @FXML private ImageView foto1,foto2,foto3,foto4,foto5,foto6,foto7,foto8,foto9,foto10;
    @FXML private Button boton1,boton2,boton3,boton4,boton5,boton6,boton7,boton8,boton9,boton10;
    @FXML private ImageView start;
    @FXML private javafx.scene.layout.TilePane panelJugadores;

    private HBox[] slots;
    private Label[] nombres;
    private ImageView[] imagenes;
    private Button[] botonesColor;
    public static MenuController instancia;
    public static EstadoLobby estadoActual;
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
        instancia = this;
    }

    @FXML
    public void initialize() {
        instancia = this;
        slots = new HBox[]{slot1, slot2, slot3, slot4, slot5, slot6, slot7, slot8, slot9,slot10};
        nombres = new Label[]{nombre1,nombre2,nombre3,nombre4,nombre5,nombre6,nombre7,nombre8,nombre9,nombre10};
        imagenes = new ImageView[]{foto1,foto2,foto3,foto4,foto5,foto6,foto7,foto8,foto9,foto10};
        botonesColor = new Button[]{boton1,boton2,boton3,boton4,boton5,boton6,boton7,boton8,boton9,boton10};

        for (HBox slot : slots) {
            if (slot != null) slot.setVisible(false);
        }
    }

    public void actualizarLobby(EstadoLobby estado) {
        estadoActual = estado;
        labelContador.setText("Jugadores: " + estado.jugadores.length + "/10");
        boolean host = false;

        for (int i = 0; i < 10; i++) {
            if (i < estado.jugadores.length) {
                JugadorLobby j = estado.jugadores[i];
                slots[i].setVisible(true);

                nombres[i].setText(j.nombre + (j.host ? " (HOST) " : ""));
                try {
                    Image sprite = FXGL.getAssetLoader().loadImage("tripulante_" + j.color + ".png");
                    imagenes[i].setImage(sprite);
                } catch (Exception e) {
                    System.out.println("No se encontró el sprite para el color: " + j.color);
                }

                boolean esMiSlot = j.nombre.equals(this.nombreUsuario);
                botonesColor[i].setDisable(!esMiSlot);
                botonesColor[i].setText("Color");

                if (esMiSlot && j.host) {
                    host = true;
                }
            } else {
                slots[i].setVisible(false);
            }
        }
        start.setVisible(host);
        // cambiar aca para hacer pruebas
        start.setDisable(estado.jugadores.length < 1);
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
        Sound sonido = FXGL.getAssetLoader().loadSound("clickBoton.wav");
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
    private void cerrarUnirse(){
        ventanaModal.setVisible(false);
    }

    @FXML
    private void cerrarCrear(){
        ventanaModal1.setVisible(false);
    }

    @FXML
    private void onUnirsePartida(){
        botonPresionado();
        ventanaModal1.setVisible(false);
        ventanaModal.setVisible(true);
    }

    @FXML
    private void unirseSala() {
        if (txtNombre2.getText().isEmpty()) return;
        nombreUsuario = txtNombre2.getText();
        cambiarEscena("/ui/lobby.fxml");
        cliente = new Cliente("127.0.0.1", nombreUsuario);
    }

    @FXML
    private void canchaSeleccionada(){
        botonBiblioteca.setSelected(false);
        mapaSeleccionado = "mapa2.tmx";
        // este es el mapa de la cancha
    }

    @FXML
    private void bibliotecaSeleccionada(){
        botonCancha.setSelected(false);
        mapaSeleccionado = "mapa1.tmx";
        // este es el mapa de la biblioteca
    }

    @FXML
    private void onCrearPartida(){
        botonPresionado();
        ventanaModal.setVisible(false);
        ventanaModal1.setVisible(true);
    }

    @FXML
    private void crearSala() {
        botonPresionado();
        try {
            if (txtNombre != null && !txtNombre.getText().trim().isEmpty()) {
                nombreUsuario = txtNombre.getText().trim();
            } else {
                int numeroID = (int) (Math.random() * 999) + 1;
                nombreUsuario = "Tripulante " + numeroID;
            }
            cambiarEscena("/ui/lobby.fxml");

            if (servidor == null) {
                servidor = new Servidor();
            }

            if (cliente != null) {
                cliente.cliente.stop();
            }
            cliente = new Cliente("127.0.0.1", nombreUsuario);

        } catch (Exception e) {
            System.err.println("Error creando la partida");
            e.printStackTrace();
        }
    }

    @FXML
    private void onJugar(){
        botonPresionado();
        cambiarEscena("/ui/jugar.fxml");
    }

    @FXML
    private void onIniciarJuego() {
        if (cliente != null && cliente.cliente.isConnected()) {
            MapaElegido mapa = new MapaElegido();
            mapa.nombreMapa = mapaSeleccionado;
            cliente.cliente.sendTCP(mapa);
        }
    }

    @FXML
    private void abrirMenuColores(javafx.event.ActionEvent event) {
        Button botonClickeado = (Button) event.getSource();

        javafx.scene.control.ContextMenu menu = new javafx.scene.control.ContextMenu();
        String[] nombresColores = {
                "blanco", "negro", "marron", "azul", "rojo",
                "rosa", "verde", "amarillo", "morado", "naranja"
        };
        String[] coloresHex = {
                "#D6E0F0", // blanco
                "#3F474E", // negro
                "#71491E", // marron
                "#132ED1", // azul
                "#C51111", // rojo
                "#ED54BA", // rosa
                "#117F2D", // verde
                "#F5F557", // amarillo
                "#6B31BC", // morado
                "#F07D0D"  // naranja
        };

        javafx.scene.layout.TilePane panelCuadrados = new javafx.scene.layout.TilePane();
        panelCuadrados.setPrefColumns(5);
        panelCuadrados.setHgap(5);
        panelCuadrados.setVgap(5);

        for (int i = 0; i < nombresColores.length; i++) {
            String nombreColor = nombresColores[i];

            Button btnCuadrado = new Button();
            btnCuadrado.setPrefSize(30, 30);
            btnCuadrado.setStyle("-fx-background-color: " + coloresHex[i] + "; -fx-border-color: black;");

            btnCuadrado.setOnAction(e -> {
                if (cliente != null && cliente.cliente.isConnected()) {
                    PeticionColor peticion = new PeticionColor();
                    peticion.color = nombreColor;
                    cliente.cliente.sendTCP(peticion);
                }
                menu.hide();
            });

            panelCuadrados.getChildren().add(btnCuadrado);
        }

        javafx.scene.control.CustomMenuItem customItem = new javafx.scene.control.CustomMenuItem(panelCuadrados);
        customItem.setHideOnClick(false);
        menu.getItems().add(customItem);

        menu.show(botonClickeado, javafx.geometry.Side.BOTTOM, 0, 0);
    }
}