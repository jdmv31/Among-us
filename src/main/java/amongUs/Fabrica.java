package main.java.amongUs;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Fábrica de entidades para el juego Among Us.
 * Implementa {@link EntityFactory} para centralizar la creación de todos los elementos
 * del mapa (techos, paredes, objetos) y los personajes. Utiliza anotaciones {@code @Spawns}
 * para vincular nombres de entidades definidos en archivos de mapa (como TMX) con
 * métodos de creación específicos.
 * @author Angel Aguilera
 */
public class Fabrica implements EntityFactory {

    /**
     * Crea una entidad de tipo "techo".
     * Se define como un cuerpo estático con colisiones. Al ser un techo,
     * no suele requerir ajustes de Z-Index para oclusión de personajes.
     * * @param data Datos de creación (posición, dimensiones).
     * @return Una entidad de tipo {@link TipoEntidad#PARED}.
     */
    @Spawns("techo")
    public Entity nuevoTecho(SpawnData data) {
        PhysicsComponent fisicasTecho = new PhysicsComponent();
        fisicasTecho.setBodyType(BodyType.STATIC);

        return FXGL.entityBuilder(data)
                .type(TipoEntidad.PARED)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .with(fisicasTecho)
                .build();
    }

    /**
     * Crea una entidad de tipo "pared".
     * Similar al techo, pero incluye un cálculo de {@code zIndex} basado en su
     * posición Y para permitir que los jugadores pasen por detrás o por delante
     * (Efecto Top-Down).
     * * @param data Datos de creación proporcionados por el motor.
     * @return Entidad estática colisionable.
     */
    @Spawns("pared")
    public Entity nuevaPared(SpawnData data) {
        PhysicsComponent fisicasPared = new PhysicsComponent();
        fisicasPared.setBodyType(BodyType.STATIC);

        return FXGL.entityBuilder(data)
                .type(TipoEntidad.PARED)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .with(fisicasPared)
                .zIndex((int) (data.getY() + data.<Integer>get("height")))
                .build();
    }

    /**
     * Genera objetos decorativos o funcionales en el mapa.
     * Al igual que las paredes, utiliza colisiones estáticas y profundidad dinámica (Z-Index).
     * * @param data Contiene parámetros como ancho y alto del objeto.
     * @return Entidad de tipo {@link TipoEntidad#OBJETO}.
     */
    @Spawns("objeto")
    public Entity nuevoObjeto(SpawnData data) {
        PhysicsComponent fisicasObjeto = new PhysicsComponent();
        fisicasObjeto.setBodyType(BodyType.STATIC);

        return FXGL.entityBuilder(data)
                .type(TipoEntidad.OBJETO)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .with(fisicasObjeto)
                .zIndex((int) (data.getY() + data.<Integer>get("height")))
                .build();
    }

    /**
     * Crea y configura la entidad del jugador, diferenciando entre el jugador local y remotos.
     * Este método realiza las siguientes tareas:
     * Recupera el color y el nombre desde el {@link MenuController}.
     * Configura la etiqueta de nombre visual sobre el personaje.
     * Si es el jugador local Añade componentes de física dinámica, visión e impostor.
     * Si es un jugador remoto: Solo define una caja de colisión básica.
     * * @param data Datos que pueden incluir el "nombre" del jugador.
     * @return Entidad del jugador configurada para el entorno multijugador.
     */
    @Spawns("jugador")
    public Entity nuevoJugador(SpawnData data) {
        String nombre = data.hasKey("nombre") ? data.get("nombre") : "Jugador";
        String nombreJugador = nombre;
        String colorJugador = "negro";
        boolean esLocal = false;

        if (MenuController.estadoActual != null) {
            for (JugadorLobby j : MenuController.estadoActual.jugadores) {
                if (j.nombre.equals(nombreJugador)) {
                    colorJugador = j.color;
                    if (j.nombre.equals(MenuController.nombreUsuario)) {
                        esLocal = true;
                    }
                    break;
                }
            }
        }

        Text nombreVisual = new Text(nombre);
        nombreVisual.setFill(Color.WHITE);
        nombreVisual.setFont(Font.font("Arial", 6));
        nombreVisual.setTranslateY(-1);
        nombreVisual.setTranslateX( (32 / 2.0) - (nombreVisual.getLayoutBounds().getWidth() / 2.0) );

        double escala = 1.6;
        double posX = (32 / escala) / 2.0 - (20 / escala) / 2.0;
        double posY = (32 / escala) - (15 / escala);
        HitBox piesHitBox = new HitBox("pies", new Point2D(posX, posY), BoundingShape.box(20 / escala, 15 / escala));

        var builder = FXGL.entityBuilder(data)
                .type(TipoEntidad.JUGADOR)
                .with(new AnimacionJugador(colorJugador))
                .scale(escala, escala)
                .view(nombreVisual);

        if (esLocal) {
            PhysicsComponent fisicasJugador = new PhysicsComponent();
            fisicasJugador.setBodyType(BodyType.DYNAMIC);

            builder.bbox(piesHitBox)
                    .with(new CollidableComponent(true))
                    .with(new VisionComponent())
                    .with(new ImpostorComponent())
                    .with(fisicasJugador);
        } else {
            builder.bbox(new HitBox("cuerpo", new Point2D(0, 0), BoundingShape.box(32, 32)));
        }

        return builder.build();
    }
}