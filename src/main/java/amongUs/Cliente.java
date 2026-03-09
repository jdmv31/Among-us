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
        cliente.getKryo().register(AsignacionRol.class);
        cliente.getKryo().register(MovimientoAlcantarilla.class);
        cliente.getKryo().register(Asesinato.class);
        cliente.getKryo().register(Sabotaje.class);
        cliente.getKryo().register(PeticionReunion.class);
        cliente.getKryo().register(MensajeChat.class);
        cliente.getKryo().register(FinPartida.class);
        cliente.getKryo().register(DesconexionJugador.class);
        cliente.getKryo().register(VotoEmitido.class);
        cliente.getKryo().register(ResultadoVotacion.class);
        cliente.getKryo().register(java.util.HashMap.class);
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
                                if (otro.hasComponent(com.almasb.fxgl.physics.PhysicsComponent.class)) {
                                    com.almasb.fxgl.physics.PhysicsComponent fisicas = otro.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class);
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
                if (object instanceof AsignacionRol) {
                    AsignacionRol rolAsignado = (AsignacionRol) object;
                    AppPrincipal.esImpostor = rolAsignado.esImpostor;
                    System.out.println("Impostor: " + AppPrincipal.esImpostor);
                }
                if (object instanceof Asesinato) {
                    Asesinato paquete = (Asesinato) object;

                    javafx.application.Platform.runLater(() -> {
                        if (paquete.victima.equals(MenuController.nombreUsuario)) {
                            AppPrincipal.estoyMuerto = true;
                            if (AppPrincipal.sistemaCamaras.isCamarasAbiertas()) {
                                com.almasb.fxgl.dsl.FXGL.getInput().mockKeyPress(javafx.scene.input.KeyCode.C);
                                com.almasb.fxgl.dsl.FXGL.getInput().mockKeyRelease(javafx.scene.input.KeyCode.C);
                            }

                            if (AppPrincipal.jugador != null) {
                                String miColor = AppPrincipal.jugador.getComponent(AnimacionJugador.class).getColor();
                                com.almasb.fxgl.dsl.FXGL.entityBuilder()
                                        .at(AppPrincipal.jugador.getX(), AppPrincipal.jugador.getY())
                                        .view(miColor + "_muerto.png")
                                        .scale(1.6, 1.6)
                                        .zIndex((int) (AppPrincipal.jugador.getY() + (32 * 1.8)))
                                        .buildAndAttach();
                                AppPrincipal.jugador.getComponent(AnimacionJugador.class).convertirFantasma();
                                AppPrincipal.jugador.getViewComponent().setOpacity(0.5);
                            }
                        } else {
                            com.almasb.fxgl.entity.Entity victima = AppPrincipal.otrosJugadores.get(paquete.victima);
                            if (victima != null) {
                                victima.getComponent(AnimacionJugador.class).morir();
                                if (victima.hasComponent(com.almasb.fxgl.physics.PhysicsComponent.class)) {
                                    victima.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityX(0);
                                    victima.getComponent(com.almasb.fxgl.physics.PhysicsComponent.class).setVelocityY(0);
                                }
                            }
                        }
                    });
                    int vivos = 0;
                    if (!AppPrincipal.estoyMuerto) vivos++;

                    for (com.almasb.fxgl.entity.Entity otro : AppPrincipal.otrosJugadores.values()) {
                        AnimacionJugador anim = otro.getComponent(AnimacionJugador.class);
                        if (anim != null && !anim.estaMuerto) vivos++;
                    }

                    if (vivos <= 1 && AppPrincipal.esImpostor) {
                        FinPartida fin = new FinPartida();
                        fin.ganador = "IMPOSTORES";
                        AppPrincipal.miCliente.cliente.sendTCP(fin);
                    }
                }
                if (object instanceof MovimientoAlcantarilla){
                    MovimientoAlcantarilla movAlcantarilla = (MovimientoAlcantarilla) object;

                    javafx.application.Platform.runLater(() -> {
                        com.almasb.fxgl.entity.Entity otroJugador = AppPrincipal.otrosJugadores.get(movAlcantarilla.nombreUsuario);

                        if (otroJugador != null) {
                            if (movAlcantarilla.entrando) {
                                otroJugador.getComponent(AnimacionJugador.class).entrarAlcantarilla();
                                com.almasb.fxgl.dsl.FXGL.getGameTimer().runOnceAfter(() -> {
                                    otroJugador.getViewComponent().setVisible(false);
                                }, javafx.util.Duration.seconds(0.5));

                            } else {
                                otroJugador.getViewComponent().setVisible(true);
                                otroJugador.getComponent(AnimacionJugador.class).salirAlcantarilla();
                            }
                        }
                    });
                }else if (object instanceof Sabotaje){
                    AppPrincipal.peticionSabotaje = true;
                }
                if (object instanceof PeticionReunion) {
                    PeticionReunion peticion = (PeticionReunion) object;
                    javafx.application.Platform.runLater(() -> {
                        if (AppPrincipal.sistemaCamaras != null && AppPrincipal.sistemaCamaras.isCamarasAbiertas()) {
                            AppPrincipal.sistemaCamaras.forzarCierre(AppPrincipal.jugador);
                        }
                        AppPrincipal.iniciarCinematicaReporte(peticion.reportador, peticion.cadaver);
                    });
                }
                if (object instanceof MensajeChat) {
                    MensajeChat msg = (MensajeChat) object;
                    javafx.application.Platform.runLater(() -> {
                        if (ReunionController.instancia != null) {
                            if (msg.esFantasma && !AppPrincipal.estoyMuerto)
                                return;
                            ReunionController.instancia.agregarMensaje(msg.emisor, msg.mensaje, msg.esFantasma);
                        }
                    });
                }
                if (object instanceof FinPartida) {
                    FinPartida fin = (FinPartida) object;
                    javafx.application.Platform.runLater(() -> {
                        AppPrincipal.mostrarPantallaFin(fin.ganador);
                    });
                }
                if (object instanceof DesconexionJugador) {
                    DesconexionJugador desc = (DesconexionJugador) object;
                    javafx.application.Platform.runLater(() -> {
                        if (!desc.nombreUsuario.equals(MenuController.nombreUsuario)) {
                            AppPrincipal.removerJugador(desc.nombreUsuario);
                        }
                    });
                }
                if (object instanceof ResultadoVotacion) {
                    ResultadoVotacion res = (ResultadoVotacion) object;
                    javafx.application.Platform.runLater(() -> {
                        if (ReunionController.instancia != null) {
                            ReunionController.instancia.mostrarResultados(res);
                        }
                    });
                }
                if (object instanceof PeticionReunion) {
                    PeticionReunion peticion = (PeticionReunion) object;

                    javafx.application.Platform.runLater(() -> {
                        if (peticion.porBotonEmergencia) {
                            AppPrincipal.iniciarCinematicaEmergencia(peticion.reportador);
                        } else {
                            AppPrincipal.iniciarCinematicaReporte(peticion.reportador, "Desconocido");
                        }
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