package main.java.amongUs;

import javafx.geometry.Point2D;
import java.util.List;

public interface ConfiguracionMapa {
    String getArchivoTMX();
    double[] getLimitesCamara();
    Point2D getPuntoAparicionCentral();
    List<NodoAlcantarilla> getRedAlcantarillas();
    Point2D getPosicionMesaCamaras();
    Point2D getPosicionBotonEmergencia(); // Nuevo método
}