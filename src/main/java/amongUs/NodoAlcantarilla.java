package main.java.amongUs;
/**
 * Representa un nodo individual dentro del sistema de alcantarillas (ductos de ventilación) del mapa.
 * Estructura la información espacial del nodo y sus conexiones directas con otras alcantarillas,
 * permitiendo modelar la red por la que se desplaza el Impostor.
 *
 * @author Sebastián Arismendi
 */

public class NodoAlcantarilla {

    /**
     * coordenadas de la alcantarilla en el eje x y el eje y del mapa
     */
    public double x;
    public double y;

    /** identificador unico de la alcantarilla
     */
    public int id;

    /** id de la posición de la alcantarilla conectada a izquierda, derecha, arriba y abajo (-1 si no hay conexión)
     */

    public int ventIzquierda, ventDerecha, ventArriba, ventAbajo;

    /**
     * Constructor de la clase NodoAlcantarilla
     * @param id identificador unico del nodo
     * @param x posición en el eje x
     * @param y posición en el eje y
     * @param ventIzq ID del nodo conectado a la izquierda.
     * @param ventDer ID del nodo conectado a la derecha.
     * @param ventArriba ID del nodo conectado arriba.
     * @param ventAbajo ID del nodo conectado abajo.
     */
    public NodoAlcantarilla(int id, double x, double y, int ventIzq, int ventDer, int ventArriba, int ventAbajo) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.ventIzquierda = ventIzq;
        this.ventDerecha = ventDer;
        this.ventArriba = ventArriba;
        this.ventAbajo = ventAbajo;
    }
}