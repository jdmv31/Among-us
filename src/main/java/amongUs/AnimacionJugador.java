package main.java.amongUs;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;

public class AnimacionJugador extends Component {
    private AnimatedTexture textura;
    private AnimationChannel animIdle, animWalk;
    private String color;
    private AnimationChannel salirAlcantarilla;
    private AnimationChannel entrarAlcantarilla;
    public boolean enAlcantarilla = false;
    private AnimationChannel animMuerto;
    public boolean estaMuerto = false;
    private AnimationChannel animFantasma;
    public boolean esFantasma = false;

    private static final int ANCHO_FRAME = 32;
    private static final int ALTO_FRAME = 48;

    private double lastX = 0;
    private double lastY = 0;

    public AnimacionJugador() {
        this("negro");
    }

    public AnimacionJugador(String color) {
        this.color = color;
        String imgQuieto = "tripulante_" + this.color + ".png";
        String imgCaminar = "animacion_" + this.color + ".png";
        String imgAlcantarillaSalida = "alcantarilla_" + this.color + ".png";
        String imgAlcantarillaEntrada = "alcantarilla_"+this.color + "E.png";
        String imgMuerto = this.color + "_muerto.png";

        animFantasma = new AnimationChannel(FXGL.image("fantasma.png"), 1, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(1.0), 0, 0);
        animMuerto = new AnimationChannel(FXGL.image(imgMuerto), 1, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(1.0), 0, 0);
        animIdle = new AnimationChannel(FXGL.image(imgQuieto), 1, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(1.0), 0, 0);
        animWalk = new AnimationChannel(FXGL.image(imgCaminar), 4, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(0.6), 0, 3);
        salirAlcantarilla = new AnimationChannel(FXGL.image(imgAlcantarillaSalida), 4, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(0.5), 0, 3);
        entrarAlcantarilla = new AnimationChannel(FXGL.image(imgAlcantarillaEntrada), 4, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(0.5), 0, 3);

        textura = new AnimatedTexture(animIdle);
        textura.loop();
    }

    public void convertirFantasma() {
        esFantasma = true;
        estaMuerto = false;
        textura.loopAnimationChannel(animFantasma);
    }

    public String getColor(){
        return color;
    }

    public void morir() {
        estaMuerto = true;
        textura.loopAnimationChannel(animMuerto);
    }

    public void entrarAlcantarilla() {
        enAlcantarilla = true;
        textura.playAnimationChannel(this.entrarAlcantarilla);
    }

    public void salirAlcantarilla() {
        textura.playAnimationChannel(this.salirAlcantarilla);
        FXGL.getGameTimer().runOnceAfter(() -> {
            enAlcantarilla = false;
            textura.loopAnimationChannel(animIdle);
        }, javafx.util.Duration.seconds(0.5));
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(textura);
        lastX = entity.getX();
        lastY = entity.getY();
    }

    @Override
    public void onUpdate(double tpf) {
        if (estaMuerto) return;

        if (enAlcantarilla) {
            lastX = entity.getX();
            lastY = entity.getY();
            return;
        }
        double currentX = entity.getX();
        double currentY = entity.getY();

        boolean isMoving = (currentX != lastX) || (currentY != lastY);

        if (currentX < lastX) {
            textura.setScaleX(1); // La imagen mira a la izquierda
        } else if (currentX > lastX) {
            textura.setScaleX(-1);  // La imagen mira a la derecha
        }

        if (isMoving) {
            if (esFantasma) {
                if (textura.getAnimationChannel() != animFantasma) textura.loopAnimationChannel(animFantasma);
            } else {
                if (textura.getAnimationChannel() != animWalk) textura.loopAnimationChannel(animWalk);
            }
        } else {
            if (esFantasma) {
                if (textura.getAnimationChannel() != animFantasma) textura.loopAnimationChannel(animFantasma);
            } else {
                if (textura.getAnimationChannel() != animIdle) textura.loopAnimationChannel(animIdle);
            }
        }

        lastX = currentX;
        lastY = currentY;
    }
}