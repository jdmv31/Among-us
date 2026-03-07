package main.java.amongUs;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;


// josue: este componente en particular se va a encargar de la logica de la vista del personaje
// siendo si esta muerto, es un tripulante o impostor

public class VisionComponent extends Component {

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

        // todos estos radios son los circulos de vision que tienen cada uno
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