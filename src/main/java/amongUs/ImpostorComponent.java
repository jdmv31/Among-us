package main.java.amongUs;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.Map;

/**
 * Componente de lógica avanzada que define las capacidades especiales de un Impostor.
 * Esta clase centraliza las tres mecánicas principales del antagonista:
 * Asesinato: Detección de víctimas por proximidad y ejecución sincronizada por red.
 * Sabotaje: Activación de eventos globales (como corte eléctrico) con tiempo de recarga.
 * Ventilación: Sistema de desplazamiento rápido e invisibilidad a través de una red de nodos.
 * @author Angel Aguilera
 */
public class ImpostorComponent extends Component {
    private boolean matarDisponible = false;
    private boolean cooldownActivo = false;
    private double tiempoCooldown = 0.0;
    private Texture botonMatar;
    private Text textoCooldown;
    private String victimaCercana = "";
    private boolean enAlcantarilla = false;
    private int alcantarillaActual = -1;
    /** Entidades visuales (flechas) para la navegación entre rejillas. */
    private Entity ventflechaIzq, ventflechaAbajo, ventflechaArriba, ventflechaDer;
    private Texture botonSabotaje;
    private Text textoCooldownSabotaje;
    private boolean sabotajeDisponible = true;
    private double tiempoCooldownSabotaje = 0.0;

    /**
     * Configura los elementos de la Interfaz de Usuario necesarios para la acción de asesinato.
     * @param botonMatar Objeto {@link Texture} que representa el botón de "Kill".
     * @param textoCooldown Objeto {@link Text} donde se renderiza la cuenta regresiva.
     */
    public void setUIAsesinato(Texture botonMatar, Text textoCooldown) {
        this.botonMatar = botonMatar;
        this.textoCooldown = textoCooldown;
        iniciarCooldown(10.0);
    }

    /**
     * Configura los elementos de la Interfaz de Usuario para la acción de sabotaje.
     * @param botonSabotaje Textura interactiva para sabotear.
     * @param textoCooldownSabotaje Etiqueta de texto para el tiempo de recarga del sabotaje.
     */
    public void setUISabotaje(Texture botonSabotaje, Text textoCooldownSabotaje) {
        this.botonSabotaje = botonSabotaje;
        this.textoCooldownSabotaje = textoCooldownSabotaje;
        this.sabotajeDisponible = true;
    }

    /**
     * Ciclo de actualización por frame del componente.
     * Se encarga de:
     * 1. Calcular la distancia euclidiana hacia todos los jugadores vivos para determinar una {@code victimaCercana}.
     * 2. Decrementar los temporizadores de recarga (cooldowns) basados en el TPF.
     * 3. Actualizar el estado visual de los botones (Habilitado/Deshabilitado) según la proximidad y el tiempo.
     * @param tpf Time Per Frame proporcionado por el motor FXGL.
     */
    @Override
    public void onUpdate(double tpf) {
        victimaCercana = "";
        double distanciaMinima = 30.0;

        if (!enAlcantarilla) {
            for (java.util.Map.Entry<String, Entity> entry : AppPrincipal.otrosJugadores.entrySet()) {
                Entity otroJugador = entry.getValue();
                double distancia = entity.getPosition().distance(otroJugador.getPosition());
                boolean estaVivo = !otroJugador.getComponent(AnimacionJugador.class).estaMuerto;
                boolean esCompanero = false;
                if (AppPrincipal.listaImpostores != null) {
                    for (String nombreCompanero : AppPrincipal.listaImpostores) {
                        if (nombreCompanero.equals(entry.getKey())) {
                            esCompanero = true;
                            break;
                        }
                    }
                }

                if (distancia < distanciaMinima && estaVivo && !esCompanero) {
                    distanciaMinima = distancia;
                    victimaCercana = entry.getKey();
                }
            }
        }

        if (cooldownActivo) {
            tiempoCooldown -= tpf;
            if (textoCooldown != null) {
                textoCooldown.setText(String.format("%.0f", tiempoCooldown));
            }

            if (tiempoCooldown <= 0) {
                cooldownActivo = false;
                matarDisponible = true;
                if (textoCooldown != null) textoCooldown.setText("");
            }
        }
        if (botonMatar != null) {
            if (!matarDisponible || victimaCercana.isEmpty()) {
                botonMatar.setImage(FXGL.image("matarNegado.png"));
            } else {
                botonMatar.setImage(FXGL.image("matar.png"));
            }
        }
        if (!sabotajeDisponible) {
            tiempoCooldownSabotaje -= tpf;
            if (textoCooldownSabotaje != null) {
                textoCooldownSabotaje.setText(String.format("%.0f", tiempoCooldownSabotaje));
            }

            if (tiempoCooldownSabotaje <= 0) {
                sabotajeDisponible = true;
                if (botonSabotaje != null) botonSabotaje.setImage(FXGL.image("sabotaje.png"));
                if (textoCooldownSabotaje != null) textoCooldownSabotaje.setText("");
            }
        }
    }

    /**
     * Ejecuta la lógica de asesinato si todas las condiciones (cooldown y proximidad) se cumplen.
     * Al matar:
     * Se activa la animación de muerte en la víctima localmente.
     * Se detiene el movimiento físico de la víctima.
     * Se envía un paquete {@link Asesinato} vía TCP para sincronizar con el servidor.
     * Se reinicia el cooldown global del impostor.
     */
    public void intentarMatar() {
        if (matarDisponible && !cooldownActivo && !victimaCercana.isEmpty()) {
            Entity victima = AppPrincipal.otrosJugadores.get(victimaCercana);

            if (victima != null) {
                victima.getComponent(AnimacionJugador.class).morir();
                if (victima.hasComponent(PhysicsComponent.class)) {
                    victima.getComponent(PhysicsComponent.class).setVelocityX(0);
                    victima.getComponent(PhysicsComponent.class).setVelocityY(0);
                }
            }
            Asesinato paquete = new Asesinato();
            paquete.asesino = MenuController.nombreUsuario;
            paquete.victima = victimaCercana;
            if (AppPrincipal.miCliente != null && AppPrincipal.miCliente.cliente != null) {
                AppPrincipal.miCliente.cliente.sendTCP(paquete);
            }

            victimaCercana = "";
            iniciarCooldown(30.0);
        }
    }

    /**
     * Activa el sabotaje de luces si está disponible.
     * Notifica a la aplicación principal para oscurecer el mapa y envía
     * la petición de sabotaje al servidor para afectar a los tripulantes.
     */
    public void intentarSabotaje() {
        if (sabotajeDisponible && !AppPrincipal.sabotajeActivo) {
            sabotajeDisponible = false;
            tiempoCooldownSabotaje = 60.0;
            if (botonSabotaje != null) botonSabotaje.setImage(FXGL.image("sabotajeNegado.png"));
            AppPrincipal.activarCorteElectrico();

            Sabotaje peticion = new Sabotaje();
            peticion.activar = true;
            if (AppPrincipal.miCliente != null && AppPrincipal.miCliente.cliente != null) {
                AppPrincipal.miCliente.cliente.sendTCP(peticion);
            }
        }
    }

    /**
     * Inicia el estado de espera (cooldown) para la acción de matar.
     * @param tiempo Segundos que debe esperar el jugador.
     */
    private void iniciarCooldown(double tiempo) {
        cooldownActivo = true;
        tiempoCooldown = tiempo;
        matarDisponible = false;
        if (botonMatar != null) botonMatar.setImage(FXGL.image("matarNegado.png"));
    }

    /**
     * Gestiona la entrada o salida de la red de alcantarillas.
     * Bloquea la acción si el sistema de cámaras está en uso.
     */
    public void alternarAlcantarilla() {
        if (AppPrincipal.sistemaCamaras.isCamarasAbiertas()) return;

        if (!enAlcantarilla) {
            entrarAlcantarilla();
        } else {
            salirAlcantarilla();
        }
    }

    /**
     * Busca la rejilla más cercana y oculta la entidad del jugador.
     * Al entrar, se detiene la física, se teletransporta al nodo exacto,
     * se reproduce la animación de entrada y se oculta la vista tras un breve retardo.
     * También notifica vía red que el jugador ha entrado en la ventilación.
     */
    private void entrarAlcantarilla() {
        NodoAlcantarilla nodoCercano = null;
        double distanciaMinima = 20.0;

        for (NodoAlcantarilla nodo : AppPrincipal.redAlcantarillas) {
            double distancia = entity.getPosition().distance(nodo.x, nodo.y);
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                nodoCercano = nodo;
            }
        }

        if (nodoCercano != null) {
            enAlcantarilla = true;
            alcantarillaActual = nodoCercano.id;

            entity.getComponent(PhysicsComponent.class).setVelocityX(0);
            entity.getComponent(PhysicsComponent.class).setVelocityY(0);
            entity.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(nodoCercano.x, nodoCercano.y));

            MovimientoAlcantarilla entrar = new MovimientoAlcantarilla();
            entrar.nombreUsuario = AppPrincipal.miCliente.username;
            entrar.entrando = true;
            AppPrincipal.miCliente.cliente.sendTCP(entrar);

            entity.getComponent(AnimacionJugador.class).entrarAlcantarilla();

            FXGL.getGameTimer().runOnceAfter(() -> {
                if (enAlcantarilla) entity.getViewComponent().setVisible(false);
            }, Duration.seconds(0.5));

            mostrarFlechasVents();
        }
    }

    /**
     * Expulsa al impostor de la alcantarilla actual, restaurando su visibilidad y física.
     */
    private void salirAlcantarilla() {
        alcantarillaActual = -1;
        ocultarFlechasVents();

        entity.getViewComponent().setVisible(true);
        entity.getComponent(AnimacionJugador.class).salirAlcantarilla();

        MovimientoAlcantarilla salir = new MovimientoAlcantarilla();
        salir.nombreUsuario = AppPrincipal.miCliente.username;
        salir.entrando = false;
        AppPrincipal.miCliente.cliente.sendTCP(salir);

        FXGL.getGameTimer().runOnceAfter(() -> enAlcantarilla = false, Duration.seconds(0.5));
    }

    /**
     * Realiza el desplazamiento instantáneo entre nodos de alcantarilla conectados.
     * @param direccion Cadena que define el sentido del viaje ("IZQ", "DER", "ARRIBA", "ABAJO").
     */
    public void viajarAlcantarilla(String direccion) {
        if (enAlcantarilla && alcantarillaActual != -1) {
            int destino = -1;
            switch(direccion) {
                case "IZQ": destino = AppPrincipal.redAlcantarillas.get(alcantarillaActual).ventIzquierda; break;
                case "DER": destino = AppPrincipal.redAlcantarillas.get(alcantarillaActual).ventDerecha; break;
                case "ARRIBA": destino = AppPrincipal.redAlcantarillas.get(alcantarillaActual).ventArriba; break;
                case "ABAJO": destino = AppPrincipal.redAlcantarillas.get(alcantarillaActual).ventAbajo; break;
            }

            if (destino != -1) {
                alcantarillaActual = destino;
                NodoAlcantarilla nuevoNodo = AppPrincipal.redAlcantarillas.get(destino);
                entity.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(nuevoNodo.x, nuevoNodo.y));
                entity.getViewComponent().setVisible(false);
                mostrarFlechasVents();
            }
        }
    }

    /**
     * Crea entidades visuales (flechas rojas) en el HUD para indicar caminos disponibles
     * en la red de ventilación desde el nodo actual.
     */
    private void mostrarFlechasVents() {
        ocultarFlechasVents();
        if (alcantarillaActual == -1) return;
        NodoAlcantarilla nodoActual = AppPrincipal.redAlcantarillas.get(alcantarillaActual);
        double centroX = nodoActual.x - 4;
        double centroY = nodoActual.y + 4;

        if (nodoActual.ventIzquierda != -1) {
            Texture texIzq = FXGL.texture("flechaRojaIzq.png", 40, 40);
            texIzq.setOnMouseClicked(e -> viajarAlcantarilla("IZQ"));
            ventflechaIzq = FXGL.entityBuilder().at(centroX - 45, centroY).view(texIzq).zIndex(2000).buildAndAttach();
        }
        if (nodoActual.ventDerecha != -1) {
            Texture texDer = FXGL.texture("flechaRojaDer.png", 40, 40);
            texDer.setOnMouseClicked(e -> viajarAlcantarilla("DER"));
            ventflechaDer = FXGL.entityBuilder().at(centroX + 53, centroY).view(texDer).zIndex(2000).buildAndAttach();
        }
        if (nodoActual.ventArriba != -1) {
            Texture texArriba = FXGL.texture("flechaRojaArriba.png", 40, 40);
            texArriba.setOnMouseClicked(e -> viajarAlcantarilla("ARRIBA"));
            ventflechaArriba = FXGL.entityBuilder().at(centroX + 4, centroY - 45).view(texArriba).zIndex(2000).buildAndAttach();
        }
        if (nodoActual.ventAbajo != -1) {
            Texture texAbajo = FXGL.texture("flechaRojaAbajo.png", 40, 40);
            texAbajo.setOnMouseClicked(e -> viajarAlcantarilla("ABAJO"));
            ventflechaAbajo = FXGL.entityBuilder().at(centroX + 4, centroY + 45).view(texAbajo).zIndex(2000).buildAndAttach();
        }
    }

    /**
     * Remueve todas las flechas de navegación del mundo de juego.
     */
    private void ocultarFlechasVents() {
        if (ventflechaIzq != null) { ventflechaIzq.removeFromWorld(); ventflechaIzq = null; }
        if (ventflechaDer != null) { ventflechaDer.removeFromWorld(); ventflechaDer = null; }
        if (ventflechaArriba != null) { ventflechaArriba.removeFromWorld(); ventflechaArriba = null; }
        if (ventflechaAbajo != null) { ventflechaAbajo.removeFromWorld(); ventflechaAbajo = null; }
    }

    /**
     * Consulta el estado de ocultamiento del impostor.
     * @return {@code true} si está oculto en la alcantarilla.
     */
    public boolean estaEnAlcantarilla() {
        return enAlcantarilla;
    }
}