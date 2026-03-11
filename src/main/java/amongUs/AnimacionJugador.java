package main.java.amongUs;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;


/**
*
* @author Josue Medina
* los datos a obtener para reproducir la animacion de cada personaje se obtiene de las siguientes clases
* {@link Fabrica} la cual obtiene la informacion proveniente de {@link MenuController}
* */

public class AnimacionJugador extends Component {
    private AnimatedTexture textura;
    /**
     * representa el molde para dibujar los personajes
     * */
    private AnimationChannel animIdle, animWalk;
    /**
    * atributos que contienen las animaciones en estado quieto y en movimiento
    * */
    private String color;
    /**
     * refleja el color del personaje para seleccionar adecuadamente su respectiva animacion
     * */
    private AnimationChannel salirAlcantarilla;
    /**
     * animacion de los impostores para salir de la alcantarilla, varia dependiendo del color
     * */
    private AnimationChannel entrarAlcantarilla;
    /**
     * animacion de los impostores para entrar a la alcantarilla, varia dependiendo del color
     * */
    public boolean enAlcantarilla = false;
    /**
     * indica si el impostor se encuentra dentro de la alcantarilla para reproducir su animacion de salida
     * */
    private AnimationChannel animMuerto;
    /**
     * carga el sprite de muerte de cada personaje, depende del color
     * */
    public boolean estaMuerto = false;
    /**
     * indica si el personaje esta muerto para cargar su respectiva animacion
     * */
    private AnimationChannel animFantasma;
    /**
     * muestra la animacion de movimiento del modo espectador
     * */
    public boolean esFantasma = false;
    /**
     * indica si fuimos expulsados o asesinados, nos volvemos espectadores siendo fantasmas
     * */
    private static final int ANCHO_FRAME = 32;
    private static final int ALTO_FRAME = 48;
    /**
     * constantes para reflejar las dimensiones de los spritesheets de las animaciones
     * */
    private double lastX = 0;
    private double lastY = 0;
    /**
     * almacenan la ultima posicion en que se encuentra el jugador
     * */


    /**
     * constructor sin parametros, por defecto se le asigna el color negro a todos los jugadores
     * */
    public AnimacionJugador() {
        this("negro");
    }

    /**
     * constructor con parametros, toma el color del jugador y se asegura de emplear su respectivas animaciones
     * @param color el parametro color refleja el color seleccionado para ese personaje
     * la construccion de esta clase proviene de la clase {@link Cliente}
     * */

    public AnimacionJugador(String color) {
        this.color = color;
        String imgQuieto = "tripulante_" + this.color + ".png";
        String imgCaminar = "animacion_" + this.color + ".png";
        String imgAlcantarillaSalida = "alcantarilla_" + this.color + ".png";
        String imgAlcantarillaEntrada = "alcantarilla_"+this.color + "E.png";
        String imgMuerto = this.color + "_muerto.png";
        animFantasma = new AnimationChannel(FXGL.image("animacion_fantasma.png"), 4, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(0.6), 0, 3);

        animMuerto = new AnimationChannel(FXGL.image(imgMuerto), 1, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(1.0), 0, 0);
        animIdle = new AnimationChannel(FXGL.image(imgQuieto), 1, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(1.0), 0, 0);
        animWalk = new AnimationChannel(FXGL.image(imgCaminar), 4, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(0.6), 0, 3);
        salirAlcantarilla = new AnimationChannel(FXGL.image(imgAlcantarillaSalida), 4, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(0.5), 0, 3);
        entrarAlcantarilla = new AnimationChannel(FXGL.image(imgAlcantarillaEntrada), 4, ANCHO_FRAME, ALTO_FRAME, Duration.seconds(0.5), 0, 3);

        textura = new AnimatedTexture(animIdle);
        textura.loop();
    }

    /**
     * obtiene el color del jugador
     * */
    public String getColor(){
        return color;
    }
    /**
     * si el jugador murio, procede a volverse espectador y se carga su respectiva animacion
     * */
    public void convertirFantasma() {
        esFantasma = true;
        estaMuerto = false;
        textura.loopAnimationChannel(animFantasma);
    }
    /**
     * si el jugador murio, su cuerpo procede a dejar un cuerpo y se muestra su animacion
     * */
    public void morir() {
        estaMuerto = true;
        textura.loopAnimationChannel(animMuerto);
    }
    /**
     * si el jugador es impostor y desea entrar en un alcantarilla se llama a su respectiva animacion
     * */
    public void entrarAlcantarilla() {
        enAlcantarilla = true;
        textura.playAnimationChannel(this.entrarAlcantarilla);
    }
    /**
     * si el jugador es impostor y desea salir de un alcantarilla se llama a su respectiva animacion
     * */
    public void salirAlcantarilla() {
        textura.playAnimationChannel(this.salirAlcantarilla);
        FXGL.getGameTimer().runOnceAfter(() -> {
            enAlcantarilla = false;
            textura.loopAnimationChannel(animIdle);
        }, javafx.util.Duration.seconds(0.5));
    }
    /**
     * metodo que obtiene la posicion central de la camara para centrar el sprite de los personajes
     * */
    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(textura);
        lastX = entity.getX();
        lastY = entity.getY();
    }
    /**
     * logica para mostrar adecuadamente las animaciones de los personajes en tiempo real
     * @param tpf (time per frame) representa el ultimo frame desde que se actualizo la pantalla
     * */
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