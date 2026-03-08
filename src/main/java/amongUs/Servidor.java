package main.java.amongUs;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Listener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Servidor {
    Server server;
    List<JugadorLobby> jugadoresLobby = new ArrayList<>();

    private final String[] COLORES_TOTALES = {
            "blanco", "negro", "marron", "azul", "rojo",
            "rosa", "verde", "amarillo", "morado", "naranja"
    };

    public Servidor() throws Exception {
        server = new Server();
        server.getKryo().register(Movimiento.class);
        server.getKryo().register(MapaElegido.class);
        server.getKryo().register(PeticionUnirse.class);
        server.getKryo().register(JugadorLobby.class);
        server.getKryo().register(JugadorLobby[].class);
        server.getKryo().register(EstadoLobby.class);
        server.getKryo().register(PeticionColor.class);
        server.getKryo().register(AsignacionRol.class);
        server.getKryo().register(MovimientoAlcantarilla.class);
        server.getKryo().register(Asesinato.class);
        server.getKryo().register(Sabotaje.class);
        server.getKryo().register(PeticionReunion.class);
        server.getKryo().register(MensajeChat.class);
        server.start();
        server.bind(54555, 54556);

        server.addListener(new Listener() {
            @Override
            public void disconnected(Connection connection) {
                jugadoresLobby.removeIf(j -> j.conexionId == connection.getID());

                if(!jugadoresLobby.isEmpty() && jugadoresLobby.stream().noneMatch(j -> j.host)) {
                    jugadoresLobby.get(0).host = true;
                }
                enviarEstadoLobby();
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PeticionUnirse) {
                    PeticionUnirse peticion = (PeticionUnirse) object;
                    JugadorLobby nuevo = new JugadorLobby();
                    nuevo.conexionId = connection.getID();
                    nuevo.nombre = peticion.nombre;
                    nuevo.color = obtenerColorDisponible();
                    nuevo.host = jugadoresLobby.isEmpty();

                    jugadoresLobby.add(nuevo);
                    enviarEstadoLobby();
                }
                else if (object instanceof PeticionColor) {
                    PeticionColor peticion = (PeticionColor) object;
                    boolean colorOcupado = jugadoresLobby.stream()
                            .anyMatch(j -> j.color.equals(peticion.color));

                    if (!colorOcupado) {
                        for (JugadorLobby j : jugadoresLobby) {
                            if (j.conexionId == connection.getID()) {
                                j.color = peticion.color;
                                break;
                            }
                        }
                        enviarEstadoLobby();
                    }
                }
                else if (object instanceof MapaElegido) {
                    Connection[] conexiones = server.getConnections();

                    if (conexiones.length > 0) {
                        Random rand = new Random();
                        int indiceImpostor = rand.nextInt(conexiones.length);
                        for (int i = 0; i < conexiones.length; i++) {
                            AsignacionRol rol = new AsignacionRol();
                            rol.esImpostor = (i == indiceImpostor);

                            server.sendToTCP(conexiones[i].getID(), rol);
                        }
                    }
                    server.sendToAllTCP(object);
                }
                if (object instanceof Movimiento) {
                    server.sendToAllExceptUDP(connection.getID(), object);
                }
                if (object instanceof MovimientoAlcantarilla){
                    server.sendToAllExceptTCP(connection.getID(),object);
                }
                if (object instanceof Asesinato){
                    server.sendToAllTCP(object);
                }
                if (object instanceof Sabotaje){
                    server.sendToAllExceptUDP(connection.getID(),object);
                }
                if (object instanceof PeticionReunion) {
                    PeticionReunion peticion = (PeticionReunion) object;
                    System.out.println("Reunión solicitada por " + peticion.reportador);
                    server.sendToAllTCP(peticion);
                }
                if (object instanceof MensajeChat) {
                    server.sendToAllTCP(object);
                }
            }
        });
    }

    private String obtenerColorDisponible() {
        for (String color : COLORES_TOTALES) {
            boolean enUso = jugadoresLobby.stream().anyMatch(j -> j.color.equals(color));
            if (!enUso) {
                return color;
            }
        }
        return "blanco";
    }

    private void enviarEstadoLobby() {
        EstadoLobby estado = new EstadoLobby();
        estado.jugadores = jugadoresLobby.toArray(new JugadorLobby[0]);
        server.sendToAllTCP(estado);
    }

    public static void main(String[] args) {
        try {
            new Servidor();
            System.out.println("Servidor iniciado");
        } catch(Exception e) {
            System.out.println("Error iniciando el servidor...");
        }
    }
}