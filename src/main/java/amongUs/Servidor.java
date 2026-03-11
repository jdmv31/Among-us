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
    private java.util.Map<String, Integer> conteoVotos = new java.util.HashMap<>();
    private java.util.Set<String> jugadoresQueYaVotaron = new java.util.HashSet<>();
    private java.util.List<String> jugadoresMuertos = new java.util.ArrayList<>();
    private java.util.List<String> nombresImpostores = new java.util.ArrayList<>();

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
        server.getKryo().register(FinPartida.class);
        server.getKryo().register(DesconexionJugador.class);
        server.getKryo().register(VotoEmitido.class);
        server.getKryo().register(ResultadoVotacion.class);
        server.getKryo().register(java.util.HashMap.class);
        server.start();
        server.bind(54555, 54556);

        server.addListener(new Listener() {
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
                    jugadoresLobby.get(0).host = true;
                }
                enviarEstadoLobby();

                if (nombreDesconectado != null) {
                    DesconexionJugador desc = new DesconexionJugador();
                    desc.nombreUsuario = nombreDesconectado;
                    server.sendToAllTCP(desc);
                }

                if (!jugadoresQueYaVotaron.isEmpty()) {
                    int vivosEsperados = jugadoresLobby.size() - jugadoresMuertos.size();
                    if (vivosEsperados > 0 && jugadoresQueYaVotaron.size() >= vivosEsperados) {
                        calcularResultadoVotacion();
                    }
                }
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
                        nombresImpostores.clear();
                        Random rand = new Random();
                        int indiceImpostor = rand.nextInt(conexiones.length);
                        for (int i = 0; i < conexiones.length; i++) {
                            AsignacionRol rol = new AsignacionRol();
                            rol.esImpostor = (i == indiceImpostor);

                            if (rol.esImpostor) {
                                int idConexion = conexiones[i].getID();
                                for (JugadorLobby j : jugadoresLobby) {
                                    if (j.conexionId == idConexion) {
                                        nombresImpostores.add(j.nombre);
                                        break;
                                    }
                                }
                            }

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
                    int vivosEsperados = jugadoresLobby.size() - jugadoresMuertos.size();
                    if (jugadoresQueYaVotaron.size() >= vivosEsperados) {
                        calcularResultadoVotacion();
                    }
                }
            }
        });
    }

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
                    try { Thread.sleep(9400); } catch (InterruptedException e) {}
                    server.sendToAllTCP(paqueteFinal);
                }).start();
            } else {
                server.sendToAllTCP(paqueteFinal);
            }
        }
    }

    private String obtenerColorDisponible() {
        for (String color : COLORES_TOTALES) {
            boolean enUso = jugadoresLobby.stream().anyMatch(j -> j.color.equals(color));
            if (!enUso) { return color; }
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