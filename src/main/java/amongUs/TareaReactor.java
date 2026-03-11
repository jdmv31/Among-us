package main.java.amongUs;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import com.almasb.fxgl.texture.AnimationChannel;
import java.util.Random;

/**
 * Representa la tarea especifica de arreglar o desbloquear el reactor mediante un teclado numerico
 * Hereda de {@link Tarea} y maneja la logica interna de un minijuego donde el jugador debe ingresar un codigo
 * @author Angelo Martini
 * */
public class TareaReactor extends Tarea {

    private String codigoCorrecto;
    private String entradaActual = "";
    private Rectangle2D[] hitboxesTeclas;
    private String texturaError;

    /**
     * Constructor que prepara la tarea del reactor y define las posiciones de cada numero del teclado en la pantalla
     * Llama al constructor padre para la configuracion basica y ademas guarda la imagen de fallo
     * @param id numero unico para identificar esta tarea en la lista del jugador
     * @param nombre texto que aparece en la interfaz, por ejemplo "Desbloquear multiples"
     * @param ubicacion coordenadas en el mapa del juego para saber donde esta el panel
     * @param texturaFondo imagen principal del panel del reactor
     * @param posicionBoton donde colocamos el boton para empezar a jugar
     * @param canalAnimacion frames de animacion si el panel tiene luces o movimiento
     * @param hitboxGeneral la zona donde el jugador clickea para abrir la interfaz de la tarea
     * @param texturaError la imagen roja o de alerta que le salta al jugador cuando se equivoca de numero
     * */
    public TareaReactor(int id, String nombre, Point2D ubicacion, String texturaFondo, Point2D posicionBoton, AnimationChannel canalAnimacion, Rectangle2D hitboxGeneral,String texturaError) {
        super(id, nombre, ubicacion, texturaFondo, posicionBoton, canalAnimacion, hitboxGeneral);

        // Mapeo exacto de los botones del 0 al 9 en el dibujo del teclado

        this.hitboxesTeclas = new Rectangle2D[]{
                new Rectangle2D(131,353,74,74),
                new Rectangle2D(51,110,70,70),
                new Rectangle2D(135,109,67,69),
                new Rectangle2D(213,107,69,74),
                new Rectangle2D(52,191,70,72),
                new Rectangle2D(133,191,71,67),
                new Rectangle2D(214,193,71,67),
                new Rectangle2D(52,274,72,68),
                new Rectangle2D(132,276,71,68),
                new Rectangle2D(215,275,70,70)
        };
        this.texturaError = texturaError;
    }

    /**
     * Selecciona de forma aleatoria cual de los 5 codigos configurados se usara en esta ronda
     * @return un numero entero al azar entre el 1 y el 5
     * */
    public static int generarCodigoAleatorio() {
        int numero = (int) (Math.random() * 5 ) + 1;
        return numero;
    }

    /**
     * Agarra el numero aleatorio generado previamente y le asigna su respectivo codigo secreto de 5 digitos
     * Este es el codigo exacto que el tripulante tendra que tipear sin equivocarse
     * @param numero el valor del 1 al 5 que determina que secuencia usar para esta ronda
     * */
    public void asignarCodigo(int numero){
        switch (numero){
            case 1:
                codigoCorrecto = "30647";
                break;

            case 2:
                codigoCorrecto = "91820";
                break;

            case 3:
                codigoCorrecto = "54093";
                break;

            case 4:
                codigoCorrecto = "76218";
                break;

            case 5:
                codigoCorrecto = "13579";
                break;
        }
    }

    /**
     * Detecta donde hizo clic el jugador y verifica si le dio a algun boton del teclado
     * Va armando la secuencia de lo que el jugador teclea y la compara con el codigo correcto en tiempo real
     * Si el jugador se equivoca en un solo digito, la secuencia se borra y tiene que empezar de cero
     * @param x la coordenada horizontal exacta del clic del mouse en la pantalla
     * @param y la coordenada vertical exacta del clic del mouse en la pantalla
     * @return devuelve 1 si el jugador logro meter el codigo bien, -1 si metio un numero equivocado, o 0 si le dio a un boton correcto pero aun le faltan numeros
     * */
    public int intentarPulsarTecla(double x, double y) {
        if (tareaCompletada()) return 0;

        for (int i = 0; i < hitboxesTeclas.length; i++) {
            if (hitboxesTeclas[i].contains(x, y)) {
                entradaActual += i;

                // Si lo que lleva escrito no coincide con el inicio del codigo real, fallo

                if (!codigoCorrecto.startsWith(entradaActual)) {
                    entradaActual = "";
                    return -1;
                }
                System.out.println(entradaActual);

                // Si la longitud y los numeros son iguales, gano el minijuego

                if (entradaActual.equals(codigoCorrecto))
                    return 1;

                return 0; // Va bien, pero le faltan digitos
            }
        }
        return 0; // Clickeo fuera de cualquier tecla
    }

    /**
     * @return el texto literal con los 5 numeros que el jugador tiene que lograr adivinar o leer
     * */
    public String getCodigoCorrecto() {
        return codigoCorrecto;
    }

    /**
     * @return el nombre de la imagen que mostramos temporalmente cuando el jugador arruina la secuencia
     * */
    public String getTexturaError() {
        return texturaError;
    }
}