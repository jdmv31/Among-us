package main.java.amongUs;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;

public class Cliente {
    public Client cliente;
    public String username;

    public Cliente(String ip, String username) {
        this.username = username;
        cliente = new Client();
        cliente.getKryo().register(Movimiento.class);
        cliente.getKryo().register(MapaElegido.class);
        cliente.getKryo().register(PeticionUnirse.class);
        cliente.getKryo().register(JugadorLobby.class);
        cliente.getKryo().register(JugadorLobby[].class);
        cliente.getKryo().register(EstadoLobby.class);
        cliente.getKryo().register(PeticionColor.class);
        cliente.start();

        cliente.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof EstadoLobby) {
                    EstadoLobby estado = (EstadoLobby) object;
                    javafx.application.Platform.runLater(() -> {
                        if (MenuController.instancia != null) {
                            MenuController.instancia.actualizarLobby(estado);
                        }
                    });
                }
                if (object instanceof Movimiento) {
                    Movimiento mov = (Movimiento) object;

                    javafx.application.Platform.runLater(() -> {
                        if (!mov.username.equals(MenuController.nombreUsuario)) {
                            com.almasb.fxgl.entity.Entity otro = AppPrincipal.otrosJugadores.get(mov.username);

                            if (otro != null) {
                                com.almasb.fxgl.physics.PhysicsComponent fisicas = otro.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class);

                                if (fisicas != null) {
                                    fisicas.overwritePosition(new javafx.geometry.Point2D(mov.x, mov.y));
                                } else {
                                    otro.setPosition(mov.x, mov.y);
                                }
                            }
                        }
                    });
                }
                else if (object instanceof MapaElegido) {
                    MapaElegido paquete = (MapaElegido) object;
                    javafx.application.Platform.runLater(() -> {
                        AppPrincipal.empezarPartida(paquete.nombreMapa);
                    });
                }
            }
        });
        try {
            cliente.connect(5000, ip, 54555, 54556);
            PeticionUnirse peticion = new PeticionUnirse();
            peticion.nombre = this.username;
            cliente.sendTCP(peticion);
        } catch(IOException e) {
            System.err.println("Error al conectar al servidor: " + e.getMessage());
        }
    }
}