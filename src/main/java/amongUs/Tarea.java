package main.java.amongUs;

public class Tarea {
    private int id;
    private String nombre;
    private boolean completada;

    public Tarea(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.completada = false;
    }

    public boolean tareaCompletada() {
        return completada;
    }

    public void completar() {
        this.completada = true;
    }

    public String getNombre() {
        return nombre;
    }
}