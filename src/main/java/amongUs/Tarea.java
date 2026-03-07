package main.java.amongUs;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D; // NUEVA IMPORTACIÓN
import com.almasb.fxgl.texture.AnimationChannel;

public class Tarea {
    private int id;
    private String nombre;
    private boolean completada;
    private Point2D ubicacion;

    private String texturaFondo;
    private Point2D posicionBoton;
    private AnimationChannel canalAnimacion;
    private Rectangle2D hitboxClic; // NUESTRA ZONA DE FUEGO

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

    public boolean tareaCompletada() { return completada; }
    public void completar() { this.completada = true; }
    public String getNombre() { return nombre; }
    public Point2D getUbicacion() { return ubicacion; }

    public String getTexturaFondo() { return texturaFondo; }
    public Point2D getPosicionBoton() { return posicionBoton; }
    public AnimationChannel getCanalAnimacion() { return canalAnimacion; }
    public Rectangle2D getHitboxClic() { return hitboxClic; } // GETTER
}