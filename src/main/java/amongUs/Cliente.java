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
        cliente.start();

        try {
            cliente.connect(5000, ip, 54555,54556);
        } catch(IOException e) {
            System.err.println("Error al conectar al servidor: " + e.getMessage());
        }

        cliente.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Movimiento) {
                    Movimiento mov = (Movimiento) object;

                    javafx.application.Platform.runLater(() -> {
                        System.out.println(mov.username + " se movió a " + mov.x + "," + mov.y);
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
    }
}