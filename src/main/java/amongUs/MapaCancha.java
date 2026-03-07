package main.java.amongUs;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class MapaCancha implements ConfiguracionMapa{

    public Tarea[] obtenerTareas() {
        Tarea[] tareas = new Tarea[2];
        AnimationChannel animacionFuego = new AnimationChannel(
                FXGL.image("animacion_extintor.png"),
                9, 300, 400, Duration.seconds(1), 0, 9
        );

        Tarea tareaFuego = new Tarea(
                1,
                "Apagar Incendio",
                new Point2D(599, 164),
                "panel_extintor.png",
                new Point2D(0, 0),
                animacionFuego,
                new Rectangle2D(104, 121, 86, 183)
        );
        tareaFuego.setDuracionSegundos(8);
        tareaFuego.setTexturaFinal("extintor_apagado.png");
        tareas[0] = tareaFuego;

        AnimationChannel animacionDatos = new AnimationChannel(
                FXGL.image("animacion_datos.png"),
                8, 615, 400, Duration.seconds(1), 0, 8
        );

        Tarea tareaDatos = new Tarea(
                2,
                "Transferir Datos",
                new Point2D(796, 701),
                "panel_datos.png",
                new Point2D(0, 0),
                animacionDatos,
                new Rectangle2D(240, 251, 131, 25)
        );
        tareaDatos.setDuracionSegundos(10.0);
        tareaDatos.setTexturaFinal("datos_terminado.png");
        tareas[1] = tareaDatos;

        return tareas;
    }


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
