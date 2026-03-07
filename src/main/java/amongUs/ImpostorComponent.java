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

public class ImpostorComponent extends Component {
    private boolean matarDisponible = false;
    private boolean cooldownActivo = false;
    private double tiempoCooldown = 0.0;
    private Texture botonMatar;
    private Text textoCooldown;
    private boolean enAlcantarilla = false;
    private int alcantarillaActual = -1;
    private Entity ventflechaIzq, ventflechaAbajo, ventflechaArriba, ventflechaDer;
    private Texture botonSabotaje;
    private Text textoCooldownSabotaje;
    private boolean sabotajeDisponible = true;
    private double tiempoCooldownSabotaje = 0.0;
    private String victimaCercana = "";


    public void setUIAsesinato(Texture botonMatar, Text textoCooldown) {
        this.botonMatar = botonMatar;
        this.textoCooldown = textoCooldown;
        iniciarCooldown(10.0);
    }

    public void setUISabotaje(Texture botonSabotaje, Text textoCooldownSabotaje) {
        this.botonSabotaje = botonSabotaje;
        this.textoCooldownSabotaje = textoCooldownSabotaje;
        this.sabotajeDisponible = true;
    }

    @Override
    public void onUpdate(double tpf) {
        victimaCercana = "";
        double distanciaMinima = 30.0;

        if (!enAlcantarilla) {
            for (Map.Entry<String, Entity> entry : AppPrincipal.otrosJugadores.entrySet()) {
                Entity otroJugador = entry.getValue();

                double distancia = entity.getPosition().distance(otroJugador.getPosition());

                boolean estaVivo = !otroJugador.getComponent(AnimacionJugador.class).estaMuerto;

                if (distancia < distanciaMinima && estaVivo) {
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

    public void intentarMatar() {
        if (matarDisponible && !cooldownActivo && !victimaCercana.isEmpty()) {
            System.out.println("¡Mataste a " + victimaCercana);
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

    private void iniciarCooldown(double tiempo) {
        cooldownActivo = true;
        tiempoCooldown = tiempo;
        matarDisponible = false;
        if (botonMatar != null) botonMatar.setImage(FXGL.image("matarNegado.png"));
    }

    public void alternarAlcantarilla() {
        if (AppPrincipal.sistemaCamaras.isCamarasAbiertas()) return;

        if (!enAlcantarilla) {
            entrarAlcantarilla();
        } else {
            salirAlcantarilla();
        }
    }

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

    private void ocultarFlechasVents() {
        if (ventflechaIzq != null) { ventflechaIzq.removeFromWorld(); ventflechaIzq = null; }
        if (ventflechaDer != null) { ventflechaDer.removeFromWorld(); ventflechaDer = null; }
        if (ventflechaArriba != null) { ventflechaArriba.removeFromWorld(); ventflechaArriba = null; }
        if (ventflechaAbajo != null) { ventflechaAbajo.removeFromWorld(); ventflechaAbajo = null; }
    }

    public boolean estaEnAlcantarilla() {
        return enAlcantarilla;
    }
}