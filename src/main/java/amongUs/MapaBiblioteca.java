package main.java.amongUs;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

public class MapaBiblioteca implements ConfiguracionMapa {

    public Tarea[] obtenerTareas() {
        // Aquí puedes definir las tareas específicas para la biblioteca, similar a como lo hiciste en MapaCancha
        return new Tarea[0]; // Cambia esto cuando crees las tareas
    }

    @Override
    public String getArchivoTMX() {
        return "mapa1.tmx";
    }

    @Override
    public double[] getLimitesCamara() {
        // Ajusta estos valores según el tamaño de tu mapa en Tiled (minX, minY, maxX, maxY)
        return new double[] { 0, 0, 1000, 1000 };
    }

    @Override
    public Point2D getPuntoAparicionCentral() {
        // Coordenadas donde aparecerán los jugadores al iniciar
        return new Point2D(400, 300);
    }

    @Override
    public List<NodoAlcantarilla> getRedAlcantarillas() {
        List<NodoAlcantarilla> red = new ArrayList<>();
        // Agrega las alcantarillas de la biblioteca aquí
        return red;
    }

    @Override
    public Point2D getPosicionMesaCamaras() {
        // Dónde estará el panel para abrir las cámaras en este mapa
        return new Point2D(200, 200);
    }
}