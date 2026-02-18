package main.java.amongUs;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;

public class AnimacionJugador extends Component {
    private AnimatedTexture textura;
    private AnimationChannel animIdle, animWalk;

    // Ahora declaramos tus dos imágenes por separado
    private static final String IMG_QUIETO = "tripulante_negro.png";
    private static final String IMG_CAMINAR = "animacion_negro.png";

    // IMPORTANTE: Asegúrate de que este sea el ancho y alto real de tu personaje.
    private static final int ANCHO_FRAME = 32;
    private static final int ALTO_FRAME = 48;

    private double lastX = 0;
    private double lastY = 0;

    public AnimacionJugador() {
        animIdle = new AnimationChannel(FXGL.image(IMG_QUIETO), 1, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(1.0), 0, 0);
        animWalk = new AnimationChannel(FXGL.image(IMG_CAMINAR), 4, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(0.6), 0, 3);

        textura = new AnimatedTexture(animIdle);
        textura.loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(textura);
        lastX = entity.getX();
        lastY = entity.getY();
    }

    @Override
    public void onUpdate(double tpf) {
        double currentX = entity.getX();
        double currentY = entity.getY();

        boolean isMoving = (currentX != lastX) || (currentY != lastY);
        if (currentX < lastX) {
            textura.setScaleX(1); // La imagen mira a la izquierda
        } else if (currentX > lastX) {
            textura.setScaleX(-1);  // La imagen mira a la derecha
        }

        if (isMoving) {
            if (textura.getAnimationChannel() != animWalk) {
                textura.loopAnimationChannel(animWalk);
            }
        } else {
            if (textura.getAnimationChannel() != animIdle) {
                textura.loopAnimationChannel(animIdle);
            }
        }

        lastX = currentX;
        lastY = currentY;
    }
}