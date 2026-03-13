package main.java.amongUs;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

/**
 * Componente visual que maneja la sombra u oscuridad de la pantalla simulando el campo de vista
 * Crea un efecto de linterna alrededor del personaje que se reduce o agranda segun las reglas del juego
 * Trabaja de la mano con {@link AppPrincipal}.
 * @author Angelo Martini
 * */
public class VisionComponent extends Component {

    /**
     * Se ejecuta en cada frame para recalcular donde debe estar el circulo transparente que nos deja ver
     * Revisa constantemente nuestro rol y estado para saber que tanto radio de vision darnos
     * @param tpf (time per frame) tiempo transcurrido desde el ultimo cuadro dibujado en pantalla
     * */
    @Override
    public void onUpdate(double tpf) {
        if (AppPrincipal.oscuridad == null) return;

        if (AppPrincipal.estoyMuerto) {
            AppPrincipal.oscuridad.setFill(Color.TRANSPARENT);
            return;
        }
        double zoom = FXGL.getGameScene().getViewport().getZoom();
        double screenX = (entity.getX() + 25 - FXGL.getGameScene().getViewport().getX()) * zoom;
        double screenY = (entity.getY() + 25 - FXGL.getGameScene().getViewport().getY()) * zoom;

        double centroX = screenX / FXGL.getAppWidth();
        double centroY = screenY / FXGL.getAppHeight();

        if (!AppPrincipal.esImpostor) {
            if (AppPrincipal.sabotajeActivo) {
                RadialGradient radioSabotaje = new RadialGradient(
                        0, 0, centroX, centroY, 0.5, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.TRANSPARENT),
                        new Stop(0.12, Color.TRANSPARENT),
                        new Stop(0.25, Color.rgb(10, 10, 10, 0.98)),
                        new Stop(1, Color.rgb(10, 10, 10, 1.0))
                );
                AppPrincipal.oscuridad.setFill(radioSabotaje);
            } else {
                RadialGradient radioNormal = new RadialGradient(
                        0, 0, centroX, centroY, 0.5, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.TRANSPARENT),
                        new Stop(0.15, Color.TRANSPARENT),
                        new Stop(0.45, Color.rgb(10, 10, 10, 0.6)),
                        new Stop(0.75, Color.rgb(10, 10, 10, 0.95)),
                        new Stop(1, Color.rgb(10, 10, 10, 0.98))
                );
                AppPrincipal.oscuridad.setFill(radioNormal);
            }
        } else {
            if (AppPrincipal.sabotajeActivo) {
                RadialGradient radioAlarma = new RadialGradient(
                        0, 0, centroX, centroY, 0.5, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.TRANSPARENT),
                        new Stop(0.15, Color.TRANSPARENT),
                        new Stop(0.45, Color.rgb(50, 10, 10, 0.6)),
                        new Stop(0.75, Color.rgb(50, 10, 10, 0.95)),
                        new Stop(1, Color.rgb(20, 0, 0, 0.98))
                );
                AppPrincipal.oscuridad.setFill(radioAlarma);
            } else {
                RadialGradient radioNormal = new RadialGradient(
                        0, 0, centroX, centroY, 0.5, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.TRANSPARENT),
                        new Stop(0.15, Color.TRANSPARENT),
                        new Stop(0.45, Color.rgb(10, 10, 10, 0.6)),
                        new Stop(0.75, Color.rgb(10, 10, 10, 0.95)),
                        new Stop(1, Color.rgb(10, 10, 10, 0.98))
                );
                AppPrincipal.oscuridad.setFill(radioNormal);
            }
        }
    }
}