package main.java.amongUs;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Listener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase principal del servidor basada en KryoNet
 * Se encarga de gestionar las conexiones, mantener el estado de la partida y retransmitir
 * los eventos (movimientos, asesinatos, votos) a todos los clientes
 * @author Angelo Martini
 * */
public class Servidor {
    Server server;
    /**
     * Lista que mantiene a todos los jugadores que estan actualmente en el lobby
     * Trabaja en conjunto con {@link JugadorLobby}.
     * */
    List<JugadorLobby> jugadoresLobby = new ArrayList<>();
    private java.util.Map<String, Integer> conteoVotos = new java.util.HashMap<>();
    private java.util.Set<String> jugadoresQueYaVotaron = new java.util.HashSet<>();
    private java.util.List<String> jugadoresMuertos = new java.util.ArrayList<>();
    private java.util.List<String> nombresImpostores = new java.util.ArrayList<>();

    /**
     * Arreglo con todos los colores posibles que pueden elegir los jugadores
     * */
    private final String[] COLORES_TOTALES = {
            "blanco", "negro", "marron", "azul", "rojo",
            "rosa", "verde", "amarillo", "morado", "naranja"
    };

    /**
     * Constructor del servidor
     * Registra todas las clases que se van a enviar por la red para que KryoNet sepa como serializarlas
     * Tambien levanta el servidor en los puertos 54555 y 54556 y configura los eventos de conexion
     * */
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
        server.getKryo().register(FinPartida.class);
        server.getKryo().register(DesconexionJugador.class);
        server.getKryo().register(VotoEmitido.class);
        server.getKryo().register(ResultadoVotacion.class);
        server.getKryo().register(java.util.HashMap.class);
        server.start();
        server.bind(54555, 54556);

        server.addListener(new Listener() {
            /**
             * Se dispara cuando un jugador cierra el juego o pierde conexion
             * Limpia al jugador del lobby y, si era el host, le pasa el host a otro
             * @param connection contiene los datos de red del jugador que acaba de desconectarse
             * */
            @Override
            public void disconnected(Connection connection) {
                String nombreDesconectado = null;
                for (JugadorLobby j : jugadoresLobby) {
                    if (j.conexionId == connection.getID()) {
                        nombreDesconectado = j.nombre;
                        break;
                    }
                }

                jugadoresLobby.removeIf(j -> j.conexionId == connection.getID());
                if(!jugadoresLobby.isEmpty() && jugadoresLobby.stream().noneMatch(j -> j.host)) {
                    jugadoresLobby.get(0).host = true; // Migracion de host si el que se fue era el lider
                }
                enviarEstadoLobby();

                if (nombreDesconectado != null) {
                    DesconexionJugador desc = new DesconexionJugador();
                    desc.nombreUsuario = nombreDesconectado;
                    server.sendToAllTCP(desc);
                }

                // Verificamos si la partida se quedo colgada en una votacion por la desconexion

                if (!jugadoresQueYaVotaron.isEmpty()) {
                    int vivosEsperados = jugadoresLobby.size() - jugadoresMuertos.size();
                    if (vivosEsperados > 0 && jugadoresQueYaVotaron.size() >= vivosEsperados) {
                        calcularResultadoVotacion();
                    }
                }
            }

            /**
             * El nucleo del servidor. Recibe cualquier paquete de datos y decide que hacer segun la clase del objeto
             * @param connection quien nos envia el paquete
             * @param object el paquete en si (puede ser un movimiento, un voto, un chat, etc)
             * */
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
                        nombresImpostores.clear();
                        java.util.Random rand = new java.util.Random();

                        int indiceImpostor1 = rand.nextInt(conexiones.length);
                        int indiceImpostor2 = -1;

                        if (conexiones.length > 1) {
                            do {
                                indiceImpostor2 = rand.nextInt(conexiones.length);
                            } while (indiceImpostor1 == indiceImpostor2);
                        }

                        for (int i = 0; i < conexiones.length; i++) {
                            if (i == indiceImpostor1 || i == indiceImpostor2) {
                                int idConexion = conexiones[i].getID();
                                for (JugadorLobby j : jugadoresLobby) {
                                    if (j.conexionId == idConexion) {
                                        nombresImpostores.add(j.nombre);
                                        break;
                                    }
                                }
                            }
                        }

                        for (int i = 0; i < conexiones.length; i++) {
                            AsignacionRol rol = new AsignacionRol();
                            rol.esImpostor = (i == indiceImpostor1 || i == indiceImpostor2);
                            rol.companeros = nombresImpostores.toArray(new String[0]);
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
                    Asesinato asesinato = (Asesinato) object;
                    jugadoresMuertos.add(asesinato.victima);
                    server.sendToAllTCP(object);

                    verificarVictoria(false);
                }
                if (object instanceof Sabotaje){
                    server.sendToAllExceptUDP(connection.getID(),object);
                }
                if (object instanceof PeticionReunion) {
                    PeticionReunion peticion = (PeticionReunion) object;
                    server.sendToAllTCP(peticion);
                }
                if (object instanceof MensajeChat) {
                    server.sendToAllTCP(object);
                }
                if (object instanceof VotoEmitido) {
                    VotoEmitido voto = (VotoEmitido) object;
                    jugadoresQueYaVotaron.add(voto.votante);
                    conteoVotos.put(voto.sospechoso, conteoVotos.getOrDefault(voto.sospechoso, 0) + 1);
                    server.sendToAllTCP(object);
                    int vivosEsperados = jugadoresLobby.size() - jugadoresMuertos.size();
                    if (jugadoresQueYaVotaron.size() >= vivosEsperados) {
                        calcularResultadoVotacion();
                    }
                }
            }
        });
    }

    /**
     * Procesa los votos almacenados una vez que todos los jugadores vivos terminan de votar
     * Identifica si hay un jugador mas votado, si hubo empate o si se skipeo
     * Al final, envia el paquete de {@link ResultadoVotacion} a todos
     * */
    private void calcularResultadoVotacion() {
        String masVotado = "SKIP";
        int maxVotos = 0;
        boolean empate = false;

        for (java.util.Map.Entry<String, Integer> entry : conteoVotos.entrySet()) {
            if (entry.getValue() > maxVotos) {
                maxVotos = entry.getValue();
                masVotado = entry.getKey();
                empate = false;
            } else if (entry.getValue() == maxVotos) {
                empate = true;
            }
        }

        ResultadoVotacion res = new ResultadoVotacion();
        if (empate || "SKIP".equals(masVotado)) {
            res.fueEmpateOSkip = true;
            res.expulsado = "Nadie";
            res.expulsadoEraImpostor = false;
        } else {
            res.fueEmpateOSkip = false;
            res.expulsado = masVotado;
            res.expulsadoEraImpostor = nombresImpostores.contains(masVotado);
            jugadoresMuertos.add(masVotado);
        }
        res.votosPorJugador = new java.util.HashMap<>(conteoVotos);
        server.sendToAllTCP(res);
        conteoVotos.clear();
        jugadoresQueYaVotaron.clear();

        verificarVictoria(true);
    }

    /**
     * Revisa si algun bando ya cumplio las condiciones para ganar la partida
     * @param fuePorVotacion booleano que nos indica si estamos verificando justo despues de expulsar a alguien
     * Sirve para mostrar la pantalla final con delay si hubo votacion
     * */
    private void verificarVictoria(boolean fuePorVotacion) {
        int tripulantesVivos = 0;
        int impostoresVivos = 0;

        for (JugadorLobby j : jugadoresLobby) {
            if (!jugadoresMuertos.contains(j.nombre)) {
                if (nombresImpostores.contains(j.nombre)) {
                    impostoresVivos++;
                } else {
                    tripulantesVivos++;
                }
            }
        }

        FinPartida fin = null;

        if (fuePorVotacion) {
            if (tripulantesVivos <= 1 && impostoresVivos >= 1) {
                fin = new FinPartida();
                fin.ganador = "IMPOSTORES";
            }
            // Si expulsaron a todos los impostores
            else if (impostoresVivos == 0) {
                fin = new FinPartida();
                fin.ganador = "TRIPULANTES";
            }
        } else {
            if (tripulantesVivos == 0) {
                fin = new FinPartida();
                fin.ganador = "IMPOSTORES";
            }
        }

        if (fin != null) {
            StringBuilder nombres = new StringBuilder();
            StringBuilder colores = new StringBuilder();
            for (JugadorLobby j : jugadoresLobby) {
                if (fin.ganador.equals("IMPOSTORES") && nombresImpostores.contains(j.nombre)) {
                    nombres.append(j.nombre).append(",");
                    colores.append(j.color).append(",");
                } else if (fin.ganador.equals("TRIPULANTES") && !nombresImpostores.contains(j.nombre)) {
                    nombres.append(j.nombre).append(",");
                    colores.append(j.color).append(",");
                }
            }
            fin.nombresCSV = nombres.toString();
            fin.coloresCSV = colores.toString();

            final FinPartida paqueteFinal = fin;
            if (fuePorVotacion) {
                new Thread(() -> {
                    try { Thread.sleep(9400); } catch (InterruptedException e) {} // Le damos tiempo a la animacion de eyeccion
                    server.sendToAllTCP(paqueteFinal);
                }).start();
            } else {
                server.sendToAllTCP(paqueteFinal);
            }
        }
    }

    /**
     * Busca el primer color que no este siendo usado por nadie en el lobby
     * @return un String con el nombre del color libre. Retorna "blanco" por defecto si hay un fallo.
     * */
    private String obtenerColorDisponible() {
        for (String color : COLORES_TOTALES) {
            boolean enUso = jugadoresLobby.stream().anyMatch(j -> j.color.equals(color));
            if (!enUso) { return color; }
        }
        return "blanco";
    }

    /**
     * Construye un objeto con la lista actual de jugadores y la envia a todos los clientes
     * Se usa cada vez que alguien entra, sale o cambia de color
     * */
    private void enviarEstadoLobby() {
        EstadoLobby estado = new EstadoLobby();
        estado.jugadores = jugadoresLobby.toArray(new JugadorLobby[0]);
        server.sendToAllTCP(estado);
    }

    /**
     * Metodo de arranque principal.
     * @param args argumentos de linea de comandos
     * */
    public static void main(String[] args) {
        try {
            new Servidor();
            System.out.println("Servidor iniciado");
        } catch(Exception e) {
            System.out.println("Error iniciando el servidor...");
        }
    }
}