package main.java.amongUs;

import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;

import java.util.Scanner;
import java.io.IOException;

public class Cliente {
    public Client cliente;

    public Cliente(){
        cliente = new Client();
        cliente.getKryo().register(Mensaje.class);
        cliente.start();
        try {
            cliente.connect(5000, "127.0.0.1", 54555);
        }
        catch(IOException e){
            System.out.println("Error de ejecucion");
        }
        cliente.addListener(new Listener(){
            @Override
            public void received(Connection connection, Object object){
                if (object instanceof Mensaje){
                    Mensaje msj = (Mensaje) object;
                    System.out.println("["+msj.username+"]: "+msj.mensaje);
                }
            }
        });
        Mensaje miMensaje = new Mensaje();
        miMensaje.username = "";
        miMensaje.mensaje = "";
        cliente.sendTCP(miMensaje);
    }


    public static void main (String [] args) throws Exception{
        Scanner sc = new Scanner(System.in);
        Cliente cliente = new Cliente();
        System.out.println("Ingrese su nombre de usuario:");
        String user = sc.nextLine();

        while(true){
            System.out.print(">");
            Mensaje mensaje = new Mensaje();
            mensaje.mensaje = sc.nextLine();
            mensaje.username = user;
            cliente.cliente.sendTCP(mensaje);
        }

    }

}
