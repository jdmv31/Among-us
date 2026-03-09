package main.java.amongUs;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MapaBiblioteca implements ConfiguracionMapa {

    public Tarea[] obtenerTareas() {
        // Aquí puedes definir las tareas específicas para la biblioteca, similar a como lo hiciste en MapaCancha
        return new Tarea[0];
    }

    @Override
    public String getArchivoTMX() {
        return "mapa1.tmx";
    }

    @Override
    public double[] getLimitesCamara() {
        return new double[] { 0, 0, 1000, 1000 };
    }

    @Override
    public Point2D getPuntoAparicionCentral() {
        return new Point2D(400, 300);
    }

    @Override
    public List<NodoAlcantarilla> getRedAlcantarillas() {
        List<NodoAlcantarilla> red = new ArrayList<>();
        return red;
    }

    @Override
    public Point2D getPosicionMesaCamaras() {
        return new Point2D(200, 200);
    }

    @Override
    public Point2D getPosicionBotonEmergencia() {
        return new Point2D(453, 475);
    }
}