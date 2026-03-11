package main.java.amongUs;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D; // NUEVA IMPORTACION
import com.almasb.fxgl.texture.AnimationChannel;

/**
 * Representa una mision o tarea individual que los tripulantes deben resolver en el mapa
 * Guarda toda la informacion necesaria tanto para su ubicacion fisica en el juego
 * como para la interfaz visual que aparece al intentar completarla
 * @author Angelo Martini
 * */
public class Tarea {
    private int id;
    private String nombre;
    private boolean completada;
    private Point2D ubicacion;

    private String texturaFondo;
    private Point2D posicionBoton;
    private AnimationChannel canalAnimacion;
    private Rectangle2D hitboxClic;
    private double duracionSegundos;
    private String texturaFinal;

    /**
     * Constructor principal que arma la estructura basica de una tarea
     * Por defecto, toda tarea arranca con su estado "completada" en false
     * * @param id numero unico para identificar la tarea internamente y no confundirla con otras
     * @param nombre texto descriptivo que aparece en la lista de misiones
     * @param ubicacion las coordenadas {@link Point2D} exactas en el mapa donde el jugador debe acercarse
     * @param texturaFondo nombre del archivo de imagen que sirve como fondo de la pantalla del minijuego
     * @param posicionBoton lugar especifico dentro de la interfaz donde se dibuja el boton interactivo
     * @param canalAnimacion contiene la secuencia de frames de {@link AnimationChannel} si la tarea tiene alguna animacion
     * @param hitboxClic el area rectangular invisible donde el jugador debe hacer clic para avanzar o terminar
     * */
    public Tarea(int id, String nombre, Point2D ubicacion, String texturaFondo, Point2D posicionBoton, AnimationChannel canalAnimacion, Rectangle2D hitboxClic) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.texturaFondo = texturaFondo;
        this.posicionBoton = posicionBoton;
        this.canalAnimacion = canalAnimacion;
        this.hitboxClic = hitboxClic;
        this.completada = false;
    }

    /**
     * Define la imagen estatica que se mostrara justo despues de terminar la accion de la tarea
     * @param texturaF el nombre del archivo de la imagen final
     * */
    public void setTexturaFinal(String texturaF){this.texturaFinal = texturaF;}

    /**
     * Establece cuanto tiempo debe durar la tarea o su animacion antes de darse por terminada
     * @param segundos cantidad de tiempo requerida en pantalla
     * */
    public void setDuracionSegundos(double segundos){this.duracionSegundos = segundos;}

    /**
     * @return el nombre del archivo de la imagen que queda al terminar la tarea
     * */
    public String getTexturaFinal(){return this.texturaFinal;}

    /**
     * @return el tiempo en segundos que toma el proceso de la tarea
     * */
    public double getDuracionSegundos(){return this.duracionSegundos;}

    /**
     * Revisa si el jugador ya termino esta mision especifica o si la sigue teniendo pendiente
     * @return true si ya la resolvio, false si falta hacerla
     * */
    public boolean tareaCompletada() { return completada; }

    /**
     * Cambia el estado interno de la tarea para darla por hecha permanentemente
     * Normalmente se llama despues de que el usuario interactua correctamente con los clics
     * */
    public void completar() { this.completada = true; }

    /**
     * @return el nombre visible de la tarea para armar la lista en la UI del jugador
     * */
    public String getNombre() { return nombre; }

    /**
     * @return las coordenadas fisicas de la tarea dentro del mundo del juego
     * */
    public Point2D getUbicacion() { return ubicacion; }

    /**
     * @return el nombre del asset o imagen que usamos de fondo para esta mision
     * */
    public String getTexturaFondo() { return texturaFondo; }

    /**
     * @return las coordenadas internas de la UI donde colocamos el boton de accion
     * */
    public Point2D getPosicionBoton() { return posicionBoton; }

    /**
     * @return el canal con los frames de animacion configurados para darle vida a este minijuego
     * */
    public AnimationChannel getCanalAnimacion() { return canalAnimacion; }

    /**
     * @return el area de colision Rectangle2D preparada para detectar donde presiona el mouse el jugador
     * */
    public Rectangle2D getHitboxClic() { return hitboxClic; }
}