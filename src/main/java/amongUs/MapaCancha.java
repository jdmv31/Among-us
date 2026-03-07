package main.java.amongUs;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MapaCancha implements ConfiguracionMapa{

    @Override
    public String getArchivoTMX() {
        return "mapa2.tmx";
    }

    @Override
    public double[] getLimitesCamara() {
        return new double[] { 0, 0, 992, 960 };
    }

    @Override
    public Point2D getPuntoAparicionCentral() {
        return new Point2D(300, 200);
    }

    @Override
    public List<NodoAlcantarilla> getRedAlcantarillas() {
        List<NodoAlcantarilla> red = new ArrayList<>();
        red.add(new NodoAlcantarilla(0,81,354,-1,4,-1,1)); // hueco 1
        red.add(new NodoAlcantarilla(1,164,797,-1,2,0,-1)); // hueco 2
        red.add(new NodoAlcantarilla(2,808,768,1,-1,3,-1)); // hueco 3
        red.add(new NodoAlcantarilla(3,897,386,-1,-1,-1,2)); // hueco 4
        red.add(new NodoAlcantarilla(4,550,223,0,-1,-1,-1)); // hueco 5

        return red;
    }

    @Override
    public Point2D getPosicionMesaCamaras() {
        return new Point2D(110, 750);
    }

}
