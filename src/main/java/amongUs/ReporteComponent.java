package main.java.amongUs;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import java.util.Map;

public class ReporteComponent extends Component {
    private Texture botonReportar;
    private boolean reporteDisponible = false;
    private String cadaverCercano = "";
    private double distanciaDeteccion = 30.0;

    public void setBotonReportar(Texture botonReportar) {
        this.botonReportar = botonReportar;
        this.botonReportar.setImage(FXGL.image("reportarNegado.png"));
    }

    @Override
    public void onUpdate(double tpf) {
        if (AppPrincipal.estoyMuerto) return;
        boolean enCamaras = AppPrincipal.sistemaCamaras.isCamarasAbiertas();
        boolean enAlcantarilla = entity.hasComponent(ImpostorComponent.class) &&
                entity.getComponent(ImpostorComponent.class).estaEnAlcantarilla();
        boolean enMinijuego = entity.hasComponent(TripulanteComponent.class) &&
                entity.getComponent(TripulanteComponent.class).isEnMinijuego();

        if (enCamaras || enAlcantarilla || enMinijuego) {
            desactivarReporte();
            return;
        }

        boolean cercaDeCadaver = false;
        cadaverCercano = "";

        for (Map.Entry<String, Entity> entry : AppPrincipal.otrosJugadores.entrySet()) {
            Entity otro = entry.getValue();
            if (otro != null && otro.hasComponent(AnimacionJugador.class)) {
                if (otro.getComponent(AnimacionJugador.class).estaMuerto) {
                    if (entity.getPosition().distance(otro.getPosition()) < distanciaDeteccion) {
                        cercaDeCadaver = true;
                        cadaverCercano = entry.getKey();
                        break;
                    }
                }
            }
        }

        if (cercaDeCadaver) {
            activarReporte();
        } else {
            desactivarReporte();
        }
    }

    private void activarReporte() {
        if (!reporteDisponible && botonReportar != null) {
            botonReportar.setImage(FXGL.image("reportar.png"));
            reporteDisponible = true;
        }
    }

    private void desactivarReporte() {
        if (reporteDisponible && botonReportar != null) {
            botonReportar.setImage(FXGL.image("reportarNegado.png"));
            reporteDisponible = false;
            cadaverCercano = "";
        }
    }

    public void intentarReportar() {
        if (reporteDisponible && !AppPrincipal.estoyMuerto && !cadaverCercano.isEmpty()) {
            desactivarReporte();
            PeticionReunion peticion = new PeticionReunion();
            peticion.reportador = MenuController.nombreUsuario;
            peticion.cadaver = cadaverCercano;
            peticion.porBotonEmergencia = false;

            if (AppPrincipal.miCliente != null && AppPrincipal.miCliente.cliente != null) {
                AppPrincipal.miCliente.cliente.sendTCP(peticion);
            }
        }
    }
}