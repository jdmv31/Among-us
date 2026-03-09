package main.java.amongUs;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.animation.Animation;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class MapaCancha implements ConfiguracionMapa{

    public Tarea[] obtenerTareas() {
        Tarea[] tareas = new Tarea[6];
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

        AnimationChannel animacionTemperatura = new AnimationChannel(
                FXGL.image("animacion_temperatura.png"),
                7,700,391,Duration.seconds(1),0,6
        );

        Tarea tareaTemperatura = new Tarea(
                3,
                "Regular Temperatura",
                new Point2D(861,386),
                "panel_temperatura.png",
                new Point2D(0,0),
                animacionTemperatura,
                new Rectangle2D(167,66,384,247)
        );
        tareaTemperatura.setDuracionSegundos(20);
        tareaTemperatura.setTexturaFinal("temperatura_final.png");
        tareas[2] = tareaTemperatura;

        AnimationChannel animacionCancha = new AnimationChannel (
                FXGL.image("animacion_encestar.png"),
                10,400,500,Duration.seconds(1),0,9
        );

        Tarea tareaCancha = new Tarea(
                4,
                "Encestar balon",
                new Point2D(377,788),
                "panel_encestar.png",
                new Point2D(0,0),
                animacionCancha,
                new Rectangle2D(137,239,121,116)
        );
        tareaCancha.setDuracionSegundos(8);
        tareaCancha.setTexturaFinal("encestar_final.png");
        tareas[3] = tareaCancha;

        AnimationChannel animacionBasura = new AnimationChannel(
                FXGL.image("animacion_basura.png"),
                6,400,500,Duration.seconds(1),0,5
        );

        Tarea tareaBasura = new Tarea(
                5,
                "Desechar Basura",
                new Point2D(122,209),
                "panel_basura.png",
                new Point2D(0,0),
                animacionBasura,
                new Rectangle2D(298,158,80,118)
        );
        tareaBasura.setDuracionSegundos(8);
        tareaBasura.setTexturaFinal("basura_final.png");
        tareas[4] = tareaBasura;

        AnimationChannel animacionTarjeta = new AnimationChannel(
                FXGL.image("animacion_tarjeta.png"),
                10,415,300,Duration.seconds(1),0,9
        );

        Tarea tareaTarjeta = new Tarea(
                6,
                "Escanear Tarjeta",
                new Point2D(895,442),
                "panel_tarjeta.png",
                new Point2D(0,0),
                animacionTarjeta,
                new Rectangle2D(181,50,186,177)

        );
        tareaTarjeta.setDuracionSegundos(5);
        tareaTarjeta.setTexturaFinal("tarjeta_final.png");
        tareas[5] = tareaTarjeta;

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
    // nicole: esto representa las coordenadas de las camaras en el mapa
    public Point2D[] getCoordenadasCamaras() {
        return new Point2D[] {
                new Point2D(216, 713),  // camara del pasillo de abajo
                new Point2D(168, 352),  // camara de arriba, pasillo bomberos
                new Point2D(690, 506),  // camara pasillo de comedor a laboratorio
                new Point2D(799, 689)   // camara de la cantina
        };
    }

    @Override
    public Point2D getPosicionMesaCamaras() {
        return new Point2D(110, 750);
    }

    @Override
    public Point2D getPosicionBotonEmergencia() {
        return new Point2D(457, 397);
    }

}
