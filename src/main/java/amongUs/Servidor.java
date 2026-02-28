package main.java.amongUs;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Listener;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    Server server;
    List<JugadorLobby> jugadoresLobby = new ArrayList<>();


    public Servidor() throws Exception {
        server = new Server();
        server.getKryo().register(Movimiento.class);
        server.getKryo().register(MapaElegido.class);
        server.getKryo().register(PeticionUnirse.class);
        server.getKryo().register(JugadorLobby.class);
        server.getKryo().register(JugadorLobby[].class);
        server.getKryo().register(EstadoLobby.class);
        server.getKryo().register(PeticionColor.class);
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
                    nuevo.color = "#negro";
                    nuevo.host = jugadoresLobby.isEmpty(); // El primero que entra es el host

                    jugadoresLobby.add(nuevo);
                    enviarEstadoLobby();
                }
                else if (object instanceof PeticionColor) {
                    PeticionColor peticion = (PeticionColor) object;
                    for (JugadorLobby j : jugadoresLobby) {
                        if (j.conexionId == connection.getID()) {
                            j.color = peticion.color;
                            break;
                        }
                    }
                    enviarEstadoLobby();
                }
                else if (object instanceof MapaElegido) {
                    // josue: modificar aca para hacer pruebas
                    if (jugadoresLobby.size() >= 5) {
                        server.sendToAllTCP(object);
                    } else {
                        System.out.println("Intento de inicio denegado. Jugadores actuales: " + jugadoresLobby.size());
                    }
                }
                else if (object instanceof Movimiento) {
                    server.sendToAllExceptUDP(connection.getID(), object);
                }
            }
        });
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