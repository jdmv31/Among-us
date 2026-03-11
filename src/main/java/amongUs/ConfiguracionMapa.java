package main.java.amongUs;

import javafx.geometry.Point2D;
import java.util.List;

/**
 * @author Josue Medina
 * interfaz empleada para definir la estructura que debe de emplear los diversos mapas del juego
 * se asegura de cada mapa posea los datos basicos para que el motor FXGL pueda renderizarlos
 * e interactuar con ellos correctamente, tales como los archivos TMX que contienen los mapas
 */

public interface ConfiguracionMapa {
    /** @return el archivo TMX generado por Tiled para almacenar los dos mapas*/
    String getArchivoTMX();
    /** @return arreglo con los limites de la camara para que el jugador no se vea por fuera del mapa*/
    double[] getLimitesCamara();
    /** @return posicion a spawnear en el mapa*/
    Point2D getPuntoAparicionCentral();
    /** @return arreglo de clases para que el impostor se mueva libremente a traves de las alcantarillas*/
    List<NodoAlcantarilla> getRedAlcantarillas();
    /** @return posicion de la mesa de camaras en el mapa*/
    Point2D getPosicionMesaCamaras();
    /** @return posicion del boton de emergencia en el mapa, se encuentra en el centro*/
    Point2D getPosicionBotonEmergencia();
    /** @return arreglo para obtener las posiciones exactas en la que las camaras se situaran*/
    Point2D[] getCoordenadasCamaras();
}