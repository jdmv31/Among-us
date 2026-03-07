package main.java.amongUs;

public class NodoAlcantarilla {
    public double x;
    public double y;
    public int id;
    int ventIzquierda, ventDerecha, ventArriba, ventAbajo;

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