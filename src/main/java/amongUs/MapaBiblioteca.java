package main.java.amongUs;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MapaBiblioteca implements ConfiguracionMapa {

    public Tarea[] obtenerTareas() {
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
        return new Point2D(578, 415);
    }

    @Override
    public List<NodoAlcantarilla> getRedAlcantarillas() {
        List<NodoAlcantarilla> red = new ArrayList<>();
        return red;
    }

    @Override
    public Point2D getPosicionMesaCamaras() {
        return new Point2D(99, 486);
    }

    @Override
    public Point2D getPosicionBotonEmergencia() {
        return new Point2D(587, 268);
    }

    @Override
    public Point2D[] getCoordenadasCamaras() {
        return new Point2D[] {
                new Point2D(679, 175), // camara 1
                new Point2D(395, 255), // camara 2
                new Point2D(249, 542), // camara 3
                new Point2D(466, 738)  // camara 4
        };
    }
}