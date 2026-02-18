package main.java.amongUs;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;

public class Cliente {
    public Client cliente;
    public String username;

    Cliente(){
        cliente = null;
        username = "";
    }

    public Cliente(String ip, String username) {
        this.username = username;
        cliente = new Client();
        cliente.getKryo().register(Mensaje.class);
        cliente.start();

        try {
            cliente.connect(5000, ip, 54555);
        } catch(IOException e) {
            System.err.println("Error al conectar al servidor: " + e.getMessage());
        }

        cliente.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Movimiento) {
                    Movimiento mov = (Movimiento) object;

                    javafx.application.Platform.runLater(() -> {
                        // Aquí debes buscar la entidad que corresponde a "mov.username"
                        // y actualizar sus coordenadas.
                        // Ejemplo conceptual:
                        // Entity otroJugador = buscarJugadorPorNombre(mov.username);
                        // otroJugador.setPosition(mov.x, mov.y);
                    });
                }
            }
        });
    }



    public void enviarMensaje(String texto) {
        Mensaje msj = new Mensaje();
        msj.username = this.username;
        msj.mensaje = texto;
        cliente.sendTCP(msj);
    }
}