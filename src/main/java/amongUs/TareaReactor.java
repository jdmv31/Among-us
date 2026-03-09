package main.java.amongUs;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import com.almasb.fxgl.texture.AnimationChannel;
import java.util.Random;

public class TareaReactor extends Tarea {

    private String codigoCorrecto;
    private String entradaActual = "";
    private Rectangle2D[] hitboxesTeclas;
    private String texturaError;

    public TareaReactor(int id, String nombre, Point2D ubicacion, String texturaFondo, Point2D posicionBoton, AnimationChannel canalAnimacion, Rectangle2D hitboxGeneral) {
        super(id, nombre, ubicacion, texturaFondo, posicionBoton, canalAnimacion, hitboxGeneral);
        this.hitboxesTeclas = new Rectangle2D[]{
          new Rectangle2D(51,110,70,70),
          new Rectangle2D(135,109,67,69),
          new Rectangle2D(213,107,69,74),
          new Rectangle2D(52,191,70,72),
          new Rectangle2D(133,191,71,67),
          new Rectangle2D(214,193,71,67),
          new Rectangle2D(52,274,72,68),
          new Rectangle2D(132,276,71,68),
          new Rectangle2D(215,374,70,70),
          new Rectangle2D(131,353,74,74)
        };
    }
    public int generarCodigoAleatorio() {
        int numero = (int) (Math.random() * 5 ) + 1;
        return numero;
    }

    public int intentarPulsarTecla(double x, double y) {
        if (tareaCompletada()) return 0;

        for (int i = 0; i < hitboxesTeclas.length; i++) {
            if (hitboxesTeclas[i].contains(x, y)) {
                int numeroPulsado = i + 1;
                entradaActual += numeroPulsado;
                if (!codigoCorrecto.startsWith(entradaActual)) {
                    entradaActual = "";
                    return -1;
                }

                if (entradaActual.equals(codigoCorrecto)) {
                    completar();
                    return 1;
                }
                return 0;
            }
        }
        return 0;
    }

    public String getCodigoCorrecto() {
        return codigoCorrecto;
    }
    public String getTexturaError() {
        return texturaError;
    }
}