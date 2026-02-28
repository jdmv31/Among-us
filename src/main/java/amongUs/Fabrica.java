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
import javafx.geometry.Point2D; // ✅ IMPORTANTE: Se necesita para posicionar el HitBox
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Fabrica implements EntityFactory {

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

    @Spawns("jugador")
    public Entity nuevoJugador(SpawnData data) {
        String nombre = data.hasKey("nombre") ? data.get("nombre") : "Jugador";
        Text nombreVisual = new Text(nombre);
        nombreVisual.setFill(Color.WHITE);
        nombreVisual.setFont(Font.font("Arial", 6));
        nombreVisual.setTranslateY(-1);
        nombreVisual.setTranslateX( (32 / 2.0) - (nombreVisual.getLayoutBounds().getWidth() / 2.0) );

        PhysicsComponent fisicasJugador = new PhysicsComponent();
        fisicasJugador.setBodyType(BodyType.DYNAMIC);

        double escala = 1.6;

        double posX = (32 / escala) / 2.0 - (20 / escala) / 2.0;
        double posY = (32 / escala) - (15 / escala);

        HitBox piesHitBox = new HitBox("pies", new Point2D(posX, posY), BoundingShape.box(20 / escala, 15 / escala));

        return FXGL.entityBuilder(data)
                .type(TipoEntidad.JUGADOR)
                .bbox(piesHitBox)
                .with(new CollidableComponent(true))
                .with(new AnimacionJugador())
                .with(fisicasJugador)
                .scale(escala, escala)
                .view(nombreVisual)
                .build();
    }
}
