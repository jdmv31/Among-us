package main.java.amongUs;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Listener;

public class Servidor {
    Server server;

    public Servidor() throws Exception {
        server = new Server();
        server.getKryo().register(Movimiento.class);
        server.getKryo().register(MapaElegido.class);
        server.start();
        server.bind(54555, 54556);

        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Movimiento) {
                    server.sendToAllExceptUDP(connection.getID(), object);
                }
            }
        });
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