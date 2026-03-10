package main.java.amongUs;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

public class MapaBiblioteca implements ConfiguracionMapa {

    public Tarea[] obtenerTareas() {
        Tarea[] tareas = new Tarea[5];
        AnimationChannel animacionCodigo = new AnimationChannel(
                FXGL.image("animacion_codigo.png"),
                10,350,300,Duration.seconds(1),0,9
        );
        Tarea tareaCodigo = new Tarea(
                0,
                "Escribir Codigo",
                new Point2D(554,613),
                "panel_codigo.png",
                new Point2D(0,0),
                animacionCodigo,
                new Rectangle2D(240,181,20,17)

        );
        tareaCodigo.setDuracionSegundos(10);
        tareaCodigo.setTexturaFinal("codigo_final.png");
        tareas[0] = tareaCodigo;

        AnimationChannel animacionDatos = new AnimationChannel(
                FXGL.image("animacion_datos.png"),
                8, 615, 400, Duration.seconds(1), 0, 8
        );

        Tarea tareaDatos = new Tarea(
                1,
                "Transferir Datos",
                new Point2D(315, 290),
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
                2,
                "Regular Temperatura",
                new Point2D(578,70),
                "panel_temperatura.png",
                new Point2D(0,0),
                animacionTemperatura,
                new Rectangle2D(167,66,384,247)
        );


        tareaTemperatura.setDuracionSegundos(20);
        tareaTemperatura.setTexturaFinal("temperatura_final.png");
        tareas[2] = tareaTemperatura;

        AnimationChannel animacionBasura = new AnimationChannel(
                FXGL.image("animacion_basura.png"),
                6,400,500,Duration.seconds(1),0,5
        );

        Tarea tareaBasura = new Tarea(
                3,
                "Desechar Basura",
                new Point2D(341,707),
                "panel_basura.png",
                new Point2D(0,0),
                animacionBasura,
                new Rectangle2D(298,158,80,118)
        );
        tareaBasura.setDuracionSegundos(8);
        tareaBasura.setTexturaFinal("basura_final.png");
        tareas[3] = tareaBasura;

        AnimationChannel animacionReactor = new AnimationChannel(
                FXGL.image("animacion_reactor.png"),
                6,1,1,Duration.seconds(1),0,5
        );

        int num = TareaReactor.generarCodigoAleatorio();
        String panel = "panel_codigo";
        String error = "error_codigo";
        String ultimo = "final_codigo";
        panel+= num;
        error+= num;
        ultimo+= num;

        panel+= ".png";
        error+= ".png";
        ultimo+= ".png";

        TareaReactor tareaReactor = new TareaReactor(6,
                "Introducir Codigo",
                new Point2D(162,116),
                panel,
                new Point2D(0,0),
                animacionReactor,
                new Rectangle2D(49,106,237,320),
                error
        );
        tareaReactor.asignarCodigo(num);
        tareaReactor.setDuracionSegundos(2);
        tareaReactor.setTexturaFinal(ultimo);
        tareas[4] = tareaReactor;

        return tareas;
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
        return new Point2D(585, 353);
    }

    @Override
    public List<NodoAlcantarilla> getRedAlcantarillas() {
        List<NodoAlcantarilla> red = new ArrayList<>();
        red.add(new NodoAlcantarilla(0,744,175,1,-1,-1,3));
        red.add(new NodoAlcantarilla(1,292,354,-1,0,-1,2));
        red.add(new NodoAlcantarilla(2,37,605,-1,3,1,-1));
        red.add(new NodoAlcantarilla(3,709,642,2,-1,0,-1));

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