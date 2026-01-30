package amongUs;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Listener;


public class Servidor {
    Server server;

    public Servidor() throws Exception{
        server = new Server();
        server.getKryo().register(Mensaje.class);
        server.start();
        server.bind(54555);

        server.addListener(new Listener(){
            @Override
            public void received(Connection connection,Object object){
                if (object instanceof Mensaje){
                    Mensaje msj = (Mensaje) object;
                    System.out.println("Mensaje enviado por: "+msj.username+"\n\n"+msj.mensaje);
                    server.sendToAllTCP(object);
                }
            }
        });
    }

    public static void main(String[]args){
        try{
            new Servidor();
            System.out.println("Servidor iniciado");
        }catch(Exception e){
            System.out.println("Error iniciando el servidor...");
        }
    }


}
